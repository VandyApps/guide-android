package edu.vanderbilt.vm.guide.ui;

import edu.vanderbilt.vm.guide.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class NavTourChooser extends Fragment implements OnClickListener {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigator_chooser2, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // Setup UI
        View view = this.getView();
        ((Button) view.findViewById(R.id.nav_et1)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.nav_btn)).setOnClickListener(this);
        
    }
    
    @Override
    public void onClick(View v) {
        
        switch (v.getId()) {
            case R.id.nav_et1:
                
                return;
                
            case R.id.nav_btn:
                
                return;
                
            default:
                return;
        }
    }
    
}
