package edu.vanderbilt.vm.guide.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.ui.adapter.CardTourAdapter;
import edu.vanderbilt.vm.guide.util.ImageDownloader;

import java.util.Date;
import java.util.Random;

/**
 * Date: 6/25/13
 * Time: 5:32 PM
 */
public class CyclingTourGridItem extends RelativeLayout {

private ImageView mImage1;
private TextView mTourName;

private int mPosition;

private boolean mIsRunning;
private Random mGenerator;
private Handler mHandler;

private CardTourAdapter.TourRecord mRecord;

public CyclingTourGridItem(Context ctx) {
    super(ctx);
    init();
}

public CyclingTourGridItem(Context ctx, AttributeSet attrs) {
    super(ctx, attrs);
    init();
}

private void init() {

    mGenerator = new Random((new Date()).getTime());
    mIsRunning = false;
    mHandler = new Handler();
}

@Override
protected void onFinishInflate() {
    super.onFinishInflate();

    mImage1 = (ImageView) findViewById(R.id.ctgi_iv1);
    mTourName = (TextView) findViewById(R.id.ctgi_name);
}

@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
}

public void setView(String tourName) {
    mTourName.setText(tourName);
}

public void setView(CardTourAdapter.TourRecord record) {
    mRecord = record;
    mPosition = 0;
    mTourName.setText(record.mTour.getName());

    if (!mIsRunning && record.mImageList.length != 0) {
        mIsRunning = true;
        mHandler.post(mImageCycler); }

}

private Runnable mImageCycler = new Runnable() {
    @Override
    public void run() {

        if (mRecord.mImageList[mPosition] == null) {
            final int position = mPosition;

            ImageDownloader.BitmapDownloaderTask download = new ImageDownloader.BitmapDownloaderTask(mImage1);
            download.setRunOnFinishDownload(new Runnable() {
                @Override public void run() {
                    mRecord.mImageList[position] = mImage1.getDrawable();
                    postImageAdvance(); }});

            download.execute(getImageLoc(mPosition)); }

        else {
            mImage1.setImageDrawable(mRecord.mImageList[mPosition]);
            postImageAdvance(); }

    }
};

private void postImageAdvance() {
    mPosition++;

    if (mPosition == mRecord.mImageList.length) {
        mPosition = 0;
    }

    // post with a slight randomization, for giggles
    mHandler.postDelayed(mImageCycler, 4000 + mGenerator.nextInt(500));
}

private String getImageLoc(int position) {
    Agenda a = mRecord.mTour.getAgenda();
    Place p = a.get(position);

    if (p == null) {
        throw new IllegalStateException(
                "There is a null object in Tour#" + mRecord.mTour.getUniqueId() + " at position: " + position +
                        ". Agenda: " + mRecord.mTour.getAgenda().toString());
    }

    return p.getPictureLoc();
}

}
