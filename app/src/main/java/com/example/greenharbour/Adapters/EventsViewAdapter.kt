package com.example.greenharbour.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.greenharbour.Models.Events
import com.example.greenharbour.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

class EventsViewAdapter(
    val context: Context,
    val eventsList: ArrayList<Events>,
    val listener: OnItemClickListener<Events>
) : RecyclerView.Adapter<EventsViewAdapter.RowHolder>() {

    inner class RowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val eventsImage = itemView.findViewById<ImageView>(R.id.eventImage)
        val eventsTitle = itemView.findViewById<TextView>(R.id.eventsTitle)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.event_img_prog_bars)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_layout_events_rv, parent, false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        Picasso.get().load(eventsList[position].eventImageUrl)
            .into(holder.eventsImage, object : Callback {
                override fun onSuccess() {
                    holder.progressBar.visibility = View.GONE
                    holder.eventsImage.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    holder.progressBar.visibility = View.GONE
                }

            })

        holder.eventsTitle.text = eventsList.get(position).eventName

        holder.itemView.setOnClickListener {
            listener.onItemClick(eventsList[position])
        }
    }
}

interface OnItemClickListener<T> {
    fun onItemClick(view: T)
}