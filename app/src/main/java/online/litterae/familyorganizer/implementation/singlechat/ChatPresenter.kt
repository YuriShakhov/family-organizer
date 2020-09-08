package online.litterae.familyorganizer.implementation.singlechat

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.litterae.familyorganizer.abstracts.presenter.BasePresenter
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.Email
import online.litterae.familyorganizer.firebase.FirebaseKey
import online.litterae.familyorganizer.sqlite.MyFriend
import java.util.*
import javax.inject.Inject

class ChatPresenter(val myFriend: MyFriend)
    : BasePresenter<ChatContract.View>(), ChatContract.Presenter {
    @Inject lateinit var email: Email
    @Inject lateinit var firebaseKey: FirebaseKey
    @Inject lateinit var sqliteManager: ChatContract.SqliteManager
    @Inject lateinit var firebaseManager: ChatContract.FirebaseManager

    override fun init() {
        val chatModule = ChatModule(myFriend)
        MainApplication.getAppComponent()
            .createPageComponent()
            .createSingleChatComponent(chatModule)
            .inject(this)

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
        view?.showMessages(messages, firebaseKey.toString(), myFriend)
        CoroutineScope(Default).launch {
            sqliteManager.updateMessages(messages)
        }
    }

    private fun setFriendInView() {
        view?.showFriend(myFriend)
    }

    private fun setMessagesInView() {
        CoroutineScope(Default).launch {
            val messages = sqliteManager.getMessages()
            withContext(Main){
                view?.showMessages(messages, firebaseKey.toString(), myFriend)
            }
        }
    }
}