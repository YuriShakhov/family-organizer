package online.litterae.familyorganizer.sqlite

import androidx.room.*

@Dao
interface MyFriendDao {
    @Query("SELECT * FROM MyFriend WHERE groupFirebaseKey = :groupFirebaseKey")
    fun getFriendsFromGroup(groupFirebaseKey: String?): List<MyFriend?>?

    @Query("SELECT * FROM MyFriend WHERE email = :email")
    fun getFriend(email: String?): MyFriend?

    @Query("SELECT chatMessages FROM MyFriend WHERE email = :friendEmail")
    fun getMessages(friendEmail: String?): String?

    @Query("SELECT userFirebaseKey FROM MyFriend WHERE email = :email")
    fun getFirebaseKey(email: String?): String?

    @Update
    fun update(myFriend: MyFriend?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(myFriend: MyFriend?)

    @Delete
    fun delete(myFriend: MyFriend?)
}