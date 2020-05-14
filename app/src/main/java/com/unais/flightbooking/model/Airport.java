package com.unais.flightbooking.model;

import com.google.gson.annotations.SerializedName;

public class Airport{

	@SerializedName("country_code")
	private String countryCode;

	@SerializedName("code")
	private String code;

	@SerializedName("name_translations")
	private NameTranslations nameTranslations;

	@SerializedName("name")
	private String name;

	@SerializedName("coordinates")
	private Coordinates coordinates;

	@SerializedName("city_code")
	private String cityCode;

	@SerializedName("time_zone")
	private String timeZone;

	@SerializedName("flightable")
	private boolean flightable;

	public void setCountryCode(String countryCode){
		this.countryCode = countryCode;
	}

	public String getCountryCode(){
		return countryCode;
	}

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public void setNameTranslations(NameTranslations nameTranslations){
		this.nameTranslations = nameTranslations;
	}

	public NameTranslations getNameTranslations(){
		return nameTranslations;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setCoordinates(Coordinates coordinates){
		this.coordinates = coordinates;
	}

	public Coordinates getCoordinates(){
		return coordinates;
	}

	public void setCityCode(String cityCode){
		this.cityCode = cityCode;
	}

	public String getCityCode(){
		return cityCode;
	}

	public void setTimeZone(String timeZone){
		this.timeZone = timeZone;
	}

	public String getTimeZone(){
		return timeZone;
	}

	public void setFlightable(boolean flightable){
		this.flightable = flightable;
	}

	public boolean isFlightable(){
		return flightable;
	}

	@Override
 	public String toString(){
		return 
			"Airport{" + 
			"country_code = '" + countryCode + '\'' + 
			",code = '" + code + '\'' + 
			",name_translations = '" + nameTranslations + '\'' + 
			",name = '" + name + '\'' + 
			",coordinates = '" + coordinates + '\'' + 
			",city_code = '" + cityCode + '\'' + 
			",time_zone = '" + timeZone + '\'' + 
			",flightable = '" + flightable + '\'' + 
			"}";
		}
}