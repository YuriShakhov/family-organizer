package online.litterae.familyorganizer.sqlite

import androidx.room.*

@Dao
interface MyReceivedInvitationDao {
    @Query("SELECT * FROM MyReceivedInvitation")
    fun getAll(): List<MyReceivedInvitation?>?

    @Query("SELECT * FROM MyReceivedInvitation WHERE status = \"New\"")
    fun getAllNew(): List<MyReceivedInvitation?>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(myReceivedInvitation: MyReceivedInvitation?)

    @Update
    fun update(myReceivedInvitation: MyReceivedInvitation?)

    @Delete
    fun delete(myReceivedInvitation: MyReceivedInvitation?)
}