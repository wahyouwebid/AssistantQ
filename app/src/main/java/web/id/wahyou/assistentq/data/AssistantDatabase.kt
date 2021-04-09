package web.id.wahyou.assistentq.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Created by : wahyouwebid.
 * Email : hello@wahyou.web.id.
 * Linkedin : linkedin.com/in/wahyouwebid.
 * Instagram : instagram.com/wahyouwebid.
 * Portopolio : wahyou.web.id.
 */

@Database(
    entities = [Assistant::class], version = 1, exportSchema = false
)
abstract class AssistantDatabase : RoomDatabase() {
    abstract fun assistant() : AssistantDao

    companion object {

        @Volatile
        private var INSTANCE: AssistantDatabase? = null

        fun getInstance(context: Context) : AssistantDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AssistantDatabase::class.java,
                        "Assistant.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }

}