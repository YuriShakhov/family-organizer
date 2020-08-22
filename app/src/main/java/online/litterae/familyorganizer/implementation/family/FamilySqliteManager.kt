package online.litterae.familyorganizer.implementation.family

import android.util.Log
import kotlinx.coroutines.*
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManager
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.Email
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup
import online.litterae.familyorganizer.sqlite.MySentInvitation

class FamilySqliteManager: BaseSqliteManager<FamilyContract.Presenter>(), FamilyContract.SqliteManager {

    override fun init() {
        MainApplication.getAppComponent().createPageComponent().inject(this)
    }

    override suspend fun addMyModeratedGroupToSQLite(groupName: String, groupFirebaseKey: String) {
        val job = CoroutineScope(Dispatchers.Default)
            .launch {
                val group = MyGroup()
                group.firebaseKey = groupFirebaseKey
                group.name = groupName
                group.iAmAdmin = 1
                group.myCurrentGroup = 1
                myGroupDao?.insert(group)
                myGroupDao?.setGroupAsCurrent(groupFirebaseKey)
            }
        job.join()
    }

    override suspend fun getMyCurrentGroup(): Pair<MyGroup?, Boolean> {
        lateinit var result: Pair<MyGroup?, Boolean>
        val job = CoroutineScope(Dispatchers.Default)
            .launch {
                val myCurrentGroupDeferred = async {
                    myGroupDao?.getMyCurrentGroup()
                }
                val isMyModeratedDeferred = async {
                    myGroupDao?.isMyModeratedGroup()
                }
                val myCurrentGroup: MyGroup? = myCurrentGroupDeferred.await()
                val isMyModerated: Int? = isMyModeratedDeferred.await()
                Log.d(TAG, "getMyCurrentGroup: group ${myCurrentGroup?.name}, isMyModeratedGroup: $isMyModerated")
                if (isMyModerated == null || isMyModerated == 0) {
                    result = Pair(myCurrentGroup, false)
                } else {
                    result = Pair(myCurrentGroup, true)
                }
        }
        job.join()
        return result
    }

    override suspend fun getAllGroups() : List<MyGroup> {
        var groupsFound: List<MyGroup?>? = null
        val job = CoroutineScope(Dispatchers.Default)
            .launch {
                val deferred = CoroutineScope(Dispatchers.Default)
                    .async {
                        myGroupDao?.getAll()
                    }
                groupsFound = deferred.await()
        }
        job.join()
        val result: List<MyGroup> = groupsFound?.mapNotNull { it } ?: emptyList()
        return result
    }

    override suspend fun setGroupAsCurrent(groupFirebaseKey: String) {
        myGroupDao?.setGroupAsCurrent(groupFirebaseKey)
    }

    // This is a stub. The function will query the SQLite DB and return members of the current group
    override suspend fun getFriends(): List<MyFriend> {
        val father = MyFriend()
        father.name = "Father"
        val mother = MyFriend()
        mother.name = "Mother"
        val sister = MyFriend()
        sister.name = "Sister"
        val brother = MyFriend()
        brother.name = "Brother"
        return listOf(father, mother, sister, brother)
    }

    override suspend fun addSentInvitationToSqlite(invitation: Invitation, invitationFirebaseKey: String) {
//        val job = CoroutineScope(Dispatchers.Default)
//            .launch {
        Log.d(TAG, "sqliteManager: addSentInvitationToSqlite")
                val mySentInvitation = MySentInvitation()
                mySentInvitation.email = invitation.email
                mySentInvitation.groupName = invitation.groupName
                mySentInvitation.groupFirebaseKey = invitation.groupFirebaseKey
                mySentInvitation.message = invitation.message
                mySentInvitation.invitationFirebaseKey = invitationFirebaseKey
                val result = mySentInvitationDao?.insert(mySentInvitation)
        Log.d(TAG, "addSentInvitationToSqlite: result: $result")
//            }
//        job.join()
    }
}