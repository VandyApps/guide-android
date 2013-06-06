
package edu.vanderbilt.vm.guide.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import edu.vanderbilt.vm.guide.container.Agenda;

public class AgendaAdapter extends BaseAdapter {

    private Context mContext;

    private Agenda mAgenda;

    private ItemView.ItemViewFactory mFactory;

    public AgendaAdapter(Context context, Agenda agenda, ItemView.ItemViewFactory factory) {
        mContext = context;
        mAgenda = agenda;
        mFactory = factory;
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
        ItemView view;
        if (convertView == null) {
            view = mFactory.getItemView(mContext);

        } else {
            view = (ItemView) convertView;
        }

        view.setView(mAgenda.get(position), position, null, null, null);

        return view;
    }

}
