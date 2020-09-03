package online.litterae.familyorganizer.implementation.notifications

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.implementation.family.FamilyContract
import javax.inject.Inject

class NotificationsPresenter : PagePresenter<NotificationsContract.View>(), NotificationsContract.Presenter {
    @Inject
    lateinit var sqliteManager: NotificationsContract.SqliteManager
    @Inject
    lateinit var firebaseManager: NotificationsContract.FirebaseManager

    override fun init() {
        MainApplication.createPageComponent().inject(this)
        super.init()
    }

    override fun setNotifications(number: Int) {
    }

    override fun getNotifications() = notificationsHolder.notificationsList

    override fun acceptInvitation(notification: ReceivedInvitationNotification) {
        Log.d(TAG, "acceptInvitation: start")
        val invitation = notification.invitation
        firebaseManager.addMeToFireBaseGroup(invitation)
        firebaseManager.changeInvitationStatusToAccepted(invitation)
        CoroutineScope(Dispatchers.Default).launch {
            sqliteManager.addGroupToSQLite(invitation)
            sqliteManager.changeInvitationStatusToAccepted(invitation)
        }
        notificationsHolder.changeNotificationStatusToAccepted(notification)
        Log.d(TAG, "acceptInvitation: OK")
    }

    override fun declineInvitation(notification: ReceivedInvitationNotification) {
        Log.d(TAG, "declineInvitation: start")
        val invitation = notification.invitation
        firebaseManager.changeInvitationStatusToDeclined(invitation)
        notificationsHolder.changeNotificationStatusToDeclined(notification)
        CoroutineScope(Dispatchers.Default).launch {
            sqliteManager.changeInvitationStatusToDeclined(invitation)
        }
        Log.d(TAG, "declineInvitation: OK")
    }
}