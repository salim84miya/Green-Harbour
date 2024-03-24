package com.example.greenharbour.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenharbour.Models.Events

class EventVIewModel:ViewModel() {

    private var eventData= MutableLiveData<Events>()
    val event : LiveData<Events> get() = eventData

    fun setData(event:Events){
        eventData.value = event
    }

}