package com.travelrights.model

import com.google.gson.annotations.SerializedName

data class Detailed(

	@field:SerializedName("arrival_time")
	val arrivalTime: Double? = null,

	@field:SerializedName("transfer")
	val transfer: Double? = null,

	@field:SerializedName("departure_time")
	val departureTime: Double? = null
)