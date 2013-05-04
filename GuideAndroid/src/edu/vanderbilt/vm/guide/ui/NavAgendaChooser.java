package edu.vanderbilt.vm.guide.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import edu.vanderbilt.vm.guide.R;

public class NavAgendaChooser extends Fragment implements OnClickListener{
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigator_chooser3, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);        
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
