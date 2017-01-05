package com.qtd.weatherforecast.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.qtd.weatherforecast.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 6/2/2016.
 */
public class AutoCompleteTextViewLocationAdapter extends ArrayAdapter<Location> {
	private ArrayList<Location> arrayList;
	
	public AutoCompleteTextViewLocationAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Location> objects) {
		super(context, resource, objects);
		arrayList = (ArrayList<Location>) objects;
	}
	
	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.textView.setText(arrayList.get(position).getName());
		
		return convertView;
	}
	
	@Override
	public int getCount() {
		return arrayList.size();
	}
	
	@Override
	public Location getItem(int position) {
		return arrayList.get(position);
	}
	
	@Override
	public Filter getFilter() {
		Filter myFilter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
//					StyleFetcher fetcher = new StyleFetcher();
					try {
//						mData = fetcher.retrieveResults(constraint.toString());
					} catch (Exception e) {
						Log.e("myException", e.getMessage());
					}
					// Now assign the values and count to the FilterResults object
//					filterResults.values = mData;
//					filterResults.count = mData.size();
				}
				return filterResults;
			}
			
			@Override
			protected void publishResults(CharSequence contraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		return myFilter;
	}
	
	private class ViewHolder {
		private TextView textView;
		
		ViewHolder(View view) {
			textView = (TextView) view.findViewById(android.R.id.text1);
		}
	}
}
