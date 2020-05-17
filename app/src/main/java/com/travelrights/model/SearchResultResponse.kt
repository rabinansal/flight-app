package com.travelrights.model

import com.google.gson.annotations.SerializedName

data class SearchResultResponse(

	@field:SerializedName("proposals")
	val proposals: List<ProposalsItem?>? = null
)