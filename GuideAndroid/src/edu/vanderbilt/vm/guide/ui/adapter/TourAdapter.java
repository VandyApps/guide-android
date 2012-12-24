package edu.vanderbilt.vm.guide.ui.adapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.util.DBUtils;

public class TourAdapter extends BaseAdapter {

	public static final int NO_ID = -1;

	private Cursor mCursor;
	private GuideDBOpenHelper mHelper;

	public TourAdapter(Cursor tourCursor, GuideDBOpenHelper helper) {
		mCursor = tourCursor;
		mHelper = helper;
	}

	@Override
	public int getCount() {
		return mCursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		if (mCursor.moveToPosition(position)) {
			SQLiteDatabase db = mHelper.getReadableDatabase();
			return DBUtils.getTourFromCursor(mCursor, db);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		int index = mCursor.getColumnIndex(GuideDBConstants.TourTable.ID_COL);
		if(mCursor.moveToPosition(position) && index != -1) {
			return mCursor.getInt(index);
		} else {
			return NO_ID;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
