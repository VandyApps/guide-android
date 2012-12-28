package edu.vanderbilt.vm.guide.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.util.DBUtils;

public class PlaceCursorAdapter extends BaseAdapter {

	private final Cursor mCursor;
	private final Context mContext;
	private int mIdColIx;
	private int mNameColIx;
	
	
	/**
	 * Do not call this constructor.
	 * @throws Exception
	 */
	public PlaceCursorAdapter() throws Exception {
		throw new Exception("Do not call this constructor");
	}

	public PlaceCursorAdapter(Context context, Cursor cursor) {
		mContext = context;
		mCursor = cursor;
		
		mIdColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.ID_COL);
		if(mIdColIx == -1) {
			throw new SQLiteException("Cursor does not have an id column");
		}
		mNameColIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.NAME_COL);
		if(mNameColIx == -1) {
			throw new SQLiteException("Cursor does not have a name column");
		}
	}

	@Override
	public int getCount() {
		return mCursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		checkPosition(position);
		mCursor.moveToPosition(position);
		return DBUtils.getPlaceFromCursor(mCursor);
	}

	@Override
	public long getItemId(int position) {
		checkPosition(position);
		mCursor.moveToPosition(position);
		return mCursor.getInt(mIdColIx);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		checkPosition(position);
		TextView tv;
		if (convertView == null) {
			tv = (TextView) LayoutInflater.from(mContext).inflate(
					android.R.layout.simple_list_item_1, null);
			tv.setTag(tv);
		} else {
			tv = (TextView) convertView.getTag();
		}
		mCursor.moveToPosition(position);
		tv.setText(mCursor.getString(mNameColIx));
		return tv;
	}
	
	private void checkPosition(int position) {
		if (position < 0 || position >= mCursor.getCount()) {
			throw new IndexOutOfBoundsException("Position " + position
					+ " is invalid for a cursor with " + getCount() + "rows.");
		}
	}

}
