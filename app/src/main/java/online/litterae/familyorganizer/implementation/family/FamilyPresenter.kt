package online.litterae.familyorganizer.implementation.family

import android.util.Log
import kotlinx.coroutines.*
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.application.Const.Companion.ERROR_INSERT_GROUP
import online.litterae.familyorganizer.application.Const.Companion.ERROR_SEND_INVITATION
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup
import javax.inject.Inject

class FamilyPresenter : PagePresenter<FamilyContract.View>(), FamilyContract.Presenter {

    @Inject lateinit var sqliteManager: FamilyContract.SqliteManager
    @Inject lateinit var firebaseManager: FamilyContract.FirebaseManager

    override fun init() {
        MainApplication.getAppComponent().createPageComponent().inject(this)
        sqliteManager.attach(this)
        firebaseManager.attach(this)
    }

    override fun setData() {
        setCurrentGroupNameInView()
        setFriendsInView()
    }

    fun setCurrentGroupNameInView() {
        CoroutineScope(Dispatchers.Default).launch {
            val result = getCurrentGroupFromSqlite()
            withContext(Dispatchers.Main) {
                view?.showCurrentGroup(result.first, result.second)
            }
        }
    }

    override suspend fun getCurrentGroupFromSqlite(): Pair<MyGroup?, Boolean> {
        var myGroup: MyGroup? = null
        var isMyModeratedGroup: Boolean = false
        val job = CoroutineScope(Dispatchers.Default
        ).launch {
            val result = sqliteManager.getMyCurrentGroup()
            myGroup = result.first
            isMyModeratedGroup = result.second
        }
        job.join()
        return Pair(myGroup, isMyModeratedGroup)
    }

    fun setFriendsInView() {
        CoroutineScope(Dispatchers.Default)
            .launch {
            var friends: List<MyFriend> = listOf()
            val job = launch {
                friends = sqliteManager.getFriends()
            }
            job.join()
            withContext(Dispatchers.Main) {
                view?.showFriends(friends)
            }
        }
    }

    override fun changeCurrentGroup(myGroup: MyGroup) {
        CoroutineScope(Dispatchers.Default)
            .launch{
            val job = launch {
                sqliteManager.setGroupAsCurrent(myGroup.firebaseKey)
            }
            job.join()
            setData()
        }
    }

    override fun getGroupsList() {
        CoroutineScope(Dispatchers.Default).launch {
            val myGroups = sqliteManager.getAllGroups()
            withContext(Dispatchers.Main) {
                view?.showChooseGroupMenu(myGroups)
            }
        }
    }

    override fun createGroup(groupName: String) {
        val groupFirebaseKey = firebaseManager.addGroupToFirebase(groupName)
        if (groupFirebaseKey != null) {
            firebaseManager.addMeToFirebaseGroupUsers(groupName, groupFirebaseKey)
            CoroutineScope(Dispatchers.Default)
                .launch {
                val job = launch {
                    sqliteManager.addMyModeratedGroupToSQLite(groupName, groupFirebaseKey)
                }
                job.join()
                withContext(Dispatchers.Main) {
                    reportSuccess("Group $groupName created")
                }
                setData()
            }
        } else {
            reportError(ERROR_INSERT_GROUP)
        }
    }

    override fun sendInvitation(myGroup: MyGroup, invitedEmail: String, message: String) {
        val invitation = Invitation(myGroup.firebaseKey, myGroup.name, invitedEmail, message)
        val invitationFirebaseKey = firebaseManager.addInvitationToFirebase(invitation)
        Log.d(TAG, "sendInvitation: invitationFirebaseKey: $invitationFirebaseKey")
        if (invitationFirebaseKey != null) {
            CoroutineScope(Dispatchers.Default)
                .launch {
                    Log.d(TAG, "sendInvitation: try addSentInvitationToSqlite")
                    sqliteManager.addSentInvitationToSqlite(invitation, invitationFirebaseKey)
                }
        } else {
            reportError(ERROR_SEND_INVITATION)
            Log.d(TAG, "sendInvitation: $ERROR_SEND_INVITATION")
        }
     }

    override fun reportSuccess(message: String) {
        view?.showMessage(message)
    }

    override fun reportError(message: String) {
        view?.showMessage(message)
    }
}