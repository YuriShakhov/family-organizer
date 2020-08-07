package online.litterae.familyorganizer.implementation.family

import kotlinx.coroutines.*
import online.litterae.familyorganizer.abstracts.sqlite.BaseSqliteManager
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.sqlite.MyFriend
import online.litterae.familyorganizer.sqlite.MyGroup

class FamilySqliteManager: BaseSqliteManager<FamilyContract.Presenter>(), FamilyContract.SqliteManager {

    val defaultScope = CoroutineScope(Dispatchers.Default)
//    val myGroupDao = database.myGroupDao()

    override fun init() {
        MainApplication.getAppComponent().createPageComponent().inject(this)
    }

    override suspend fun addMyModeratedGroupToSQLite(groupName: String, groupFirebaseKey: String) {
//        myGroupDao?.let {
            defaultScope.launch {
                val group = MyGroup()
                group.firebaseKey = groupFirebaseKey
                group.name = groupName
                group.iAmAdmin = 1
                group.myCurrentGroup = 1
                myGroupDao?.insert(group)
                myGroupDao?.setGroupAsCurrent(groupFirebaseKey)
            }
//        }
    }

    override suspend fun getMyCurrentGroup(): MyGroup? {
        var result: MyGroup? = null
        val job = defaultScope.launch {
            val myGroupDao = database.myGroupDao()
//            myGroupDao?.let {
                val deferred = async {
                    myGroupDao?.getMyCurrentGroup()
                }
                result = deferred.await()
//            }
        }
        job.join()
        return result
    }

    override suspend fun getAllGroups() : List<MyGroup?>? {
        var result: List<MyGroup?>? = null
        val job = defaultScope.launch {
//            myGroupDao?.let {
                val deferred = GlobalScope.async {
                    myGroupDao?.getAll()
                }
                result = deferred.await()
//            }
        }
        job.join()
        return result
    }

    override suspend fun setGroupAsCurrent(groupFirebaseKey: String) {
//        myGroupDao?.let {
            myGroupDao?.setGroupAsCurrent(groupFirebaseKey)
//        }
    }

    // This is a stub. The function will query the db and return the members of the current group
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
}