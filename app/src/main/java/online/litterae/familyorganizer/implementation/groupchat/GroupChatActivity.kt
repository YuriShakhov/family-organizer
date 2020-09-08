package online.litterae.familyorganizer.implementation.groupchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_group_chat.*
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.BaseCompatActivity
import online.litterae.familyorganizer.application.Const.Companion.KEY_MY_GROUP
import online.litterae.familyorganizer.application.Const.Companion.TYPE_MESSAGE_RECEIVED
import online.litterae.familyorganizer.application.Const.Companion.TYPE_MESSAGE_SENT
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.implementation.singlechat.DateManager
import online.litterae.familyorganizer.implementation.singlechat.Message
import online.litterae.familyorganizer.sqlite.MyGroup
import java.util.*
import javax.inject.Inject

class GroupChatActivity : BaseCompatActivity(), GroupChatContract.View {
    @Inject
    lateinit var presenter : GroupChatContract.Presenter

    lateinit var myFirebaseKey: String
    lateinit var groupFirebaseKey: String

    private lateinit var groupMessagesRecycler : RecyclerView
    private lateinit var adapter: GroupMessageAdapter

    var groupMessagesList : List<Message> = ArrayList()

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_group_chat)
        setRecyclerView()
        val groupChatModule = GroupChatModule(getGroupFromIntent())
        MainApplication.getAppComponent()
            .createPageComponent()
            .createGroupChatComponent(groupChatModule)
            .inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
        presenter.setDataInView()
    }

    private fun setRecyclerView() {
        groupMessagesRecycler = findViewById(R.id.rv_messages)
        groupMessagesRecycler.setHasFixedSize(true)
        groupMessagesRecycler.layoutManager = LinearLayoutManager(this)
        adapter = GroupMessageAdapter()
        groupMessagesRecycler.adapter = adapter
        groupMessagesRecycler.addOnLayoutChangeListener{
                _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                scrollToBottom()
            }
        }
    }

    override fun getGroupFromIntent(): MyGroup {
        val myGroupJson = intent.getStringExtra(KEY_MY_GROUP)
        return Gson().fromJson(myGroupJson, MyGroup::class.java)
    }

    override fun showGroup(myGroup: MyGroup) {
        tv_group_name.text = myGroup.name
    }

    override fun showMessages(messages: List<Message>, myFirebaseKey: String) {
        this.myFirebaseKey = myFirebaseKey
        groupMessagesList = messages
        adapter.notifyDataSetChanged()
    }

    private fun scrollToBottom() {
        val count = adapter.itemCount
        groupMessagesRecycler.postDelayed({
            if (count > 0) {
                groupMessagesRecycler.smoothScrollToPosition(
                    count - 1)
            }
        }, 100)
    }


    fun sendMessage(view: View) {
        val text = et_message.text.toString()
        val date = Date()
        presenter.sendMessage(text, date)
        et_message.setText("")
        scrollToBottom()
    }

    inner class GroupMessageAdapter : RecyclerView.Adapter<GroupMessageViewHolder>() {
        override fun getItemViewType(position: Int): Int {
            val message = groupMessagesList[position]
            return if (message.sender == myFirebaseKey)
                TYPE_MESSAGE_SENT
            else
                TYPE_MESSAGE_RECEIVED
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMessageViewHolder {
            return if (viewType == TYPE_MESSAGE_SENT) {
                SentGroupMessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.group_message_sent, parent, false)
                )
            } else {
                ReceivedGroupMessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.group_message_received, parent, false)
                )
            }
        }

        override fun onBindViewHolder(holder: GroupMessageViewHolder, position: Int) {
            val message = groupMessagesList[position]
            holder.bind(message)
        }

        override fun getItemCount(): Int = groupMessagesList.size
    }

    abstract class GroupMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateManager = DateManager()
        val gson = Gson()
        abstract fun bind(message: Message)
    }

    class SentGroupMessageViewHolder(itemView: View) : GroupMessageViewHolder(itemView) {
        private val messageBody : TextView = itemView.findViewById(R.id.tv_message_body)
        private val messageDate : TextView = itemView.findViewById(R.id.tv_message_time)

        override fun bind(message: Message) {
            messageBody.text = message.text
            messageDate.text = dateManager.formatDate(gson.fromJson(message.date, Date::class.java))
        }
    }

    class ReceivedGroupMessageViewHolder(itemView: View) : GroupMessageViewHolder(itemView) {
        private val messageBody : TextView = itemView.findViewById(R.id.tv_message_body)
        private val messageDate : TextView = itemView.findViewById(R.id.tv_message_time)
        private val userName : TextView = itemView.findViewById(R.id.user_name)

        override fun bind(message: Message) {
            userName.text = message.sender
            messageBody.text = message.text
            messageDate.text = dateManager.formatDate(gson.fromJson(message.date, Date::class.java))
        }
    }
}