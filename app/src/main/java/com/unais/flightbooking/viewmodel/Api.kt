package com.unais.flightbooking.viewmodel



import com.unais.flightbooking.model.AirportResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url


interface Api {

    companion object Constants {

        const val BASE_URL = "https://api.travelpayouts.com/v1/"
        lateinit var retrofit: Retrofit
        fun getclient(): Retrofit {

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit
        }

    }

    @GET
    fun airportList(@Url url: String?): Call<List<AirportResponse>>


}