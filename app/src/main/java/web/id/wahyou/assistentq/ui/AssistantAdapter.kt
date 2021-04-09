package web.id.wahyou.assistentq.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import web.id.wahyou.assistentq.R
import web.id.wahyou.assistentq.data.Assistant

/**
 * Created by : wahyouwebid.
 * Email : hello@wahyou.web.id.
 * Linkedin : linkedin.com/in/wahyouwebid.
 * Instagram : instagram.com/wahyouwebid.
 * Portopolio : wahyou.web.id.
 */
class AssistantAdapter : RecyclerView.Adapter<AssistantAdapter.ViewHolder>() {

    var data = listOf<Assistant>()

    set(value){
        field = value
        notifyDataSetChanged()
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val assistantMessage : TextView = itemView.findViewById(R.id.tvAssistant)
        val userMessage : TextView = itemView.findViewById(R.id.tvUser)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.adapter_assistant, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.assistantMessage.text = item.assistantMessage
        holder.userMessage.text = item.userMessage
    }

    override fun getItemCount() = data.size
}