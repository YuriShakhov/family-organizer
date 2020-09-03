package online.litterae.familyorganizer.implementation.notifications

import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManagerInterface
import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManagerInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.implementation.family.FamilyContract
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup

interface NotificationsContract {
    interface View: BaseViewInterface {
    }

    interface Presenter: BasePresenterInterface<NotificationsContract.View> {
        fun getNotifications(): ArrayList<Notification>
        fun acceptInvitation(notification: ReceivedInvitationNotification)
        fun declineInvitation(notification: ReceivedInvitationNotification)
    }

    interface SqliteManager: BaseSqliteManagerInterface<NotificationsContract.Presenter> {
        suspend fun addGroupToSQLite(invitation: Invitation)
        suspend fun addFriendsToSQLite(invitation: Invitation)
        suspend fun changeInvitationStatusToAccepted(invitation: Invitation)
        suspend fun changeInvitationStatusToDeclined(invitation: Invitation)
    }

    interface FirebaseManager: BaseFirebaseManagerInterface<NotificationsContract.Presenter> {
        fun addMeToFireBaseGroup(invitation: Invitation)
        fun changeInvitationStatusToAccepted(invitation: Invitation)
        fun changeInvitationStatusToDeclined(invitation: Invitation)
    }
}