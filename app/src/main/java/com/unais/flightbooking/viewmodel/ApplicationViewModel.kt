package com.unais.flightbooking.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.unais.flightbooking.model.AirportResponse

class ApplicationViewModel (application: Application) : AndroidViewModel(application) {


    var appRepository: AppRepository;
    private var applicationContext: Application = application;

    init {
        appRepository = AppRepository.getInstance(application)

    }

    fun getAirportlist(term: String): MutableLiveData<List<AirportResponse>> {
        return appRepository.airportlist(term)
    }

}