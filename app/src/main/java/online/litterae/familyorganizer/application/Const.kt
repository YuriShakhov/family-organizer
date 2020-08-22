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
        const val CREATE_GROUP = "Create group"
        const val SEND_GROUP = "Send"
        const val CREATE_INVITATION = "Invite"
        const val SEND_INVITATION = "Send"
        const val INVITED = "Invited"
        const val ERROR_INSERT_GROUP = "Firebase error: couldn't get database key for new group"
        const val ERROR_SEND_INVITATION = "Firebase error: couldn't get database key for new invitation"

        //Dialog
        const val OK_DIALOG = "OK"
        const val CANCEL_DIALOG = "Cancel"
        const val CHOOSE_GROUP_DIALOG = "Choose group"
    }
}