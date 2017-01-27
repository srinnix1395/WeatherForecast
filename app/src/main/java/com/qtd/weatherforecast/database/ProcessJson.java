package com.qtd.weatherforecast.database;

import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.model.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dell on 5/6/2016.
 */
public class ProcessJson {
	public static ArrayList<Location> getLocationAutocomplete(JSONObject response) {
		ArrayList<Location> arrayList = new ArrayList<>();
		try {
			JSONArray array = response.getJSONArray(ApiConstant.RESULTS);
			JSONObject object;
			for (int i = 0; i < array.length(); i++) {
				object = array.getJSONObject(i);
				if ((object.length() == 10 || object.length() == 9) && object.getString(ApiConstant.TYPE).equals(ApiConstant.CITY)) {
					arrayList.add(new Location(object.getString("name"), object.getString("lat") + "," + object.getString("lon")));
				}
			}
		} catch (JSONException je) {
			je.printStackTrace();
		}
		return arrayList;
	}
}
