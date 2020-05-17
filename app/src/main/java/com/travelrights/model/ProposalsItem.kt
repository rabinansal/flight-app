package com.travelrights.model

import com.google.gson.annotations.SerializedName

data class ProposalsItem(

	@field:SerializedName("segment")
	val segment: List<SegmentItem?>? = null
)