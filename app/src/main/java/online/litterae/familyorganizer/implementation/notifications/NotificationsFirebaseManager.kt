package online.litterae.familyorganizer.implementation.notifications

import android.util.Log
import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManager
import online.litterae.familyorganizer.application.Const.Companion.STATUS_ACCEPTED
import online.litterae.familyorganizer.application.Const.Companion.STATUS_DECLINED
import online.litterae.familyorganizer.application.Const.Companion.TABLE_GROUPS
import online.litterae.familyorganizer.application.Const.Companion.TABLE_INVITATIONS
import online.litterae.familyorganizer.application.Const.Companion.TABLE_USERS
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.Invitation
import java.lang.Exception

class NotificationsFirebaseManager : BaseFirebaseManager<NotificationsContract.Presenter>(), NotificationsContract.FirebaseManager {
    override fun init() {
        MainApplication.createPageComponent().inject(this)
    }

    override fun addMeToFireBaseGroup(invitation: Invitation) {
        dbReference.updateChildren(
            mapOf<String, String>(
                "/$TABLE_GROUPS/${invitation.groupFirebaseKey}/$TABLE_USERS/${firebaseKey}"
                        to
                        email.toString()
            )
        )
    }

    override fun changeInvitationStatusToAccepted(invitation: Invitation) {
        invitation.status = STATUS_ACCEPTED
        dbReference.updateChildren(
            mapOf<String, Any>(
                "/$TABLE_INVITATIONS/${invitation.invitationFirebaseKey}"
                        to
                        invitation
            )
        )
    }

    override fun changeInvitationStatusToDeclined(invitation: Invitation) {
        invitation.status = STATUS_DECLINED
        dbReference.updateChildren(
            mapOf<String, Any>(
                "/$TABLE_INVITATIONS/${invitation.invitationFirebaseKey}"
                        to
                        invitation
            )
        )
    }
}