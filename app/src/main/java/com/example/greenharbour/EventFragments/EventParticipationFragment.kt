package com.example.greenharbour.EventFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenharbour.Adapters.EventParticipationAdapter
import com.example.greenharbour.Models.Events
import com.example.greenharbour.R
import com.example.greenharbour.ViewModel.EventVIewModel
import com.example.greenharbour.databinding.FragmentEventParticipationBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class EventParticipationFragment : Fragment() {

    private lateinit var participationList:ArrayList<String>
    private val viewModel: EventVIewModel by activityViewModels()
    private lateinit var eventName:String
    private lateinit var adapter: EventParticipationAdapter

    private lateinit var binding:FragmentEventParticipationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEventParticipationBinding.inflate(inflater,container,false)

        participationList = ArrayList()

        viewModel.event.observe(viewLifecycleOwner){
            eventName = it.eventName.toString()
            getParticipationData()
            adapter = EventParticipationAdapter(requireContext(),participationList)
            binding.rv.layoutManager = LinearLayoutManager(requireContext())
            binding.rv.adapter= adapter
        }


        return binding.root
    }

    private fun getParticipationData(){
        Firebase.firestore.collection(eventName).get().addOnSuccessListener {
            val tempEventList = ArrayList<String>()
            if (!it.isEmpty) {
                for (document in it) {
                    val participant = document.getString("email")
                    if (participant != null) {
                        tempEventList.add(participant)
                    }
                }
                participationList.clear()
                participationList.addAll(tempEventList)

                for(i in participationList){
                    Log.d("NearbyEventActivity","image${i}")
                }

               adapter.notifyDataSetChanged()



            }

        }
    }

}