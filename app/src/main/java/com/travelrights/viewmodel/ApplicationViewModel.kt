package com.travelrights.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.travelrights.model.AirportResponse
import com.travelrights.model.Flight_searchResponse

class ApplicationViewModel (application: Application) : AndroidViewModel(application) {


    var appRepository: AppRepository;
    private var applicationContext: Application = application;

    init {
        appRepository =
            AppRepository.getInstance(
                application
            )

    }

    fun getAirportlist(term: String): MutableLiveData<List<AirportResponse>> {
        return appRepository.airportlist(term)
    }
    fun flight_search(jobj:JsonObject): MutableLiveData<Flight_searchResponse> {
        return appRepository.flight_search(jobj)
    }
}