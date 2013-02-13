
package edu.vanderbilt.vm.guide.ui.listener;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import edu.vanderbilt.vm.guide.ui.PlaceDetailer;

public class PlaceListClickListener implements AdapterView.OnItemClickListener {

    private Context mContext;

    public PlaceListClickListener(Context context) {
        mContext = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int x = (int)parent.getItemIdAtPosition(position);
        if (x > 0) {
            PlaceDetailer.open(mContext, x);
        } else {
            return;
        }

    }

}
