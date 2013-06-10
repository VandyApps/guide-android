
package edu.vanderbilt.vm.guide.ui;

import com.actionbarsherlock.app.SherlockFragment;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.ui.adapter.AgendaAdapter;
import edu.vanderbilt.vm.guide.util.GlobalState;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class StatsFragment extends SherlockFragment {
    TextView tvHistory;

    ListView listHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TextView)getActivity().findViewById(R.id.tv_history)).setText("History");

        listHistory = (ListView)getActivity().findViewById(R.id.list_history);
        listHistory.setAdapter(new AgendaAdapter(getActivity(), GlobalState.getUserHistory(), null));
        listHistory.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int posi, long id) {
                Place place = (Place)listHistory.getItemAtPosition(posi);
                if (place.getUniqueId() == 1000) {
                    Toast.makeText(getActivity(), "History is Empty. Please explore the Campus!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // Intent i = new Intent().setClass(getActivity(),
                // PlaceDetailer.class).putExtra(
                // GuideConstants.PLACE_ID_EXTRA, place.getUniqueId());
                // startActivity(i);
            }

        });

    }
}
