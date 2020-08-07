package online.litterae.familyorganizer.implementation.family

import online.litterae.familyorganizer.abstracts.firebase.BaseFirebaseManagerInterface
import online.litterae.familyorganizer.abstracts.presenter.BasePresenterInterface
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManagerInterface
import online.litterae.familyorganizer.abstracts.view.BaseViewInterface
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup

interface FamilyContract {
    interface View: BaseViewInterface {
        fun showCurrentGroup(group: MyGroup?)
        fun showFriends(friends: List<MyFriend?>?)
        fun showChooseGroupMenu(groups: List<MyGroup?>)
        fun showErrorMessage(message: String)
    }

    interface Presenter: BasePresenterInterface<FamilyContract.View> {
        fun getData()
        fun getGroupsList()
        fun changeCurrentGroup(myGroup: MyGroup)
        fun createGroup(groupName: String)
        fun logout()
        fun reportError(message: String)
    }

    interface SqliteManager: BaseSqliteManagerInterface<FamilyContract.Presenter> {
        suspend fun getMyCurrentGroup(): MyGroup?
        suspend fun getAllGroups(): List<MyGroup?>?
        suspend fun getFriends(): List<MyFriend?>?
        suspend fun setGroupAsCurrent(groupFirebaseKey: String)
        suspend fun addMyModeratedGroupToSQLite (groupName: String, groupFirebaseKey: String)
    }

    interface FirebaseManager: BaseFirebaseManagerInterface<FamilyContract.Presenter> {
        fun addGroupToFirebase(name: String): String?
        fun addMeToFirebaseGroupUsers(groupName: String, groupFirebaseKey: String)
    }
}