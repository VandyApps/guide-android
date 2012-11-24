package edu.vanderbilt.vm.guide.ui.adapter;

import java.util.List;

import edu.vanderbilt.vm.guide.container.Place;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlaceListAdapter extends BaseAdapter {

	private List<Place> mPlaceList;
	private Context mContext;

	public PlaceListAdapter(Context context, List<Place> placeList) {
		mContext = context;
		mPlaceList = placeList;
	}

	@Override
	public int getCount() {
		return mPlaceList.size();
	}

	@Override
	public Object getItem(int position) {
		return mPlaceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mPlaceList.get(position).getUniqueId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv;
		if (convertView == null) {
			tv = (TextView) LayoutInflater.from(mContext).inflate(
					android.R.layout.simple_list_item_1, null);
			tv.setTag(tv);
		} else {
			tv = (TextView) convertView.getTag();
		}
		tv.setText(mPlaceList.get(position).getName());
		return tv;
	}

}
