
package edu.vanderbilt.vm.guide.ui.adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.util.Geomancer;

public class AgendaAdapter extends BaseAdapter {

    private Context mContext;

    private Agenda mAgenda;

    public AgendaAdapter(Context context, Agenda agenda) {
        mContext = context;
        mAgenda = agenda;
    }

    @Override
    public int getCount() {
        return mAgenda.size();
    }

    @Override
    public Object getItem(int position) {
        return mAgenda.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mAgenda.get(position).getUniqueId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;
        if (convertView == null) {
            layout = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.place_list_item,
                    null);
            layout.setTag(layout);
        } else {
            layout = (LinearLayout)convertView.getTag();
        }

        ((TextView)layout.findViewById(R.id.placelist_item_title)).setText(mAgenda.get(position)
                .getName());
        
        ((ImageView)layout.findViewById(R.id.placelist_item_thunbnail))
                .setImageResource(R.drawable.home);
        

        Location tmp = new Location("Temp");
        tmp.setLatitude(mAgenda.get(position).getLatitude());
        tmp.setLongitude(mAgenda.get(position).getLongitude());

        ((TextView)layout.findViewById(R.id.placelist_item_distance)).setText(Geomancer
                .getDistanceString(tmp));

        return layout;
    }

}
