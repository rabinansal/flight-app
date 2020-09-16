package com.travelrights.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Terms(

	@field:SerializedName("unified_price")
	var price: Int? = null,

	@field:SerializedName("currency")
    var currency: String? = null,
	@field:SerializedName("url")
	var url: String? = null


)

