package com.example.greenharbour.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.greenharbour.Models.BarrenLocation
import com.example.greenharbour.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class BarrenEventViewAdapter(
    val context: Context,
    val eventsList: ArrayList<BarrenLocation>,
    val listener: OnItemClickListener<BarrenLocation>
) : RecyclerView.Adapter<BarrenEventViewAdapter.RowHolder>() {


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

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        Picasso.get().load(eventsList[position].img)
            .into(holder.eventsImage, object : Callback {
                override fun onSuccess() {
                    holder.progressBar.visibility = View.GONE
                    holder.eventsImage.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    holder.progressBar.visibility = View.GONE
                }

            })

        holder.eventsTitle.text = eventsList.get(position).title

        holder.itemView.setOnClickListener {
            listener.onItemClick(eventsList[position])
        }
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }
}

