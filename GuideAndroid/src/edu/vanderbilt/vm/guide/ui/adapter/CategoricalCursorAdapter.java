
package edu.vanderbilt.vm.guide.ui.adapter;

import java.util.ArrayList;

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

    private ArrayList<Integer> mEnigma;

    private int CATEGORIES;

    private ArrayList<HeaderRecord> mRecord;

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("ui.PlaceTabFragment");

    private static int categoryOffset = 0;

    public CategoricalCursorAdapter() throws Exception {
        throw new Exception("Do not call this constructor");
    }

    public CategoricalCursorAdapter(Context ctx, Cursor cursor) {
        mCtx = ctx;
        mCursor = cursor;

        mIdColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.ID_COL);
        if (mIdColIx == -1) {
            throw new SQLiteException("Cursor does not have an id column");
        }
        mNameColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.NAME_COL);
        if (mNameColIx == -1) {
            throw new SQLiteException("Cursor does not have a name column");
        }
        mCatColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.CATEGORY_COL);
        if (mCatColIx == -1) {
            throw new SQLiteException("Cursor does not have a category column");
        }
        mLatColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.LATITUDE_COL);
        if (mLatColIx == -1) {
            throw new SQLiteException("Cursor does not have a latitude column");
        }
        mLngColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.LONGITUDE_COL);
        if (mLngColIx == -1) {
            throw new SQLiteException("Cursor does not have a longitude column");
        }

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

        LinearLayout layout = null;
        if (convertView == null) {
            layout = (LinearLayout) LayoutInflater.from(mCtx).inflate(R.layout.place_list_item, null);
            layout.setTag(layout);
            
        } else {
            layout = (LinearLayout)convertView.getTag();
        }
        
        
        int x = 0;
        x = mEnigma.get(position);

        
        if (x < 0) { // isHeader
            layout.findViewById(R.id.placelist_item_header).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.placelist_item_item).setVisibility(View.GONE);

            ((TextView)layout.findViewById(R.id.header_title)).setText(
                    mRecord.get(-x - 1).mTitle);

        } else {
            layout.findViewById(R.id.placelist_item_header).setVisibility(View.GONE);
            layout.findViewById(R.id.placelist_item_item).setVisibility(View.VISIBLE);

            
            mCursor.moveToPosition(x);
            ((TextView)layout.findViewById(R.id.placelist_item_title)).setText(
                    mCursor.getString(mNameColIx));

            
            // TODO replace placeholder icon with categorical icon
            ((ImageView)layout.findViewById(R.id.placelist_item_thunbnail))
                    .setImageResource(R.drawable.home);

            
            Location tmp = new Location("Temp");
            tmp.setLatitude(Double.parseDouble(mCursor.getString(mLatColIx)));
            tmp.setLongitude(Double.parseDouble(mCursor.getString(mLngColIx)));
            ((TextView)layout.findViewById(R.id.placelist_item_distance)).setText(Geomancer
                    .getDistanceString(tmp));
        }

        return layout;
    }

    private void checkPosition(int position) {
        if (position < 0 || position >= mCursor.getCount() + CATEGORIES + categoryOffset) {
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

    // iterates through the database and make an index
    private void scanningDatabase() {

        if (mCursor.moveToFirst()) {

            String catStr;
            boolean isCat;
            
            do {
                
                catStr = mCursor.getString(mCatColIx);
                isCat = false;
                //logger.info("Category String from database: " + catStr);
                for (int i = 0; i < mRecord.size() - 1; i++) {
                    
                    if (catStr.equalsIgnoreCase(mRecord.get(i).mCat.text())) {
                        mRecord.get(i).mChild.add(mCursor.getPosition());
                        isCat = true;
                        break;
                    }
                    
                }
                
                if (!isCat) { // add to final category
                    mRecord.get(mRecord.size() - 1).mChild.add(mCursor.getPosition());
                }
                
            } while (mCursor.moveToNext());
        }
        
    }

    // Build HashMap based of the information stored in mRecord
    private void buildMap() {

        int listPosition = 0;
        mEnigma = new ArrayList<Integer>();
        
        // DEBUG
        //logger.info("Size of Records: " + mRecord.size());
        
        for (int i = 0; i < mRecord.size(); i++) {

            if (mRecord.get(i).mChild.size() == 0) {
                //logger.info("Size of Records' child: " + mRecord.get(i).mChild.size());
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

    public static class HeaderRecord {

        int mPosition;
        final String mTitle;
        final PlaceCategories mCat;
        final ArrayList<Integer> mChild;

        public HeaderRecord(PlaceCategories d) {
            mPosition = 0;
            mCat = d;
            logger.info("text of Enum: " + mCat.text());
            mTitle = d.text();
            mChild = new ArrayList<Integer>();
        }

    }

}
