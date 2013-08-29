
package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.container.Route;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.ImageDownloader;

public class AgendaNavigatorFragment extends NavigatorFragment {

    private View mLocationView;

    private ImageView mLocationIv;

    private TextView mLocationDesc;

    private TextView mLocationName;

    private Agenda mUserAgenda = GlobalState.getUserAgenda();

    private Route mRoute;

    private ImageDownloader.BitmapDownloaderTask mDlTask = null;

    private final int DESCRIPTION_LENGTH = 100;

    private static final Logger logger = LoggerFactory.getLogger("ui.AgendaNavigatorFrag");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agenda_navigator, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View layout = getView();
        mLocationView = layout.findViewById(R.id.current_ll_ref);
        mLocationDesc = (TextView)mLocationView.findViewById(R.id.location_prev_desc);
        mLocationName = (TextView)mLocationView.findViewById(R.id.location_prev_name);
        mLocationName.setText("Next location:");
        mLocationIv = (ImageView)mLocationView.findViewById(R.id.current_img);
        mRoute = GlobalState.findRoute(Geomancer.getDeviceLocation(), mUserAgenda, getActivity());
        mMapper.mapGraph(mRoute.getRouteGraph());

        updatePlaceView();

        final Button hideButton = (Button)layout.findViewById(R.id.agenda_nav_hide);
        final Button visitButton = (Button)layout.findViewById(R.id.nav_mark_visited);
        hideButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLocationView.isShown()) {
                    mLocationView.setVisibility(View.GONE);
                    visitButton.setVisibility(View.GONE);
                    hideButton.setText("Show Panel");
                } else {
                    mLocationView.setVisibility(View.VISIBLE);
                    visitButton.setVisibility(View.VISIBLE);
                    hideButton.setText("Hide Panel");
                }
            }

        });

        visitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mRoute.proceed();
                updatePlaceView();
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDlTask != null) {
            logger.trace("Cancelling image download task");
            mDlTask.cancel(true);
        }
    }

    private void updatePlaceView() {
        if (!mRoute.isFinished()) {
            // Update to show next place on the route
            Place current = mRoute.getCurrentPlace();
            String desc = current.getDescription();
            if (desc.length() > DESCRIPTION_LENGTH) {
                desc = desc.substring(0, DESCRIPTION_LENGTH).concat("...");
            }

            mLocationDesc.setText(Html.fromHtml("<b>" + current.getName() + "</b> " + desc));

            mDlTask = new ImageDownloader.BitmapDownloaderTask(mLocationIv);
            logger.trace("Starting image download task");
            mDlTask.execute(current.getPictureLoc());
        } else {
            mLocationIv.setVisibility(View.GONE);
            mLocationName.setText("You have finished the tour");
            mLocationDesc.setVisibility(View.GONE);
        }
    }

}
