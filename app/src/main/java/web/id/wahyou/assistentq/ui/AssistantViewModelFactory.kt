package web.id.wahyou.assistentq.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import web.id.wahyou.assistentq.data.AssistantDao
import java.lang.IllegalArgumentException

/**
 * Created by : wahyouwebid.
 * Email : hello@wahyou.web.id.
 * Linkedin : linkedin.com/in/wahyouwebid.
 * Instagram : instagram.com/wahyouwebid.
 * Portopolio : wahyou.web.id.
 */
class AssistantViewModelFactory (
    private val assistantDao : AssistantDao,
    private val application : Application
) : ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssistantViewModel::class.java)){
            return AssistantViewModel(assistantDao, application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}