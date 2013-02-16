package edu.vanderbilt.vm.guide.container;

import android.location.Location;

public class Node {

    private double mScore;

    private final int mId;

    private final double mLat;

    private final double mLng;

    private final int[] mNeighbours;

    private int mPrevious;
    
    private boolean isPlace;
    
    private static final double R = 6371;
    
    private static final double DEGPERRAD = 180/Math.PI;

    public Node(int id, double lat, double lng, int[] neighs, int isAPlace) {
        mId = id;
        mLat = lat/DEGPERRAD;
        mLng = lng/DEGPERRAD;
        mNeighbours = neighs;
        isPlace = (isAPlace > 0) ? true : false;
    }
    
    public Node(double lat, double lng) {
        mId = -1;
        mLat = lat/DEGPERRAD;
        mLng = lng/DEGPERRAD;
        mNeighbours = null;
        isPlace = false;
    }
    
    public Node(Place plc) {
        mId = plc.getUniqueId();
        mLat = plc.getLatitude()/DEGPERRAD;
        mLng = plc.getLongitude()/DEGPERRAD;
        mNeighbours = null;
        isPlace = true;
    }
    
    public Node(Location loc) {
        mId = -1;
        mLat = loc.getLatitude()/DEGPERRAD;
        mLng = loc.getLongitude()/DEGPERRAD;
        mNeighbours = null;
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
    
    public int[] getNeighbours() {
        return mNeighbours;
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
    
    public double distanceTo(Node d) {
        double dy = d.mLat - this.mLat;
        double dx = d.mLng - this.mLng;
        return R*Math.sqrt(dx*dx*Math.cos(this.mLat)*Math.cos(this.mLat)+dy*dy);
    }
    
    
}
