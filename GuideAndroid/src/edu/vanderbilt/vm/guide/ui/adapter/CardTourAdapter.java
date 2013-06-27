package edu.vanderbilt.vm.guide.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Tour;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.ui.CyclingTourGridItem;
import edu.vanderbilt.vm.guide.util.DBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 6/25/13
 * Time: 5:56 PM
 */
public class CardTourAdapter extends BaseAdapter {

public CardTourAdapter(Context context, Cursor tourCursor, GuideDBOpenHelper helper) {
    mContext = context;
    mRecords = new ArrayList<TourRecord>(tourCursor.getCount());
    tourCursor.moveToFirst();

    do {
        TourRecord r = new TourRecord(
                DBUtils.getTourFromCursor(
                        tourCursor,
                        helper.getReadableDatabase()));

        logger.info("Tour#" + r.mTour.getUniqueId()  +  " : " + r.mTour.getAgenda().toString());

        mRecords.add(r);
         } /* CONFESSION BEAR : I LOVE LISP */

    while (tourCursor.moveToNext());
}

public static final int NO_ID = -1;
private static final Logger logger = LoggerFactory.getLogger("ui.adapter.CardTourAdapter");

private List<TourRecord> mRecords;

private Context mContext;

@Override
public int getCount() {
    return mRecords.size();
}

@Override
public Object getItem(int position) {
    return mRecords.get(position).mTour;
}

@Override
public long getItemId(int position) {
    return mRecords.get(position).mTour.getUniqueId();
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {


    CyclingTourGridItem view = (CyclingTourGridItem) View.inflate(mContext, R.layout.cycling_tour_grid_item, null);
    // Not doing recycling for now, because the potential for concurrency holocaust is just too great.
    /*if (convertView == null) {
        view = new CyclingTourGridItem(mContext); }

    else  {
        view = (CyclingTourGridItem) convertView; } */

    view.setView(mRecords.get(position));

    return view;
}

public static class TourRecord {
    public final Tour mTour;
    public final Drawable[] mImageList;

    public TourRecord(Tour tour) {
        mTour = tour;
        mImageList = new Drawable[tour.getAgenda().size()];
    }
}

}
