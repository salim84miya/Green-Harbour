package com.example.greenharbour.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.greenharbour.R

class EventParticipationAdapter(val context: Context, val participants: ArrayList<String>) :
    RecyclerView.Adapter<EventParticipationAdapter.RowHolder>() {

    inner class RowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.participantView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.participants_row_layout, parent, false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return participants.size
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.text.text = participants[position]
    }
}