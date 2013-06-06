
package edu.vanderbilt.vm.guide.ui.adapter;

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
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.ui.adapter.CursorIndexerFactory.CursorIndexer;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.Geomancer;

public class IndexedCursorAdapter extends BaseAdapter {

    private final Cursor mCursor;

    private final Context mContext;

    private final CursorIndexer mIndexer;
    
    private int mIdColIx;

    private int mNameColIx;

    private int mCatColIx;

    private int mLatColIx;

    private int mLngColIx;

    public static final int SORT_ALPHABETICALLY = 0;
    public static final int SORT_BY_DISTANCE = 1;
    public static final int SORT_BY_CATEGORY = 2;
    /**
     * Do not call this constructor.
     * 
     * @throws Exception
     */
    public IndexedCursorAdapter() throws Exception {
        throw new Exception("Do not call this constructor");
    }

    public IndexedCursorAdapter(Context context, Cursor cursor, int sort) {
        mContext = context;
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
        
        switch (sort) {
        case SORT_ALPHABETICALLY:
            mIndexer = CursorIndexerFactory.getAlphabeticalIndexer(context, cursor, mNameColIx);
            break;
            
        case SORT_BY_CATEGORY:
            mIndexer = CursorIndexerFactory.getCategoricalIndexer(context, cursor, mCatColIx);
            break;
            
        case SORT_BY_DISTANCE:
            mIndexer = CursorIndexerFactory.getDistanceIndexer(context, cursor, mLatColIx, mLngColIx);
            break;
            
        default:
            mIndexer = null;
        }
        
    }

    public static String[] getExpectedProjection() {
        return new String[] {
                GuideDBConstants.PlaceTable.ID_COL, GuideDBConstants.PlaceTable.NAME_COL,
                GuideDBConstants.PlaceTable.CATEGORY_COL, GuideDBConstants.PlaceTable.LATITUDE_COL,
                GuideDBConstants.PlaceTable.LONGITUDE_COL
        };
    }

    @Override
    public int getCount() {
        return mIndexer.categoriesCount() + mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        checkPosition(position);
        mCursor.moveToPosition(mIndexer.getDBRow(position));
        return DBUtils.getPlaceFromCursor(mCursor);
    }

    @Override
    public long getItemId(int position) {
        checkPosition(position);

        if (mIndexer.isHeader(position)) {
            return -1;
        }

        mCursor.moveToPosition(mIndexer.getDBRow(position));
        return mCursor.getInt(mIdColIx);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        checkPosition(position);
        
        
        LinearLayout layout;
        if (convertView == null) {
            layout = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.place_list_item,
                    null);
            layout.setTag(layout);
        } else {
            layout = (LinearLayout)convertView.getTag();
        }

        
        if (mIndexer.isHeader(position)) {
            layout.findViewById(R.id.placelist_item_header).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.placelist_item_item).setVisibility(View.GONE);
            
            ((TextView)layout.findViewById(R.id.header_title)).setText(
                    mIndexer.getHeaderTitle(position));

        } else {
            layout.findViewById(R.id.placelist_item_header).setVisibility(View.GONE);
            layout.findViewById(R.id.placelist_item_item).setVisibility(View.VISIBLE);

            
            mCursor.moveToPosition(mIndexer.getDBRow(position));
            ((TextView)layout.findViewById(R.id.placelist_item_title)).setText(mCursor
                    .getString(mNameColIx));
            
            
            // TODO replace placeholder icon with categorical icon
            ((ImageView)layout.findViewById(R.id.placelist_item_thunbnail))
                    .setImageResource(R.drawable.home);

            
            Location tmp = new Location("Temp");
            tmp.setLatitude(Double.parseDouble(mCursor.getString(mLatColIx)));
            tmp.setLongitude(Double.parseDouble(mCursor.getString(mLngColIx)));
            ((TextView)layout.findViewById(R.id.placelist_item_distance)).setText(
                    Geomancer.getDistanceString(tmp));
        }

        return layout;
    }

    private void checkPosition(int position) {
        if (position < 0 || position >= (mCursor.getCount() + mIndexer.categoriesCount())) {
            throw new IndexOutOfBoundsException("Position " + position
                    + " is invalid for a cursor with " + getCount() + "rows.");
        }
    }

}













