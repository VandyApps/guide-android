package edu.vanderbilt.vm.guide.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;

/**
 * Cursor Adapter of Place cursors for AutoCompleteTextViews
 * @author nicholasking
 *
 */
public class AutoPlaceCursorAdapter extends BaseAdapter implements Filterable {
    
    private Cursor mCursor;
    private List<String> mFiltered = new ArrayList<String>();
    private Object mLock = new Object();
    private Filter mFilter;
    private Context mContext;
    private int mNameIx;
    private int mIdIx;

    public AutoPlaceCursorAdapter(Context context, Cursor c) {
        mContext = context;
        mCursor = c;
        mNameIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.NAME_COL);
        if (mNameIx == -1) {
            throw new SQLiteException("Cursor must have a name column");
        }
        mIdIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.ID_COL);
        if (mIdIx == -1) {
            throw new SQLiteException("Cursor must have an id column");
        }
        synchronized (mLock) {
            for (int i=0; i<mCursor.getCount(); i++) {
                mCursor.moveToPosition(i);
                mFiltered.add(mCursor.getString(mNameIx));
            }
        }
    }
    
    @Override
    public int getCount() {
        return mFiltered.size();
    }

    @Override
    public String getItem(int position) {
        return mFiltered.get(position);
    }

    /** 
     * Currently does nothing
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if (convertView != null) {
            tv = (TextView) convertView;
        } else {
            tv = new TextView(mContext);
        }
        tv.setText(mFiltered.get(position));
        return tv;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new MyFilter();
        }
        return mFilter;
    }
    
    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> matches = new ArrayList<String>();
            String prefix = constraint.toString().toLowerCase();
            synchronized (mLock) {
                for (int i=0; i<mCursor.getCount(); i++) {
                    mCursor.moveToPosition(i);
                    String name = mCursor.getString(mNameIx);
                    if (name.toLowerCase().startsWith(prefix)) {
                        matches.add(name);
                    }
                }
            }
            results.count = matches.size();
            results.values = matches;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                mFiltered = (List<String>)results.values;
                notifyDataSetChanged();
            }
        }
        
    }
    

}
