package online.litterae.familyorganizer.implementation.notifications

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.application.MainApplication
import javax.inject.Inject

class NotificationsPresenter : PagePresenter<NotificationsContract.View>(), NotificationsContract.Presenter {
    @Inject
    lateinit var sqliteManager: NotificationsContract.SqliteManager
    @Inject
    lateinit var firebaseManager: NotificationsContract.FirebaseManager

    override fun init() {
        MainApplication.createPageComponent().inject(this)
    }

    override fun setNotifications(count: Int) {
    }

    override fun getNotifications() = notificationsHolder.notificationsList

    override fun acceptInvitation(notification: ReceivedInvitationNotification) {
        val invitation = notification.invitation
        firebaseManager.addMeToFireBaseGroup(invitation)
        firebaseManager.changeInvitationStatusToAccepted(invitation)
        CoroutineScope(Dispatchers.Default).launch {
            sqliteManager.addGroupToSQLite(invitation)
            sqliteManager.changeInvitationStatusToAccepted(invitation)
        }
        notificationsHolder.changeNotificationStatusToAccepted(notification)
    }

    override fun declineInvitation(notification: ReceivedInvitationNotification) {
        val invitation = notification.invitation
        firebaseManager.changeInvitationStatusToDeclined(invitation)
        notificationsHolder.changeNotificationStatusToDeclined(notification)
        CoroutineScope(Dispatchers.Default).launch {
            sqliteManager.changeInvitationStatusToDeclined(invitation)
        }
    }
}