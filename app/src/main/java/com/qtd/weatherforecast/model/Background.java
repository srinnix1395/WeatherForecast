package com.qtd.weatherforecast.model;

/**
 * Created by DELL on 1/18/2017.
 */

public class Background {
	private String name;
	private boolean isChosen;
	
	public Background(String name, boolean isChosen) {
		this.name = name;
		this.isChosen = isChosen;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isChosen() {
		return isChosen;
	}
	
	public void setChosen(boolean chosen) {
		isChosen = chosen;
	}
}
