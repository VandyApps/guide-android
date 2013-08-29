package edu.vanderbilt.vm.guide.ui.adapter;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.util.Geomancer;

/**
 * Date: 6/5/13
 * Time: 7:12 PM
 */
public class PlaceItemView extends ItemView {

    public PlaceItemView(Context ctx) {
        super(ctx);

        View.inflate(ctx, R.layout.place_list_item, this);

        mName = (TextView) findViewById(R.id.placelist_item_title);
        mIcon = (ImageView) findViewById(R.id.placelist_item_thumbnail);
        mDistance = (TextView) findViewById(R.id.placelist_item_distance);

    }

    /**
     * <p>
     * Instruct this object to morph itself according to a data object passed into it. This method's signature is made
     * to match the setView() of BaseAdapter, but only <code>obj</code> is important. View recycling should be done
     * in the adapter that uses this ItemView, so <code>convertView</code> is useless.
     * </p>
     * <p>
     * <code>indexer</code> is used if the ListView will be indexed. Otherwise, use null.
     * </p>
     *
     * @param obj         Data object to be detailed
     * @param position    This item's position in the list. Usually unused.
     * @param convertView view for recycling. leave nulled.
     * @param parent      the ViewGroup which contains this view. leave nulled.
     * @param indexer
     */
    @Override
    public void setView(
            Object obj,
            int position,
            View convertView,
            ViewGroup parent,
            CursorIndexerFactory.CursorIndexer indexer)
    {
        Place plc = (Place) obj;

        mName.setText(plc.getName());

        Location tmp = new Location("Temp");
        tmp.setLatitude(plc.getLatitude());
        tmp.setLongitude(plc.getLongitude());

        mDistance.setText(Geomancer.getDistanceString(tmp));

    }

    private TextView mName;
    private ImageView mIcon;
    private TextView mDistance;

    //private LinearLayout mHeader;
    //private LinearLayout mBody;


    public static ItemViewFactory getFactory() {
        return new ItemViewFactory() {
            @Override
            public ItemView getItemView(Context ctx) {
                return new PlaceItemView(ctx);
            }
        };
    }

}
