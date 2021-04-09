package web.id.wahyou.assistentq.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by : wahyouwebid.
 * Email : hello@wahyou.web.id.
 * Linkedin : linkedin.com/in/wahyouwebid.
 * Instagram : instagram.com/wahyouwebid.
 * Portopolio : wahyou.web.id.
 */

@Entity
data class Assistant(
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L,
    var assistantMessage : String = "DEFAULT_MESSAGE",
    var userMessage : String = "DEFAULT_MESSAGE"
)
