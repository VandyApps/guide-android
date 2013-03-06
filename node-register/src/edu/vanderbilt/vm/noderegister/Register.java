
package edu.vanderbilt.vm.noderegister;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Register extends FragmentActivity implements OnClickListener{
    
    private Button mBtn1;
    
    private Button mBtn2;
    
    private static final String MAP = "map";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Setup UI
        mBtn1 = (Button) findViewById(R.id.btn1);
        mBtn2 = (Button) findViewById(R.id.btn2);
        
        // Load the nodes.json and setup Json reader
        
        // Setup Json writer
        JsonWriter jw;
        
        // Setup MapFragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        ft.add(R.id.map_container, new SupportMapFragment(), MAP);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_register, menu);
        return true;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        SupportMapFragment frag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentByTag(MAP);
        
        GoogleMap map = frag.getMap();
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(36.144782, -86.803231), 16));
        
        
    }
    
    @Override
    public void onClick(View v) {
        
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
}
