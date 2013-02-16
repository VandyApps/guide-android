package edu.vanderbilt.vm.guide.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GuideConstants.PlaceCategories;

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
public class CategoricalCursorAdapter extends BaseAdapter {
	
	private final Cursor mCursor;
	private final Context mCtx;
	private int mIdColIx;
	private int mNameColIx;
	private int mCatColIx;
	private int mLatColIx;
	private int mLngColIx;
	private HashMap<Integer,Integer> mEnigma;
	private int CATEGORIES;
	private ArrayList<HeaderRecord> mRecord;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory
			.getLogger("ui.PlaceTabFragment");
	private static int categoryOffset = 0;
	
	public CategoricalCursorAdapter() throws Exception {
		throw new Exception("Do not call this constructor");
	}
	
	public CategoricalCursorAdapter(Context ctx, Cursor cursor) {
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
		
		mEnigma = new HashMap<Integer,Integer>();
		
		initializeRecord();
		
		scanningDatabase();
		
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
		
		int x = 0;
		x = mEnigma.get(position);
		
		LinearLayout layout = null;
		if (x < 0) {
			layout = (LinearLayout) LayoutInflater.from(mCtx).inflate(
					R.layout.place_list_header,null);
			
			HeaderRecord record = mRecord.get(-x - 1);
			((TextView) layout.findViewById(R.id.header_title)).setText(
					record.mTitle);
			
		} else {
			layout = (LinearLayout) LayoutInflater.from(mCtx).inflate(
					R.layout.place_list_item,null);
			
			mCursor.moveToPosition(x);
			((TextView) layout.findViewById(R.id.placelist_item_title))
				.setText(
						mCursor.getString(mNameColIx));
			
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
	
	private void initializeRecord() {
	    mRecord = new ArrayList<HeaderRecord>();
	    
	    for (PlaceCategories c : PlaceCategories.values()) {
	        mRecord.add(new HeaderRecord(c));
	    }
	    
        CATEGORIES = mRecord.size();
	}
	
	private void scanningDatabase() {
	    // iterates through the database and make an index
        if (!mCursor.moveToFirst()) {
            return;
        }
        
        PlaceCategories c;
        do {
            
            
            
        } while (mCursor.moveToNext());
    
	}
	
	private void buildMap(){
		// Build HashMap based of the information stored in mRecord
		int listPosition = 0;
		for (int i = 0; i < mRecord.size(); i++) {
			
			if (mRecord.get(i).mChild.size() == 0) {
				categoryOffset--;
				continue;
			}
			
			mRecord.get(i).mPosition = listPosition;
			mEnigma.put(listPosition, -(i + 1));
			listPosition++;
			
			for (Integer child : mRecord.get(i).mChild) {
				mEnigma.put(listPosition, child);
				listPosition++;
			}
			
		}
			
	}
	
	public static class HeaderRecord {
		
		int mPosition;
		String mTitle;
		PlaceCategories mCat;
		ArrayList<Integer> mChild;
		
		public HeaderRecord(PlaceCategories d) {
			mPosition = 0;
			mCat = d;
			mTitle = d.text();
			mChild = new ArrayList<Integer>();
		}
		
		public HeaderRecord() {
			mPosition = 0;
			mCat = PlaceCategories.MISC;
			mTitle = "";
			mChild = new ArrayList<Integer>();
		}
		
	}
	
}
