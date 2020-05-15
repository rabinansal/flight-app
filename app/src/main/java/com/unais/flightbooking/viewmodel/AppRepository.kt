package com.unais.flightbooking.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.unais.flightbooking.model.AirportResponse
import com.unais.flightbooking.viewmodel.Api.Constants.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppRepository (application: Application) {
    private val application2: Application? = application

    companion object {

        @Volatile private var instance: AppRepository? = null
        fun getInstance(application: Application) =
                instance ?: synchronized(this) {
                    instance ?: AppRepository(application).also { instance = it }
                }
    }

    val mutablecodesLiveData = MutableLiveData<List<AirportResponse>>()
    fun airportlist(term:String): MutableLiveData<List<AirportResponse>> {

        val apiService = Api.getclient().create(Api::class.java)
        val url ="https://autocomplete.travelpayouts.com/places2?term=$term&locale=en&types[]= city"
        val call = apiService.airportList(url)
            call.enqueue(object : Callback<List<AirportResponse>> {
                override fun onResponse(call: Call<List<AirportResponse>>, response: Response<List<AirportResponse>>) {

                    if (response.isSuccessful) {
                        mutablecodesLiveData.value=response.body()

                    }

                }
                override fun onFailure(call: Call<List<AirportResponse>>, t: Throwable) {

                }
            })


        return mutablecodesLiveData
    }


}
