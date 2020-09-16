package com.travelrights.viewmodel



import com.google.gson.JsonObject
import com.travelrights.model.AirportResponse
import com.travelrights.model.Flight_searchResponse
import com.travelrights.model.GeturlResponse
import com.travelrights.model.SearchResultResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


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

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST
    fun flight_search(@Url url: String?, @Body task: JsonObject?): Call<Flight_searchResponse>

    @GET
    fun flight_search_result(@Url url: String?):Call<List<SearchResultResponse>>
    @GET
    fun urlresult(@Url url: String?):Call<GeturlResponse>

}