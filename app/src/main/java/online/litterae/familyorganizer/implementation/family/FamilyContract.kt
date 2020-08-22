package online.litterae.familyorganizer.implementation.family

import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManagerInterface
import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManagerInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.firebase.Email
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup

interface FamilyContract {
    interface View: BaseViewInterface {
        fun showCurrentGroup(group: MyGroup?, isMyModeratedGroup: Boolean)
        fun showFriends(friends: List<MyFriend>)
        fun showChooseGroupMenu(groups: List<MyGroup>)
        fun showMessage(message: String)
    }

    interface Presenter: BasePresenterInterface<FamilyContract.View> {
        fun setData()
        fun getGroupsList()
        suspend fun getCurrentGroupFromSqlite(): Pair<MyGroup?, Boolean>
        fun changeCurrentGroup(myGroup: MyGroup)
        fun createGroup(groupName: String)
        fun sendInvitation(myGroup: MyGroup, invitedEmail: String, message: String)
        fun logout()
        fun reportSuccess(message: String)
        fun reportError(message: String)
    }

    interface SqliteManager: BaseSqliteManagerInterface<FamilyContract.Presenter> {
        suspend fun getMyCurrentGroup(): Pair<MyGroup?, Boolean>
        suspend fun getAllGroups(): List<MyGroup>
        suspend fun getFriends(): List<MyFriend>
        suspend fun setGroupAsCurrent(groupFirebaseKey: String)
        suspend fun addMyModeratedGroupToSQLite (groupName: String, groupFirebaseKey: String)
        suspend fun addSentInvitationToSqlite (invitation: Invitation, invitationFirebaseKey: String)
    }

    interface FirebaseManager: BaseFirebaseManagerInterface<FamilyContract.Presenter> {
        fun addGroupToFirebase(name: String): String?
        fun addMeToFirebaseGroupUsers(groupName: String, groupFirebaseKey: String)
        fun addInvitationToFirebase(invitation: Invitation): String?
    }
}