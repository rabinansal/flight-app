package com.travelrights.model

import com.google.gson.annotations.SerializedName

data class Gatesmember(

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("currency_code")
	val currencyCode: String? = null
)
