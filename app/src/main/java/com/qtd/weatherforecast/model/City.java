package com.qtd.weatherforecast.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dell on 4/26/2016.
 */
public class City implements Parcelable{
    protected int id;
    protected String name;
    protected int temp;
    protected String weather;
    protected String coordinate;
    protected boolean isChosen;
    protected String fullName;
	protected String timeZone;
	
    public City(int id, String name, int temp, String weather, String coordinate, boolean isChosen, String fullName) {
        this.id = id;
        this.name = name;
        this.temp = temp;
        this.weather = weather;
        this.coordinate = coordinate;
        this.isChosen = isChosen;
        this.fullName = fullName;
    }

    public City(int id, String name, String coordinate) {
        this.id = id;
        this.name = name;
        this.coordinate = coordinate;
    }

    public City() {
        this.id = -1;
        this.name = "";
        this.coordinate = "";
    }
	
	protected City(Parcel in) {
		id = in.readInt();
		name = in.readString();
		temp = in.readInt();
		weather = in.readString();
		coordinate = in.readString();
		isChosen = in.readByte() != 0;
		fullName = in.readString();
		timeZone = in.readString();
	}
	
	public static final Creator<City> CREATOR = new Creator<City>() {
		@Override
		public City createFromParcel(Parcel in) {
			return new City(in);
		}
		
		@Override
		public City[] newArray(int size) {
			return new City[size];
		}
	};
	
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTemp() {
        return temp;
    }


    public String getWeather() {
        return weather;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
	
	public String getTimeZone() {
		return timeZone;
	}
	
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int i) {
		
		parcel.writeInt(id);
		parcel.writeString(name);
		parcel.writeInt(temp);
		parcel.writeString(weather);
		parcel.writeString(coordinate);
		parcel.writeByte((byte) (isChosen ? 1 : 0));
		parcel.writeString(fullName);
		parcel.writeString(timeZone);
	}
}
