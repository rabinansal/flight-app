package com.travelrights.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.travelrights.model.AirportResponse
import com.travelrights.model.Flight_searchResponse
import com.travelrights.viewmodel.Api.Constants.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppRepository (application: Application) {
    private val application: Application? = application

    companion object {

        @Volatile private var instance: AppRepository? = null
        fun getInstance(application: Application) =
                instance
                    ?: synchronized(this) {
                    instance
                        ?: AppRepository(
                            application
                        ).also { instance = it }
                }
    }

    val searchLiveData = MutableLiveData<List<AirportResponse>>()
    fun airportlist(term:String): MutableLiveData<List<AirportResponse>> {

        val apiService = Api.getclient().create(Api::class.java)
        val url ="https://autocomplete.travelpayouts.com/places2?term=$term&locale=en&types[]= city"
        val call = apiService.airportList(url)
            call.enqueue(object : Callback<List<AirportResponse>> {
                override fun onResponse(call: Call<List<AirportResponse>>, response: Response<List<AirportResponse>>) {

                    if (response.isSuccessful) {
                        searchLiveData.value=response.body()

                    }

                }
                override fun onFailure(call: Call<List<AirportResponse>>, t: Throwable) {

                }
            })


        return searchLiveData
    }
    val flight_searchLiveData = MutableLiveData<Flight_searchResponse>()
    fun flight_search(jobj: JsonObject): MutableLiveData<Flight_searchResponse> {

        val apiService = Api.getclient().create(Api::class.java)
        val url =BASE_URL+"flight_search"
        val call = apiService.flight_search(url,jobj)
        call.enqueue(object : Callback<Flight_searchResponse> {
            override fun onResponse(call: Call<Flight_searchResponse>, response: Response<Flight_searchResponse>) {

                if (response.isSuccessful) {
                    flight_searchLiveData.value=response.body()

                }
                else{
                    Toast.makeText(application,""+response.errorBody(), Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(call: Call<Flight_searchResponse>, t: Throwable) {
                Toast.makeText(application,t.message, Toast.LENGTH_SHORT).show()
            }
        })

        return flight_searchLiveData
    }

}
