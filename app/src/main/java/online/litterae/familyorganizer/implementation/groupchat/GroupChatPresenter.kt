package online.litterae.familyorganizer.implementation.groupchat

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import online.litterae.familyorganizer.abstracts.presenter.BasePresenter
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.FirebaseKey
import online.litterae.familyorganizer.implementation.singlechat.Message
import online.litterae.familyorganizer.sqlite.MyGroup
import java.util.*
import javax.inject.Inject

class GroupChatPresenter (val myGroup: MyGroup)
    : BasePresenter<GroupChatContract.View>(), GroupChatContract.Presenter{
    @Inject lateinit var myFirebaseKey: FirebaseKey
    @Inject lateinit var sqliteManager: GroupChatContract.SqliteManager
    @Inject lateinit var firebaseManager: GroupChatContract.FirebaseManager

    override fun init() {
        val groupChatModule = GroupChatModule(myGroup)
        MainApplication.getAppComponent()
            .createPageComponent()
            .createGroupChatComponent(groupChatModule)
            .inject(this)

        sqliteManager.attach(this)
        firebaseManager.attach(this)
    }

    override fun setDataInView() {
        setGroupInView()
        setMessagesInView()
    }

    private fun setGroupInView(){
        view?.showGroup(myGroup)
    }

    private fun setMessagesInView(){
        CoroutineScope(Default).launch {
            val messages = sqliteManager.getMessages()
            withContext(Dispatchers.Main){
                view?.showMessages(messages, myFirebaseKey.toString())
            }
        }
    }

    override fun sendMessage(text: String, date: Date) {
        val sender = myFirebaseKey.toString()
        val dateJson = Gson().toJson(date)
        val message = Message(text, sender, dateJson)
        firebaseManager.addMessageToFirebase(message)
    }

    override fun updateMessages(messages: List<Message>) {
        view?.showMessages(messages, myFirebaseKey.toString())
        CoroutineScope(Default).launch {
            sqliteManager.updateMessages(messages)
        }
    }
}