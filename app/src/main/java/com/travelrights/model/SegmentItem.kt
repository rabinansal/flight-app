package com.travelrights.model

import com.google.gson.annotations.SerializedName

data class SegmentItem(

	@field:SerializedName("flight")
	val flight: List<FlightItem?>? = null,

	@field:SerializedName("rating")
	val rating: Rating? = null
)