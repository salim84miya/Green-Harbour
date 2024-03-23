package com.example.greenharbour.EventFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.greenharbour.R
import com.example.greenharbour.databinding.FragmentEventParticipationBinding


class EventParticipationFragment : Fragment() {

    private lateinit var binding:FragmentEventParticipationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEventParticipationBinding.inflate(inflater,container,false)
        return binding.root
    }

}