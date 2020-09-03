package online.litterae.familyorganizer.implementation.singlechat

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat.*
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.BaseCompatActivity
import online.litterae.familyorganizer.application.Const
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.dagger.PageComponent
import online.litterae.familyorganizer.dagger.SingleChatComponent
import online.litterae.familyorganizer.implementation.family.FamilyActivity
import online.litterae.familyorganizer.implementation.notifications.NotificationsContract
import online.litterae.familyorganizer.sqlite.MyFriend
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ChatActivity : BaseCompatActivity(), ChatContract.View {
    @Inject
    lateinit var presenter : ChatContract.Presenter
    lateinit var adapter: MessageAdapter
    lateinit var myFirebaseKey: String
    lateinit var friendFirebaseKey: String

    var messagesList : List<Message> = ArrayList()

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_chat)
        setRecyclerView()
        val chatModule : ChatModule = ChatModule(getFriendFromIntent().userFirebaseKey)
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
        val messagesRecycler : RecyclerView = findViewById(R.id.rv_messages)
        messagesRecycler.setHasFixedSize(true)
        messagesRecycler.setLayoutManager(LinearLayoutManager(this))
        adapter = MessageAdapter()
        messagesRecycler.setAdapter(adapter)
    }

    override fun showMessages(messages: List<Message>, myFirebaseKey: String, friendFirebaseKey: String) {
        this.myFirebaseKey = myFirebaseKey
        this.friendFirebaseKey = friendFirebaseKey
        messagesList = messages
        adapter.notifyDataSetChanged()
    }

    override fun getFriendFromIntent(): MyFriend {
        val myFriendJson = intent.getStringExtra("myFriend")
        Log.d(Const.TAG, "getFriendFromIntent. myFriendJson: $myFriendJson")
        val myFriend = Gson().fromJson(myFriendJson, MyFriend::class.java)
        Log.d(Const.TAG, "getFriendFromIntent. myFriend: ${myFriend.name}")
        return myFriend
    }

    override fun showFriend(myFriend: MyFriend) {
        tv_friend_name.text = myFriend.name
    }

    fun sendMessage(view: View) {
        val text = et_message.text.toString()
        val date = Date()
        presenter.sendMessage(text, date)
        et_message.setText("")
    }

    inner class MessageAdapter : RecyclerView.Adapter<MessageViewHolder>() {
        val dateManager = DateManager()
        val gson = Gson()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false))
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val message = messagesList[position]
            holder.messageText.text = message.text
            holder.messageDate.text = dateManager.formatDate(gson.fromJson(message.date, Date::class.java))
            when (message.sender) {
                myFirebaseKey -> {
                    holder.marginLeft.visibility = View.INVISIBLE
                    holder.marginRight.visibility = View.GONE
                    holder.messageBody.setBackgroundColor(Color.rgb(179, 255, 153))
                }
                friendFirebaseKey -> {
                    holder.marginLeft.visibility = View.GONE
                    holder.marginRight.visibility = View.INVISIBLE
                    holder.messageBody.setBackgroundColor(Color.WHITE)
                }
            }
        }

        override fun getItemCount(): Int = messagesList.size
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageBody : CardView = itemView.findViewById(R.id.message_body)
        val messageText : TextView = itemView.findViewById(R.id.tv_message_text)
        val messageDate : TextView = itemView.findViewById(R.id.tv_message_date)
        val marginLeft : TextView = itemView.findViewById(R.id.message_margin_left)
        val marginRight : TextView = itemView.findViewById(R.id.message_margin_right)
    }
}