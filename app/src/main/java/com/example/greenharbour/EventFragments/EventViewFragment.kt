package com.example.greenharbour.EventFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
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

            binding.eventDescFr.text = it.eventDesc
            binding.eventLocationFr.text = it.eventLocation
            binding.contactFr.text = it.eventContact
            binding.evDateFr.text = it.eventDate
            binding.eventTitleFr.text = it.eventName
        }

        return binding.root



    }


}