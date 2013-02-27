package edu.vanderbilt.vm.guide.container;

import java.util.LinkedList;
import java.util.List;

import android.location.Location;

public class Node {

    private double mScore;

    private final int mId;

    private double mLat;

    private double mLng;
    
    private List<Integer> mNeighbours = new LinkedList<Integer>();

    private int mPrevious;
    
    private boolean isPlace;
    
    private static final double R = 6371;
    
    private static final double DEGPERRAD = 180/Math.PI;
    
    // Resolution of the Node network
    static final double EPSILON = 0.000001;
    
    // Main constructor for a node that represents a row in the Nodes DB table.
    public Node(int id, double lat, double lng, int[] neighs, int isAPlace) {
        mId = id;
        mLat = lat/DEGPERRAD;
        mLng = lng/DEGPERRAD;
        
        if (neighs != null) {
            for (int i : neighs) {
                mNeighbours.add(i);
            }
        }
        isPlace = (isAPlace > 0) ? true : false;
    }
    
    public Node(double lat, double lng) {
        mId = -1;
        mLat = lat/DEGPERRAD;
        mLng = lng/DEGPERRAD;
        isPlace = false;
    }
    
    public Node(Place plc) {
        mId = plc.getUniqueId();
        mLat = plc.getLatitude()/DEGPERRAD;
        mLng = plc.getLongitude()/DEGPERRAD;
        isPlace = true;
    }
    
    public Node(Location loc) {
        mId = -1;
        mLat = loc.getLatitude()/DEGPERRAD;
        mLng = loc.getLongitude()/DEGPERRAD;
        isPlace = false;
    }

    public double getLat() {
        return mLat*DEGPERRAD;
    }

    public double getLng() {
        return mLng*DEGPERRAD;
    }

    public double getScore() {
        return mScore;
    }

    public int getId() {
        return mId;
    }
    
    public Integer[] getNeighbours() {
        Integer[] iii = new Integer[this.mNeighbours.size()];
        for (int i = 0; i < this.mNeighbours.size(); i++ ) {
            iii[i] = this.mNeighbours.get(i);
        }
        
        return iii;
    }
    
    public boolean isPlace() {
        return isPlace;
    }
    
    public int getPrevious() {
        return mPrevious;
    }
    
    public void setScore(double scr) {
        mScore = scr;
    }
    
    public void setPrevious(int prev) {
        mPrevious = prev;
    }
    
    public void setNeighbour(int[] i) {
        mNeighbours.clear();
        for (int ii : i) {
            mNeighbours.add(ii);
        }
    }
    
    public void addNeighbour(int id) {
        if (mNeighbours.contains(Integer.valueOf(id))) {
            return;
        }
        mNeighbours.add(id);
    }
    
    public void setLat(double lat) {
        this.mLat = lat/DEGPERRAD;
    }
    
    public void setLng(double lng) {
        this.mLng = lng/DEGPERRAD;
    }
    
    public double distanceTo(Node d) {
        double dy = d.mLat - this.mLat;
        double dx = d.mLng - this.mLng;
        return R*Math.sqrt(dx*dx*Math.cos(this.mLat)*Math.cos(this.mLat)+dy*dy);
    }
    
    public double naiveDist(Node d) {
        double dy = d.mLat*DEGPERRAD - this.mLat*DEGPERRAD;
        double dx = d.mLng*DEGPERRAD - this.mLng*DEGPERRAD;
        return Math.sqrt(dx*dx + dy*dy);
    }
}
