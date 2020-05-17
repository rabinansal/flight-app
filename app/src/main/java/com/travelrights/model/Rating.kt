package com.travelrights.model

import com.google.gson.annotations.SerializedName

data class Rating(

	@field:SerializedName("total")
	val total: Double? = null,

	@field:SerializedName("detailed")
	val detailed: Detailed? = null
)