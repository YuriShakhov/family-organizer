package online.litterae.familyorganizer.implementation.singlechat

import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManagerInterface
import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManagerInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.implementation.notifications.Notification
import online.litterae.familyorganizer.implementation.notifications.NotificationsContract
import online.litterae.familyorganizer.implementation.notifications.ReceivedInvitationNotification
import online.litterae.familyorganizer.sqlite.MyFriend
import java.util.*

interface ChatContract {
    interface View: BaseViewInterface {
        fun getFriendFromIntent(): MyFriend
        fun showFriend(myFriend: MyFriend)
        fun showMessages(messages: List<Message>, myFirebaseKey: String, friendFirebaseKey: String)
    }

    interface Presenter: BasePresenterInterface<ChatContract.View> {
        //from view
        fun setDataInView()
        fun sendMessage(text: String, date: Date)

        //from firebaseManager
        fun updateMessages(messages: List<Message>)
    }

    interface SqliteManager: BaseSqliteManagerInterface<ChatContract.Presenter> {
        suspend fun getMessages(myFriend: MyFriend): List<Message>
        suspend fun updateMessages(myFriend: MyFriend, messages: List<Message>)
    }

    interface FirebaseManager: BaseFirebaseManagerInterface<ChatContract.Presenter> {
        fun addMessageToFirebase(message: Message)
    }
}