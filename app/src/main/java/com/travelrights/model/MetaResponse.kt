package com.travelrights.model

import com.google.gson.annotations.SerializedName

data class MetaResponse(

	@field:SerializedName("gates")
	val gates: List<GatesItem?>? = null

)