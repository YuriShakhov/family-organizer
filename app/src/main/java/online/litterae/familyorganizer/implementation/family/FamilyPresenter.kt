package online.litterae.familyorganizer.implementation.family

import kotlinx.coroutines.*
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.abstracts.presenter.PagePresenter
import online.litterae.familyorganizer.application.Const.Companion.ERROR_INSERT_GROUP
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup
import javax.inject.Inject

class FamilyPresenter : PagePresenter<FamilyContract.View>(), FamilyContract.Presenter {

    @Inject lateinit var sqliteManager: FamilyContract.SqliteManager
    @Inject lateinit var firebaseManager: FamilyContract.FirebaseManager

    val defaultScope = CoroutineScope(Dispatchers.Default)
    val mainScope = CoroutineScope(Dispatchers.Main)

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
        defaultScope.launch {
            var myGroup: MyGroup? = null
            val job = launch {
                myGroup = sqliteManager.getMyCurrentGroup()
            }
            job.join()
            mainScope.launch {
                view?.showCurrentGroup(myGroup)
            }
        }
    }

    fun getFriends() {
        defaultScope.launch {
            var friends: List<MyFriend?>? = null
            val job = launch {
                friends = sqliteManager.getFriends()
            }
            job.join()
            mainScope.launch {
                view?.showFriends(friends)
            }
        }
    }

    override fun changeCurrentGroup(myGroup: MyGroup) {
        defaultScope.launch{
            val job = launch {
                sqliteManager.setGroupAsCurrent(myGroup.firebaseKey)
            }
            job.join()
            getData()
        }
    }

    override fun getGroupsList() {
        defaultScope.launch {
            val myGroups = sqliteManager.getAllGroups()
            myGroups?.let{
                mainScope.launch {
                    view?.showChooseGroupMenu(myGroups)
                }
            }
        }
    }

    override fun createGroup(groupName: String) {
        val groupFirebaseKey = firebaseManager.addGroupToFirebase(groupName)
        if (groupFirebaseKey != null) {
            firebaseManager.addMeToFirebaseGroupUsers(groupName, groupFirebaseKey)
            defaultScope.launch {
                val job = launch {
                    sqliteManager.addMyModeratedGroupToSQLite(groupName, groupFirebaseKey)
                }
                job.join()
                getData()
            }
        } else {
            reportError(ERROR_INSERT_GROUP)
        }
    }

    override fun reportError(message: String) {
        view?.showErrorMessage(message)
    }
}