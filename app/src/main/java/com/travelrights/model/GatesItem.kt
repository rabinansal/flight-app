package com.travelrights.model

import com.google.gson.annotations.SerializedName

data class GatesItem(


	@field:SerializedName("duration")
	val duration: Double? = null,

	@field:SerializedName("gate_label")
	val gateLabel: String? = null,

	@field:SerializedName("id")
	val id: Int? = null

)