package web.id.wahyou.assistentq.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import web.id.wahyou.assistentq.data.Assistant
import web.id.wahyou.assistentq.data.AssistantDao

/**
 * Created by : wahyouwebid.
 * Email : hello@wahyou.web.id.
 * Linkedin : linkedin.com/in/wahyouwebid.
 * Instagram : instagram.com/wahyouwebid.
 * Portopolio : wahyou.web.id.
 */
class AssistantViewModel(
    private val localDatabase : AssistantDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()

    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val currentMessage = MutableLiveData<Assistant?>()

    val messages = localDatabase.getAll()

    init {
        initializeCurrentMessage()
    }

    private fun initializeCurrentMessage() {
        uiScope.launch {
            currentMessage.value = getCurrentMessage()
        }
    }

    private suspend fun getCurrentMessage(): Assistant? {
        return withContext(Dispatchers.IO){
            var message = localDatabase.getCurrent()
            if(
                message?.assistantMessage == "DEFAULT_MESSAGE" ||
                message?.userMessage == "DEFAULT_MESSAGE"

            ) {
                message = null
            }
            message
        }
    }

    fun sendMessage (assistantMessage : String, userMessage : String) {
        uiScope.launch {
            val newAssistant = Assistant()
            newAssistant.assistantMessage = assistantMessage
            newAssistant.userMessage = userMessage

            insert(newAssistant)
            currentMessage.value = getCurrentMessage()
        }
    }

    private suspend fun insert(assistant: Assistant) {
        withContext(Dispatchers.IO){
            localDatabase.insert(assistant)
        }
    }

    private suspend fun update(assistant: Assistant) {
        withContext(Dispatchers.IO){
            localDatabase.update(assistant)
        }
    }

    fun onClear(){
        uiScope.launch {
            clear()
            currentMessage.value = null
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO){
            localDatabase.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}