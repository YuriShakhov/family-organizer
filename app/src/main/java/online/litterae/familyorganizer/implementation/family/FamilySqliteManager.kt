package online.litterae.familyorganizer.implementation.family

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManager
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup
import online.litterae.familyorganizer.sqlite.MyReceivedInvitation
import online.litterae.familyorganizer.sqlite.MySentInvitation

class FamilySqliteManager: BaseSqliteManager<FamilyContract.Presenter>(), FamilyContract.SqliteManager {

    override fun init() {
        MainApplication.createPageComponent().inject(this)
    }

    override suspend fun addMyModeratedGroupToSqlite(groupName: String, groupFirebaseKey: String) {
        val job = CoroutineScope(Default)
            .launch {
                val group = MyGroup()
                group.firebaseKey = groupFirebaseKey
                group.name = groupName
                group.iAmAdmin = 1
                group.myCurrentGroup = 1
                myGroupDao.insert(group)
                myGroupDao.setGroupAsCurrent(groupFirebaseKey)
            }
        job.join()
    }

    override suspend fun getMyCurrentGroup(): Pair<MyGroup?, Boolean> {
        lateinit var result: Pair<MyGroup?, Boolean>
        val job = CoroutineScope(Default)
            .launch {
                val myCurrentGroupDeferred = async {
                    myGroupDao.getMyCurrentGroup()
                }
                val isMyModeratedDeferred = async {
                    myGroupDao.isMyModeratedGroup()
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
        val job = CoroutineScope(Default)
            .launch {
                val deferred = CoroutineScope(Default)
                    .async {
                        myGroupDao.getAll()
                    }
                groupsFound = deferred.await()
        }
        job.join()
        val result: List<MyGroup> = groupsFound?.mapNotNull { it } ?: emptyList()
        return result
    }

    override suspend fun setGroupAsCurrent(groupFirebaseKey: String) {
        myGroupDao.setGroupAsCurrent(groupFirebaseKey)
    }

    override suspend fun getFriends(group: MyGroup): List<MyFriend> {
        val result: List<MyFriend> = myFriendDao.getFriendsFromGroup(group.firebaseKey)?.mapNotNull { it } ?: emptyList()
        return result
    }

    override suspend fun addSentInvitationToSqlite(invitation: Invitation) {
//        val job = CoroutineScope(Default)
//            .launch {
//        Log.d(TAG, "sqliteManager: addSentInvitationToSqlite")
                val mySentInvitation = MySentInvitation()
                mySentInvitation.email = invitation.invitedEmail
                mySentInvitation.groupName = invitation.groupName
                mySentInvitation.groupFirebaseKey = invitation.groupFirebaseKey
                mySentInvitation.message = invitation.message
                mySentInvitation.invitationFirebaseKey = invitation.invitationFirebaseKey
                mySentInvitationDao.insert(mySentInvitation)
//        Log.d(TAG, "addSentInvitationToSqlite: result: $result")
//            }
//        job.join()
    }

    override suspend fun addReceivedInvitationToSqlite(invitation: Invitation) {
        val myReceivedInvitation = MyReceivedInvitation()
        myReceivedInvitation.email = invitation.senderEmail
        myReceivedInvitation.groupName = invitation.groupName
        myReceivedInvitation.groupFirebaseKey = invitation.groupFirebaseKey
        myReceivedInvitation.message = invitation.message
        myReceivedInvitation.status = invitation.status
        myReceivedInvitation.invitationFirebaseKey = invitation.invitationFirebaseKey
        myReceivedInvitationDao.insert(myReceivedInvitation)
        Log.d(TAG, "addReceivedInvitationToSqlite: OK")
    }

    override suspend fun updateFriends(group: MyGroup, currentFriends: List<MyFriend>, newFriends: List<Pair<String, String>>, myFirebaseKey: String) {
        Log.d(TAG, "updateFriends: currentFriends: $currentFriends, newFriends: $newFriends")
        val currentFriendsIds: List<String>
                = currentFriends.map { it.userFirebaseKey }
        newFriends.filter { !currentFriendsIds.contains(it.first) }
            .apply {
                Log.d(TAG, "updateFriends: 1st filter: $this")
            }
            .filter { it.first != myFirebaseKey }
            .apply {
                Log.d(TAG, "updateFriends: 2nd filter: $this")
            }
            .forEach{
                val myFriend = MyFriend()
                myFriend.userFirebaseKey = it.first
                myFriend.name = it.second
                myFriend.email = it.second
                myFriend.groupFirebaseKey = group.firebaseKey
                myFriendDao.insert(myFriend)
            }
    }
}