package online.litterae.familyorganizer.sqlite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface MySentInvitationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(mySentInvitation: MySentInvitation?)

    @Delete
    fun delete(mySentInvitation: MySentInvitation?)
}