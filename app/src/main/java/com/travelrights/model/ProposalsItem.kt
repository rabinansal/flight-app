package com.travelrights.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

 class ProposalsItem(

	@field:SerializedName("segment")
	val segment: ArrayList<SegmentItem>? = null,
	@field:SerializedName("terms")
	@Expose val result: Map<String,Terms>? = null,
	@field:SerializedName("segment_durations")
	val segmentDurations: List<Int?>? = null
)