package com.travelrights.model

import com.google.gson.annotations.SerializedName

data class FlightItem(

	@field:SerializedName("departure_timestamp")
	val departureTimestamp: Int? = null,

	@field:SerializedName("arrival_time")
	val arrivalTime: String? = null,

	@field:SerializedName("local_arrival_timestamp")
	val localArrivalTimestamp: Int? = null,

	@field:SerializedName("arrival")
	val arrival: String? = null,

	@field:SerializedName("trip_class")
	val tripClass: String? = null,

	@field:SerializedName("aircraft")
	val aircraft: String? = null,

	@field:SerializedName("departure_date")
	val departureDate: String? = null,

	@field:SerializedName("rating")
	val rating: Double? = null,

	@field:SerializedName("arrival_timestamp")
	val arrivalTimestamp: Int? = null,

	@field:SerializedName("equipment")
	val equipment: String? = null,

	@field:SerializedName("operating_carrier")
	val operatingCarrier: String? = null,

	@field:SerializedName("operated_by")
	val operatedBy: String? = null,

	@field:SerializedName("seats")
	val seats: Int? = null,

	@field:SerializedName("marketing_carrier")
	val marketingCarrier: String? = null,

	@field:SerializedName("duration")
	val duration: Int? = null,

	@field:SerializedName("local_departure_timestamp")
	val localDepartureTimestamp: Int? = null,

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("delay")
	val delay: Int? = null,

	@field:SerializedName("technical_stops")
	val technicalStops: Any? = null,

	@field:SerializedName("departure")
	val departure: String? = null,

	@field:SerializedName("arrival_date")
	val arrivalDate: String? = null,

	@field:SerializedName("departure_time")
	val departureTime: String? = null
)