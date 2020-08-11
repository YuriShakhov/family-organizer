package online.litterae.familyorganizer.implementation.family

import android.util.Log
import kotlinx.coroutines.*
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.application.Const.Companion.ERROR_INSERT_GROUP
import online.litterae.familyorganizer.application.Const.Companion.TAG
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

    override fun getData() {
        getCurrentGroupName()
        getFriends()
    }

    fun getCurrentGroupName() {
        CoroutineScope(Dispatchers.Default
        ).launch {
            var myGroup: MyGroup? = null
            val job = launch {
                myGroup = sqliteManager.getMyCurrentGroup()

            }
            job.join()
            CoroutineScope(Dispatchers.Main)
                .launch {
                    view?.showCurrentGroup(myGroup)
            }
        }
    }

    fun getFriends() {
        CoroutineScope(Dispatchers.Default)
            .launch {
            var friends: List<MyFriend?>? = null
            val job = launch {
                friends = sqliteManager.getFriends()
            }
            job.join()
            CoroutineScope(Dispatchers.Main)
                .launch {
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
            getData()
        }
    }

    override fun getGroupsList() {
        CoroutineScope(Dispatchers.Default)
            .launch {
            val myGroups = sqliteManager.getAllGroups()
            myGroups?.let{
                CoroutineScope(Dispatchers.Main)
                    .launch {
                    view?.showChooseGroupMenu(myGroups)
                }
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
                CoroutineScope(Dispatchers.Main)
                    .launch {
                        reportSuccess("Group $groupName created")
                    }
                getData()
            }
        } else {
            reportError(ERROR_INSERT_GROUP)
        }
    }

    override fun reportSuccess(message: String) {
        view?.showMessage(message)
    }

    override fun reportError(message: String) {
        view?.showMessage(message)
    }
}