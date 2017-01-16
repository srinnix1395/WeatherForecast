package com.qtd.weatherforecast.model;

/**
 * Created by DELL on 1/5/2017.
 */

public class Location {
	private String name;
	private String coordinate;
	
	public Location(String name, String coordinate) {
		this.name = name;
		this.coordinate = coordinate;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCoordinate() {
		return coordinate;
	}
	
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
}
