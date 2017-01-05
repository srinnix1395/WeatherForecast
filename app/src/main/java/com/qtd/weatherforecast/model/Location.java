package com.qtd.weatherforecast.model;

/**
 * Created by DELL on 1/5/2017.
 */

public class Location {
	private String name;
	private String lat;
	private String lng;
	
	public Location(String name, String lat, String lng) {
		this.name = name;
		this.lat = lat;
		this.lng = lng;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLat() {
		return lat;
	}
	
	public void setLat(String lat) {
		this.lat = lat;
	}
	
	public String getLng() {
		return lng;
	}
	
	public void setLng(String lng) {
		this.lng = lng;
	}
}
