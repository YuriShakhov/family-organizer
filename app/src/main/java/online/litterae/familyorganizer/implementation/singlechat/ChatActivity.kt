package online.litterae.familyorganizer.implementation.singlechat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat.*
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.BaseCompatActivity
import online.litterae.familyorganizer.application.Const.Companion.KEY_MY_FRIEND
import online.litterae.familyorganizer.application.Const.Companion.TYPE_MESSAGE_RECEIVED
import online.litterae.familyorganizer.application.Const.Companion.TYPE_MESSAGE_SENT
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.sqlite.MyFriend
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ChatActivity : BaseCompatActivity(), ChatContract.View {
    @Inject
    lateinit var presenter : ChatContract.Presenter

    private lateinit var messagesRecycler : RecyclerView
    private lateinit var adapter: MessageAdapter

    lateinit var myFirebaseKey: String
    private lateinit var friendFirebaseKey: String

    var messagesList : List<Message> = ArrayList()

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_chat)
        setRecyclerView()
        val chatModule = ChatModule(getFriendFromIntent())
        MainApplication.getAppComponent()
            .createPageComponent()
            .createSingleChatComponent(chatModule)
            .inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
        presenter.setDataInView()
    }

    private fun setRecyclerView() {
        messagesRecycler = findViewById(R.id.rv_messages)
        messagesRecycler.setHasFixedSize(true)
        messagesRecycler.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter()
        messagesRecycler.adapter = adapter
        messagesRecycler.addOnLayoutChangeListener{
                _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                scrollToBottom()
            }
        }
    }

    private fun scrollToBottom() {
        val count = adapter.itemCount
        messagesRecycler.postDelayed({
            if (count > 0) {
                messagesRecycler.smoothScrollToPosition(
                    count - 1)
            }
        }, 100)
    }

    override fun showMessages(messages: List<Message>, myFirebaseKey: String, myFriend: MyFriend) {
        this.myFirebaseKey = myFirebaseKey
        this.friendFirebaseKey = myFriend.userFirebaseKey
        messagesList = messages
        adapter.notifyDataSetChanged()
        scrollToBottom()
    }

    override fun getFriendFromIntent(): MyFriend {
        val myFriendJson = intent.getStringExtra(KEY_MY_FRIEND)
        return Gson().fromJson(myFriendJson, MyFriend::class.java)
    }

    override fun showFriend(myFriend: MyFriend) {
        tv_friend_name.text = myFriend.name
    }

    fun sendMessage(view: View) {
        val text = et_message.text.toString()
        val date = Date()
        presenter.sendMessage(text, date)
        et_message.setText("")
        scrollToBottom()
    }

    inner class MessageAdapter : RecyclerView.Adapter<MessageViewHolder>() {
        override fun getItemViewType(position: Int): Int {
            val message = messagesList[position]
            return if (message.sender == myFirebaseKey)
                TYPE_MESSAGE_SENT
            else
                TYPE_MESSAGE_RECEIVED
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            return if (viewType == TYPE_MESSAGE_SENT) {
                MessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_sent, parent, false)
                )
            } else {
                MessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_received, parent, false)
                )
            }
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val message = messagesList[position]
            holder.bind(message)
        }

        override fun getItemCount(): Int = messagesList.size
    }

    open class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateManager = DateManager()
        val gson = Gson()

        private val messageBody : TextView = itemView.findViewById(R.id.tv_message_body)
        private val messageDate : TextView = itemView.findViewById(R.id.tv_message_time)

        fun bind(message: Message) {
            messageBody.text = message.text
            messageDate.text = dateManager.formatDate(gson.fromJson(message.date, Date::class.java))
        }
    }
}