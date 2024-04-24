package com.example.greenharbour.EventFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.greenharbour.Events.CreateEventsActivity
import com.example.greenharbour.ViewModel.EventVIewModel
import com.example.greenharbour.databinding.FragmentEventViewBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class EventViewFragment : Fragment() {

    private lateinit var binding:FragmentEventViewBinding
    private val viewModel: EventVIewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEventViewBinding.inflate(inflater,container,false)


        viewModel.event.observe(viewLifecycleOwner){

            Picasso.get().load(it.eventImageUrl)
                .into(binding.eventImageView, object : Callback {
                    override fun onSuccess() {
                        binding.progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        binding.progressBar.visibility = View.GONE
                    }

                })

//            val description = "<font color=#000000>Title ? </font> <font color=#069563>Login In</font>"
            binding.eventDescFr.text = it.eventDesc
            binding.eventLocationFr.text = it.eventLocation
            binding.contactFr.text = it.eventContact
            binding.evDateFr.text = it.eventDate
            binding.eventTitleFr.text = it.eventName
            binding.evTimeFr.text = it.eventTime

            val eventTitle = it.eventName
            val eventDesc = it.eventDesc
            val eventLocation = it.eventLocation
            val eventDate = it.eventDate
            val eventTime = it.eventTime
            val eventContact = it.eventContact
            val eventImage = it.eventImageUrl

            binding.edtBtn.setOnClickListener {
                val intent = Intent(requireActivity(),CreateEventsActivity::class.java)
                intent.putExtra("eventTitle",eventTitle)
                intent.putExtra("eventDesc",eventDesc)
                intent.putExtra("eventLocation",eventLocation)
                intent.putExtra("eventDate",eventDate)
                intent.putExtra("eventTime",eventTime)
                intent.putExtra("eventContact",eventContact)
                intent.putExtra("eventImage",eventImage)
                intent.putExtra("MODE",2)
                requireActivity().startActivity(intent)
            }
        }

        return binding.root



    }


}