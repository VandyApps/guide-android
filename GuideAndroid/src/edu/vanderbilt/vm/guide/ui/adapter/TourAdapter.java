package edu.vanderbilt.vm.guide.ui.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.util.DBUtils;

public class TourAdapter extends BaseAdapter {

	public static final int NO_ID = -1;
	private static final Logger logger = LoggerFactory
			.getLogger("ui.adapter.TourAdapter");

	private Cursor mCursor;
	private GuideDBOpenHelper mHelper;
	private Context mContext;

	public TourAdapter(Context context, Cursor tourCursor,
			GuideDBOpenHelper helper) {
		mCursor = tourCursor;
		mHelper = helper;
		mContext = context;
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
		if (mCursor.moveToPosition(position) && index != -1) {
			return mCursor.getInt(index);
		} else {
			return NO_ID;
		}
	}

	private static final class ViewHolder {
		ImageView iv;
		TextView tv;
		int imageColIx;
		int nameColIx;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;

		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.tour_grid_item, null);
		} else {
			view = convertView;
		}

		if (view.getTag() == null || !(view.getTag() instanceof ViewHolder)) {
			holder = new ViewHolder();
			holder.iv = (ImageView) view.findViewById(R.id.tourGridItemIV);
			holder.tv = (TextView) view.findViewById(R.id.tourGridItemTV);
			holder.imageColIx = mCursor
					.getColumnIndex(GuideDBConstants.TourTable.ICON_LOC_COL);
			holder.nameColIx = mCursor
					.getColumnIndex(GuideDBConstants.TourTable.NAME_COL);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if (mCursor.moveToPosition(position)) {
			String imageLoc = null;
			String name = null;
			if (holder.imageColIx != -1) {
				imageLoc = mCursor.getString(holder.imageColIx);
			}
			if (holder.nameColIx != -1) {
				name = mCursor.getString(holder.nameColIx);
			}

			// For now we're not supporting tours with icons that need to be
			// downloaded from the web. We will probably never need this
			// functionality anyway.
			int imageResource;
			if (imageLoc == null) {
				logger.warn(
						"Row {} in tour cursor has a null image location.  "
								+ "Using default icon.", position);
				imageResource = R.drawable.tour_placeholder;
			} else {
				String packageName = mContext.getApplicationContext()
						.getPackageName();
				imageResource = mContext.getResources().getIdentifier(imageLoc,
						"drawable", packageName);
			}
			holder.iv.setImageResource(imageResource);

			if (name == null) {
				logger.warn("Row {} in tour cursor has a null tour name");
				name = "Unnamed Tour";
			}
			holder.tv.setText(name);
		} else {
			throw new IndexOutOfBoundsException();
		}
		return view;
	}

}
