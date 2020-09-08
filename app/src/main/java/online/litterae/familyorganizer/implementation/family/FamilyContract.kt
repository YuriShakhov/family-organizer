package online.litterae.familyorganizer.implementation.family

import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManagerInterface
import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManagerInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup

interface FamilyContract {
    interface View: BaseViewInterface {
        fun showNotifications(count: Int)
        fun showCurrentGroup(group: MyGroup?, isMyModeratedGroup: Boolean)
        fun showFriends(friends: List<MyFriend>)
        fun showChooseGroupMenu(groups: List<MyGroup>)
        fun showToast(message: String)
    }

    interface Presenter: BasePresenterInterface<FamilyContract.View> {
        //from view
        fun getEmail(): String
        fun setDataInView()
        fun getGroupsList()
        suspend fun getCurrentGroup(): Pair<MyGroup?, Boolean>
        fun changeCurrentGroup(myGroup: MyGroup)
        fun createGroup(groupName: String)
        fun sendInvitation(myGroup: MyGroup, invitedEmail: String, message: String)
        fun logout()

        //from firebaseManager
        fun processReceivedInvitation(invitation: Invitation)
        fun onInvitationAddedToFirebase(invitation: Invitation)
        fun updateFriends(newFriends: List<Pair<String, String>>)

        fun reportSuccess(message: String)
        fun reportError(message: String)
    }

    interface SqliteManager: BaseSqliteManagerInterface<FamilyContract.Presenter> {
        suspend fun getMyCurrentGroup(): Pair<MyGroup?, Boolean>
        suspend fun getAllGroups(): List<MyGroup>
        suspend fun getFriends(group: MyGroup): List<MyFriend>
        suspend fun setGroupAsCurrent(groupFirebaseKey: String)
        suspend fun addMyModeratedGroupToSqlite (groupName: String, groupFirebaseKey: String)
        suspend fun addSentInvitationToSqlite (invitation: Invitation)
        suspend fun addReceivedInvitationToSqlite (invitation: Invitation)
        suspend fun updateFriends(group: MyGroup, currentFriends: List<MyFriend>, newFriends: List<Pair<String, String>>, myFirebaseKey: String)
    }

    interface FirebaseManager: BaseFirebaseManagerInterface<FamilyContract.Presenter> {
        fun addGroupToFirebase(name: String): String?
        fun addMeToFirebaseGroupUsers(groupName: String, groupFirebaseKey: String)
        fun addInvitationToFirebase(invitation: Invitation)
        fun subscribeToUpdates(myGroup: MyGroup)
    }
}