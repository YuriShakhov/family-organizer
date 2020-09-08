package online.litterae.familyorganizer.implementation.notifications

import online.litterae.familyorganizer.application.Const.Companion.STATUS_NEW
import online.litterae.familyorganizer.firebase.Invitation

sealed class Notification {
    var shortMessage: String = ""
    var status: String = STATUS_NEW
}

class ReceivedInvitationNotification (val invitation: Invitation) : Notification() {
    init {
        this.shortMessage = "${invitation.senderEmail} invites you to join the group ${invitation.groupName}. Message: ${invitation.message}"
    }
}

class ReplyToInvitationNotification : Notification()

class ReceivedMessageNotification : Notification()