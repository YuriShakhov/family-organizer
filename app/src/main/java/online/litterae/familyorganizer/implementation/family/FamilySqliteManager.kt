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
        CoroutineScope(Default)
            .launch {
                val group = MyGroup()
                group.groupFirebaseKey = groupFirebaseKey
                group.name = groupName
                group.iAmAdmin = 1
                group.myCurrentGroup = 1
                myGroupDao.insert(group)
                myGroupDao.setGroupAsCurrent(groupFirebaseKey)
            }.join()
    }

    override suspend fun getMyCurrentGroup(): Pair<MyGroup?, Boolean> {
        lateinit var result: Pair<MyGroup?, Boolean>
        CoroutineScope(Default)
            .launch {
                val myCurrentGroupDeferred = async {
                    myGroupDao.getMyCurrentGroup()
                }
                val isMyModeratedDeferred = async {
                    myGroupDao.isMyModeratedGroup()
                }
                val myCurrentGroup: MyGroup? = myCurrentGroupDeferred.await()
                val isMyModerated: Int? = isMyModeratedDeferred.await()
                result = if (isMyModerated == null || isMyModerated == 0) {
                    Pair(myCurrentGroup, false)
                } else {
                    Pair(myCurrentGroup, true)
                }
            }.join()
        return result
    }

    override suspend fun getAllGroups() : List<MyGroup> {
        var groupsFound: List<MyGroup?>? = null
        CoroutineScope(Default)
            .launch {
                val deferred = CoroutineScope(Default)
                    .async {
                        myGroupDao.getAll()
                    }
                groupsFound = deferred.await()
            }.join()
        return groupsFound?.mapNotNull { it } ?: emptyList()
    }

    override suspend fun setGroupAsCurrent(groupFirebaseKey: String) {
        myGroupDao.setGroupAsCurrent(groupFirebaseKey)
    }

    override suspend fun getFriends(group: MyGroup): List<MyFriend> {
        return myFriendDao.getFriendsFromGroup(group.groupFirebaseKey)?.mapNotNull { it } ?: emptyList()
    }

    override suspend fun addSentInvitationToSqlite(invitation: Invitation) {
        val mySentInvitation = MySentInvitation()
        mySentInvitation.email = invitation.invitedEmail
        mySentInvitation.groupName = invitation.groupName
        mySentInvitation.groupFirebaseKey = invitation.groupFirebaseKey
        mySentInvitation.message = invitation.message
        mySentInvitation.invitationFirebaseKey = invitation.invitationFirebaseKey
        mySentInvitationDao.insert(mySentInvitation)
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
        val currentFriendsIds: List<String>
                = currentFriends.map { it.userFirebaseKey }
        newFriends.filter { !currentFriendsIds.contains(it.first) }
            .filter { it.first != myFirebaseKey }
            .forEach{
                val myFriend = MyFriend()
                myFriend.userFirebaseKey = it.first
                myFriend.name = it.second
                myFriend.email = it.second
                myFriend.groupFirebaseKey = group.groupFirebaseKey
                myFriendDao.insert(myFriend)
            }
    }
}