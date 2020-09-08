package online.litterae.familyorganizer.implementation.groupchat

import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManagerInterface
import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManagerInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.implementation.singlechat.ChatContract
import online.litterae.familyorganizer.implementation.singlechat.Message
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup
import java.util.*

interface GroupChatContract {
    interface View: BaseViewInterface {
        fun getGroupFromIntent(): MyGroup
        fun showGroup(myGroup: MyGroup)
        fun showMessages(messages: List<Message>, myFirebaseKey: String)
    }

    interface Presenter: BasePresenterInterface<GroupChatContract.View> {
        //from view
        fun setDataInView()
        fun sendMessage(text: String, date: Date)

        //from firebaseManager
        fun updateMessages(messages: List<Message>)
    }

    interface SqliteManager: BaseSqliteManagerInterface<GroupChatContract.Presenter> {
        suspend fun getMessages(): List<Message>
        suspend fun updateMessages(messages: List<Message>)
    }

    interface FirebaseManager: BaseFirebaseManagerInterface<GroupChatContract.Presenter> {
        fun addMessageToFirebase(message: Message)
    }
}