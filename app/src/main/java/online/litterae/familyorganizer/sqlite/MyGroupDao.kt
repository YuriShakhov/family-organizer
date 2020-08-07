package online.litterae.familyorganizer.sqlite

import androidx.room.*

@Dao
interface MyGroupDao {
    @Query("SELECT * FROM MyGroup")
    fun getAll(): List<MyGroup?>?
//
//    @Query("SELECT * FROM MYGROUP WHERE iAmAdmin = 1")
//    fun getMyModeratedGroups(): List<MyGroup?>?
//
//    @Query("SELECT name FROM MYGROUP WHERE iAmAdmin = 1")
//    fun getMyModeratedGroupsNames(): List<String?>?
//
//    @Query("SELECT firebaseKey FROM MYGROUP WHERE iAmAdmin = 1")
//    fun getMyModeratedGroupsKeys(): List<String?>?
//
//    @Query("SELECT * FROM MyGroup WHERE firebaseKey = :firebaseKey")
//    fun getGroupByFirebaseKey(firebaseKey: Long): MyGroup?
//
//    @Query("SELECT * FROM MyGroup WHERE name = :name LIMIT 1")
//    fun getGroupByName(name: String?): MyGroup?

    @Query("SELECT * FROM MyGroup WHERE myCurrentGroup = 1")
    suspend fun getMyCurrentGroup(): MyGroup

    @Query("UPDATE MyGroup SET myCurrentGroup = 1 WHERE firebaseKey = :firebaseKey")
    suspend fun markGroupAsCurrent(firebaseKey: String)

    @Query("UPDATE MyGroup SET myCurrentGroup = 0 WHERE NOT firebaseKey = :firebaseKey")
    suspend fun unmarkOtherGroups(firebaseKey: String)

    @Transaction
    suspend fun setGroupAsCurrent(firebaseKey: String) {
        markGroupAsCurrent(firebaseKey)
        unmarkOtherGroups(firebaseKey)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(group: MyGroup?)

//    @Update
//    fun update(group: MyGroup?)
//
//    @Delete
//    fun delete(group: MyGroup?)
}