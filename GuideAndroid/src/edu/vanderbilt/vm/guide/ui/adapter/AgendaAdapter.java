package edu.vanderbilt.vm.guide.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.vanderbilt.vm.guide.container.Agenda;

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
		TextView tv;
		if (convertView == null) {
			tv = (TextView) LayoutInflater.from(mContext).inflate(
					android.R.layout.simple_list_item_1, null);
			tv.setTag(tv);
		} else {
			tv = (TextView) convertView.getTag();
		}
		tv.setText(mAgenda.get(position).getName());
		return tv;
	}

	
	
}
