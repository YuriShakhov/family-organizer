package online.litterae.familyorganizer.sqlite

import androidx.room.*

@Dao
interface MyReceivedInvitationDao {
    @Query("SELECT * FROM MyReceivedInvitation")
    fun getAll(): List<MyReceivedInvitation?>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(myReceivedInvitation: MyReceivedInvitation?)

    @Delete
    fun delete(myReceivedInvitation: MyReceivedInvitation?)
}