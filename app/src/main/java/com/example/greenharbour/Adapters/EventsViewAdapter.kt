package com.example.greenharbour.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.greenharbour.Models.Events
import com.example.greenharbour.R
import com.squareup.picasso.Picasso

class EventsViewAdapter(val context: Context,val eventsList:ArrayList<Events>) :RecyclerView.Adapter<EventsViewAdapter.RowHolder>(){

    inner class RowHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        val eventsImage = itemView.findViewById<ImageView>(R.id.eventImage)
        val eventsAddress = itemView.findViewById<TextView>(R.id.eventsAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_layout_events_rv,parent,false)
        return  RowHolder(view)
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
    Picasso.get().load(eventsList[position].eventImageUrl).placeholder(R.drawable.placeholder).into(holder.eventsImage)
        holder.eventsAddress.text = eventsList.get(position).eventLocation
    }
}