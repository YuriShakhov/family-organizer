package online.litterae.familyorganizer.implementation.family

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.application.Const.Companion.ERROR_INSERT_GROUP
import online.litterae.familyorganizer.application.Const.Companion.ERROR_SEND_INVITATION
import online.litterae.familyorganizer.application.Const.Companion.INVITATION_KEY_DEFAULT
import online.litterae.familyorganizer.application.Const.Companion.TAG
import online.litterae.familyorganizer.firebase.Invitation
import online.litterae.familyorganizer.implementation.notifications.ReceivedInvitationNotification
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup
import javax.inject.Inject

class FamilyPresenter : PagePresenter<FamilyContract.View>(), FamilyContract.Presenter {

    @Inject lateinit var sqliteManager: FamilyContract.SqliteManager
    @Inject lateinit var firebaseManager: FamilyContract.FirebaseManager

    override fun init() {
        MainApplication.createPageComponent().inject(this)
        sqliteManager.attach(this)
        firebaseManager.attach(this)
        notificationsHolder.attach(this)
        super.init()
    }

    override fun setDataInView() {
        setCurrentGroupInView()
        setNotificationsCount()
    }

    fun setCurrentGroupInView() {
        CoroutineScope(Default).launch {
            val currentGroup = getCurrentGroup()
            val myGroup = currentGroup.first
            val isMyModeratedGroup = currentGroup.second
            withContext(Main) {
                view?.showCurrentGroup(myGroup, isMyModeratedGroup)
            }
            myGroup?.let {
                setFriendsInView(it)
                firebaseManager.subscribeToUpdates(it)
            }
        }
    }
    
    fun setFriendsInView(group: MyGroup) {
        CoroutineScope(Default).launch {
            val friends = sqliteManager.getFriends(group)
            withContext(Main) {
                view?.showFriends(friends)
            }
        }
    }

    override suspend fun getCurrentGroup(): Pair<MyGroup?, Boolean> {
        var myGroup: MyGroup? = null
        var isMyModeratedGroup = false
        CoroutineScope(Default).launch {
            val result = sqliteManager.getMyCurrentGroup()
            myGroup = result.first
            isMyModeratedGroup = result.second
        }.join()
        return Pair(myGroup, isMyModeratedGroup)
    }

    fun setNotificationsCount() {
        val count = notificationsHolder.getNewNotificationsCount()
        setNotifications(count)
    }

    override fun changeCurrentGroup(myGroup: MyGroup) {
        CoroutineScope(Default).launch{
            launch {
                sqliteManager.setGroupAsCurrent(myGroup.firebaseKey)
            }.join()
            withContext(Main) {
                setDataInView()
            }
        }
    }

    override fun getGroupsList() {
        CoroutineScope(Default).launch {
            val myGroups = sqliteManager.getAllGroups()
            withContext(Main) {
                view?.showChooseGroupMenu(myGroups)
            }
        }
    }

    override fun createGroup(groupName: String) {
        val groupFirebaseKey = firebaseManager.addGroupToFirebase(groupName)
        if (groupFirebaseKey != null) {
            firebaseManager.addMeToFirebaseGroupUsers(groupName, groupFirebaseKey)
            CoroutineScope(Default).launch {
                launch {
                    sqliteManager.addMyModeratedGroupToSqlite(groupName, groupFirebaseKey)
                }.join()
                withContext(Main) {
                    reportSuccess("Group $groupName created")
                    setDataInView()
                }
            }
        } else {
            reportError(ERROR_INSERT_GROUP)
        }
    }

    override fun sendInvitation(myGroup: MyGroup, invitedEmail: String, message: String) {
        if (invitedEmail.contains("@")) {
            val invitation = Invitation(myGroup.firebaseKey, myGroup.name, invitedEmail, message)
            firebaseManager.addInvitationToFirebase(invitation)
        } else {
            reportError("Please enter email")
        }
     }

    override fun onInvitationAddedToFirebase(invitation: Invitation) {
        if (invitation.invitationFirebaseKey != INVITATION_KEY_DEFAULT) {
            CoroutineScope(Default)
                .launch {
                    sqliteManager.addSentInvitationToSqlite(invitation)
                }
        } else {
            reportError(ERROR_SEND_INVITATION)
        }
    }

    override fun updateFriends(newFriends: List<Pair<String, String>>) {
        CoroutineScope(Default)
            .launch {
                val myCurrentGroup = sqliteManager.getMyCurrentGroup().first
                myCurrentGroup?.let {
                    val currentFriends = sqliteManager.getFriends(myCurrentGroup)
                    val currentFriendsIds: List<String>
                            = currentFriends.map { it.userFirebaseKey }
                    val newFriendsIds: List<String>
                            = newFriends.map { it.first }
                    if (!currentFriendsIds.equals(newFriendsIds)) {
                        launch {
                            sqliteManager.updateFriends(myCurrentGroup, currentFriends, newFriends, firebaseKey.toString())
                        }.join()
                        setFriendsInView(myCurrentGroup)
                    }
                }
            }
    }

    override fun processReceivedInvitation(invitation: Invitation) {
        CoroutineScope(Default).launch {
            sqliteManager.addReceivedInvitationToSqlite(invitation)
        }
        val notification = ReceivedInvitationNotification(invitation)
        notificationsHolder.addNotification(notification)
    }

    override fun reportSuccess(message: String) {
        view?.showToast(message)
    }

    override fun reportError(message: String) {
        view?.showToast(message)
    }

    override fun setNotifications(count: Int) {
        view?.showNotifications(count)
    }

    override fun getEmail(): String = email.toString()
}