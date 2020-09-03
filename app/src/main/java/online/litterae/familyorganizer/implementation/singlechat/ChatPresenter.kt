package online.litterae.familyorganizer.implementation.singlechat

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.litterae.familyorganizer.abstracts.presenter.BasePresenter
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.Email
import online.litterae.familyorganizer.firebase.FirebaseKey
import online.litterae.familyorganizer.sqlite.MyFriend
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ChatPresenter(val friendFirebaseKey: String)
    : BasePresenter<ChatContract.View>(), ChatContract.Presenter {
    @Inject lateinit var email: Email
    @Inject lateinit var firebaseKey: FirebaseKey
    @Inject lateinit var sqliteManager: ChatContract.SqliteManager
    @Inject lateinit var firebaseManager: ChatContract.FirebaseManager

    var myFriend: MyFriend? = null

    override fun init() {
        val chatModule = ChatModule(friendFirebaseKey)
        MainApplication.getAppComponent()
            .createPageComponent()
            .createSingleChatComponent(chatModule)
            .inject(this)

        myFriend = view?.getFriendFromIntent()
        Log.d(TAG, "ChatPresenter init. view: $view")
        Log.d(TAG, "ChatPresenter init. myFriend: ${myFriend?.name}")

        sqliteManager.attach(this)
        firebaseManager.attach(this)
    }

    override fun setDataInView() {
        setFriendInView()
        setMessagesInView()
    }

    override fun sendMessage(text: String, date: Date) {
        val sender = firebaseKey.toString()
        val dateJson = Gson().toJson(date)
        val message = Message(text, sender, dateJson)
        firebaseManager.addMessageToFirebase(message)
    }

    override fun updateMessages(messages: List<Message>) {
        view?.showMessages(messages, firebaseKey.toString(), friendFirebaseKey)
        myFriend?.let {
            CoroutineScope(Default).launch {
                sqliteManager.updateMessages(myFriend!!, messages)
            }
        }
    }

    fun setFriendInView() {
        myFriend?.let {
            view?.showFriend(it)
        }
    }

    fun setMessagesInView() {
        CoroutineScope(Default).launch {
            myFriend?.let {
                val messages = sqliteManager.getMessages(myFriend!!)
                withContext(Main){
                    view?.showMessages(messages, firebaseKey.toString(), friendFirebaseKey)
                }
            }
        }
    }
}