package web.id.wahyou.assistentq.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Created by : wahyouwebid.
 * Email : hello@wahyou.web.id.
 * Linkedin : linkedin.com/in/wahyouwebid.
 * Instagram : instagram.com/wahyouwebid.
 * Portopolio : wahyou.web.id.
 */

@Dao
interface AssistantDao {

    @Insert
    fun insert(assistant: Assistant)

    @Update
    fun update(assistant: Assistant)

    @Query("SELECT * FROM assistant WHERE id = :id")
    fun get (id : Long) : Assistant?

    @Query("DELETE FROM assistant ")
    fun clear()

    @Query("SELECT * FROM assistant ORDER BY id ASC")
    fun getAll(): LiveData<List<Assistant>>

    @Query("SELECT * FROM assistant ORDER BY id ASC LIMIT 1 ")
    fun getCurrent(): Assistant?
}