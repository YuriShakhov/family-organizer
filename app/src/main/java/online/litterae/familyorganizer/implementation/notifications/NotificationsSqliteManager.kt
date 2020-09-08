package online.litterae.familyorganizer.implementation.notifications

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManager
import online.litterae.familyorganizer.application.Const.Companion.STATUS_ACCEPTED
import online.litterae.familyorganizer.application.Const.Companion.STATUS_DECLINED
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.sqlite.MyGroup
import online.litterae.familyorganizer.sqlite.MyReceivedInvitation

class NotificationsSqliteManager
    : BaseSqliteManager<NotificationsContract.Presenter>(), NotificationsContract.SqliteManager {
    override fun init() {
        MainApplication.createPageComponent().inject(this)
    }

    override suspend fun addGroupToSQLite(invitation: Invitation) {
        CoroutineScope(Dispatchers.Default).launch {
            val myGroup = MyGroup()
            myGroup.name = invitation.groupName
            myGroup.groupFirebaseKey = invitation.groupFirebaseKey
            myGroupDao.insert(myGroup)
        }
    }

    override suspend fun addFriendsToSQLite(invitation: Invitation) {
    }

    override suspend fun changeInvitationStatusToAccepted(invitation: Invitation) {
        val myReceivedInvitation = MyReceivedInvitation()
        myReceivedInvitation.email = invitation.senderEmail
        myReceivedInvitation.groupName = invitation.groupName
        myReceivedInvitation.groupFirebaseKey = invitation.groupFirebaseKey
        myReceivedInvitation.message = invitation.message
        myReceivedInvitation.status = STATUS_ACCEPTED
        myReceivedInvitation.invitationFirebaseKey = invitation.invitationFirebaseKey
        myReceivedInvitationDao.update(myReceivedInvitation)
    }

    override suspend fun changeInvitationStatusToDeclined(invitation: Invitation) {
        val myReceivedInvitation = MyReceivedInvitation()
        myReceivedInvitation.email = invitation.senderEmail
        myReceivedInvitation.groupName = invitation.groupName
        myReceivedInvitation.groupFirebaseKey = invitation.groupFirebaseKey
        myReceivedInvitation.message = invitation.message
        myReceivedInvitation.status = STATUS_DECLINED
        myReceivedInvitation.invitationFirebaseKey = invitation.invitationFirebaseKey
        myReceivedInvitationDao.update(myReceivedInvitation)
    }
}