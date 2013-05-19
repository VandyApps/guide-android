package edu.vanderbilt.vm.guide.ui.adapter;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.Geomancer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class DistanceCursorAdapter extends BaseAdapter {
	
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory
            .getLogger("ui.PlaceTabFragment");
    
	private final Cursor mCursor;
	private final Context mCtx;
	private int mIdColIx;
	private int mNameColIx;
	private int mCatColIx;
	private int mLatColIx;
	private int mLngColIx;
	
	private ArrayList<Integer> mEnigma;
	private int CATEGORIES;
	private ArrayList<HeaderRecord> mRecord = new ArrayList<HeaderRecord>();
	private static int categoryOffset = 0;
	
	
	
	public DistanceCursorAdapter() throws Exception {
		throw new Exception("Do not call this constructor");
	}
	
	public DistanceCursorAdapter(Context ctx, Cursor cursor) {
		mCtx = ctx;
		mCursor = cursor;
		
		mIdColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.ID_COL);
		if(mIdColIx == -1) {
			throw new SQLiteException("Cursor does not have an id column");
		}
		mNameColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.NAME_COL);
		if(mNameColIx == -1) {
			throw new SQLiteException("Cursor does not have a name column");
		}
		mCatColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.CATEGORY_COL);
		if(mCatColIx == -1) {
			throw new SQLiteException("Cursor does not have a category column");
		}
		mLatColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.LATITUDE_COL);
		if(mLatColIx == -1) {
			throw new SQLiteException("Cursor does not have a latitude column");
		}
		mLngColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.LONGITUDE_COL);
		if(mLngColIx == -1) {
			throw new SQLiteException("Cursor does not have a longitude column");
		}
		
		
		buildMap();
	}

	@Override
	public int getCount() {
		return mCursor.getCount() + CATEGORIES + categoryOffset;
	}

	@Override
	public Object getItem(int position) {
		checkPosition(position);
		
		int x = mEnigma.get(position);
		while (x < 0) {
			position++;
			x = mEnigma.get(position);
		}
		
		mCursor.moveToPosition(x);
		return DBUtils.getPlaceFromCursor(mCursor);
	}

	@Override
	public long getItemId(int position) {
		checkPosition(position);
		int x = mEnigma.get(position);
		
		if (x < 0) {
			return x;
		} else {
			mCursor.moveToPosition(x);
			return mCursor.getInt(mIdColIx);
		}
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		checkPosition(position);
		
		LinearLayout layout = null;
		if (convertView == null) {
		    layout = (LinearLayout)LayoutInflater.from(mCtx).inflate(R.layout.place_list_item,
                    null);
            layout.setTag(layout);
        } else {
            layout = (LinearLayout)convertView.getTag();
		}
		
		
		int x = 0;
        x = mEnigma.get(position);
		
		if (x < 0) {
		    layout.findViewById(R.id.placelist_item_header).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.placelist_item_item).setVisibility(View.GONE);
			
			((TextView) layout.findViewById(R.id.header_title)).setText(
			        mRecord.get(-x - 1).mTitle);
			
		} else {
		    layout.findViewById(R.id.placelist_item_header).setVisibility(View.GONE);
            layout.findViewById(R.id.placelist_item_item).setVisibility(View.VISIBLE);
			
			mCursor.moveToPosition(x);
			((TextView) layout.findViewById(R.id.placelist_item_title)).setText(
					mCursor.getString(mNameColIx));
			
	        // TODO replace placeholder with categorical icon
			((ImageView) layout.findViewById(R.id.placelist_item_thunbnail))
				.setImageResource(R.drawable.home);
			
			Location tmp = new Location("Temp");
			tmp.setLatitude(Double.parseDouble(mCursor.getString(mLatColIx)));
			tmp.setLongitude(Double.parseDouble(mCursor.getString(mLngColIx)));
			((TextView) layout.findViewById(R.id.placelist_item_distance))
				.setText(Geomancer.getDistanceString(tmp));
		}
		
		return layout;
	}
	
	private void checkPosition(int position) {
		if (position < 0 || position >= mCursor.getCount() 
				+ CATEGORIES + categoryOffset) {
			throw new IndexOutOfBoundsException("Position " + position
					+ " is invalid for a cursor with " + getCount() + "rows.");
		}
	}
	
	private void buildMap(){
		

		// Initializing the Header records
		mRecord.add(new HeaderRecord("100 ft", 30.5));
		mRecord.add(new HeaderRecord("200 ft", 61));
		mRecord.add(new HeaderRecord("400 ft", 122));
		mRecord.add(new HeaderRecord("800 ft", 244));
		mRecord.add(new HeaderRecord("1000 ft", 304.8));
		mRecord.add(new HeaderRecord("0.3 mi", 483));
		mRecord.add(new HeaderRecord("0.6 mi", 965.6));
		mRecord.add(new HeaderRecord("1.2 mi", 1931));
		mRecord.add(new HeaderRecord("2.4 mi", 3862));
		mRecord.add(new HeaderRecord("In a galaxy far far away", 10000000));
		CATEGORIES = mRecord.size();

		
		
		// Scanning the database to index
		if (mCursor.moveToFirst()) {
		

    		Location current = Geomancer.getDeviceLocation();
    		Location tmp = new Location("Temp");
    		
    		do {
    			
                tmp.setLatitude(Double.parseDouble(mCursor.getString(mLatColIx)));
                tmp.setLongitude(Double.parseDouble(mCursor.getString(mLngColIx)));
    			
    			for (int i = 0; i < mRecord.size();i++) {
    				if (current.distanceTo(tmp) < mRecord.get(i).mDist) {
    					mRecord.get(i).mChild.add(mCursor.getPosition());
    					break;
    				}
    			}
    			
    		} while (mCursor.moveToNext());
    
    		
    		
    		// Build HashMap based of the information stored in mRecord
    		int listPosition = 0;
    		mEnigma = new ArrayList<Integer>();
    		
    		for (int i = 0; i < mRecord.size(); i++) {
    			
    			if (mRecord.get(i).mChild.size() == 0) {
    				categoryOffset--;
    
    			} else {
        			mRecord.get(i).mPosition = listPosition;
        			mEnigma.add(listPosition, -(i + 1));
        			listPosition++;
        			
        			for (Integer child : mRecord.get(i).mChild) {
        			    mEnigma.add(listPosition, child);
        				listPosition++;
        			}
    			}
    			
    		}
		}
	}
	
	static class HeaderRecord {
		
		int mPosition;
		final double mDist;     // in meters
		final String mTitle;
		final ArrayList<Integer> mChild;
		
		public HeaderRecord(String s, double d) {
			mPosition = 0;
			mTitle = s;
			mDist = d;
			mChild = new ArrayList<Integer>();
		}

	}
	
}
