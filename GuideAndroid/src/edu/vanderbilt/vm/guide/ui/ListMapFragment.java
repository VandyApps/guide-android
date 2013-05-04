package edu.vanderbilt.vm.guide.ui;

import java.util.List;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ListMapFragment extends SupportMapFragment 
        implements OnMarkerClickListener, OnInfoWindowClickListener {
    
    public static ListMapFragment newInstance(List<Mappable> list) {
        ListMapFragment frag = new ListMapFragment();
        frag.mList = list;
        frag.mDetailer = null;
        frag.setRetainInstance(true);
        return frag;
    }
    
    public static ListMapFragment newInstance(List<Mappable> list, MappableDetailer detailer) {
        ListMapFragment frag = new ListMapFragment();
        frag.mList = list;
        frag.mDetailer = detailer;
        frag.setRetainInstance(true);
        return frag;
    }
    
    public interface Mappable {
        
        double getMapLatitude();
        
        double getMapLongitude();
        
    }
    
    public interface MappableDetailer {
        
        void getDetail(Mappable mapitem);
        
        void fillMarker(Mappable mapitem, MarkerOptions options);
        
    }
    
    private List<Mappable> mList;
    
    private MappableDetailer mDetailer;
    
    @Override
    public void onResume() {
        super.onResume();
        MarkerOptions options;
        
        GoogleMap map = getMap();
        map.setOnMarkerClickListener(this);
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        
        for (Mappable item : mList) {
            options = new MarkerOptions();
            if (mDetailer != null) {
                mDetailer.fillMarker(item, options);
                
            } else {
                options.position(toLatLng(item))
                    .title(Double.toString(item.getMapLatitude()) + ", " + Double.toString(item.getMapLongitude()))
                    .draggable(false);
                
            }
            //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_large));
            map.addMarker(options);
            
        }
        
        if (mList.size() == 0) {
            throw new IllegalStateException("Has no point to view");
            
        } else if (mList.size() == 1) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(toLatLng(mList.get(0)), 16); // MAGIC
            map.moveCamera(update);
            
        } else {
            map.setOnCameraChangeListener(new OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
                    GoogleMap map = getMap();
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    
                    for (Mappable point : mList) {
                        builder.include(toLatLng(point));
                    }
                    
                    CameraUpdate update = CameraUpdateFactory.newLatLngBounds(builder.build(), 20);
                    map.moveCamera(update);
                    
                    map.setOnCameraChangeListener(null);
                    
                }
                
            });
            
        }
        
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    
    public static LatLng toLatLng(Mappable mappable) {
        return new LatLng(mappable.getMapLatitude(), mappable.getMapLongitude());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (mDetailer != null) {
            for (Mappable point : mList) {
                
                LatLng ll = marker.getPosition();
                Location loc1 = new Location("");
                loc1.setLatitude(ll.latitude);
                loc1.setLongitude(ll.longitude);
                
                Location loc2 = new Location("");
                loc2.setLatitude(point.getMapLatitude());
                loc2.setLongitude(point.getMapLongitude());
                
                if (loc2.distanceTo(loc1) < 10) { // EPSILON
                    mDetailer.getDetail(point);
                    return;
                }
            }
            
        } else {
            // Log.i("ListMapFragment", "mDetailer is null");
        }
    }
    
    
    
}




















