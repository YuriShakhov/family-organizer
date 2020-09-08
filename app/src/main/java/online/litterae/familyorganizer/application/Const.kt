package online.litterae.familyorganizer.application

class Const {
    companion object {
        const val TAG = "MyTag"

        //Shared preferences
        const val APP_PREFERENCES = "App Preferences"

        //Firebase
        const val TABLE_PROFILES = "Profiles"
        const val TABLE_GROUPS = "Groups"
        const val TABLE_INVITATIONS = "Invitations"
        const val TABLE_USERS = "users"
        const val TABLE_MESSAGES = "messages"
        const val CREATE_GROUP = "Create group"
        const val SEND_GROUP = "Send"
        const val CREATE_INVITATION = "Invite"
        const val SEND_INVITATION = "Send"
        const val INVITED_EMAIL = "invitedEmail"
        const val GROUP_NAME = "groupName"
        const val INVITATION_STATUS = "status"
        const val INVITATION_FIREBASE_KEY = "invitationFirebaseKey"

        const val ERROR_AUTHORIZATION_FAILED = "Authorization failed"
        const val ERROR_REGISTRATION_FAILED = "Registration failed"
        const val ERROR_CREATE_PROFILE = "Firebase error: couldn't get database key for new profile"
        const val ERROR_INSERT_GROUP = "Firebase error: couldn't get database key for new group"
        const val ERROR_SEND_INVITATION = "Firebase error: couldn't get database key for new invitation"
        const val ERROR_INVITATION_ALREADY_SENT = "Invitation already sent"

        //Dialog
        const val DIALOG_OK = "OK"
        const val DIALOG_CREATE = "Create"
        const val DIALOG_CANCEL = "Cancel"
        const val DIALOG_ENTER_EMAIL = "Enter email"
        const val DIALOG_WRITE_MESSAGE = "Write a message"
        const val DIALOG_SEND = "Send"
        const val DIALOG_CHOOSE_GROUP = "Choose group"
        const val DIALOG_ENTER_GROUP_NAME = "Enter group name:"

        //Invitation
        const val INVITATION_KEY_DEFAULT = "Default key"

        //Notification status
        const val STATUS_NEW = "New"
        const val STATUS_SEEN = "Seen"
        const val STATUS_ACCEPTED = "Accepted"
        const val STATUS_DECLINED = "Declined"

        //keys
        const val KEY_NAME = "name"
        const val KEY_MY_FRIEND = "myFriend"
        const val KEY_MY_GROUP = "myGroup"

        //view
        const val TYPE_MESSAGE_RECEIVED = 0
        const val TYPE_MESSAGE_SENT = 1
    }
}