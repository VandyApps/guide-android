package edu.vanderbilt.vm.guide.container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import edu.vanderbilt.vm.guide.annotations.NeedsTesting;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.util.DBUtils;

/**
 * Represents a list of places that the user plans to visit.
 * 
 * @author nick
 */
@NeedsTesting(lastModifiedDate = "12/22/12")
public class Agenda implements Iterable<Place> {

    private List<Place> mPlaces = new ArrayList<Place>();

    public Agenda() {
    }

    public Agenda(List<Place> places) {
        Collections.copy(mPlaces, places);
    }

    /**
     * Add a Place to the Agenda. If the Place was already on the Agenda, then
     * it will not be added and this method returns false.
     * 
     * @param place The Place to add
     * @return true if place was added, false otherwise
     */
    public boolean add(Place place) {
        if (!mPlaces.contains(place)) {
            return mPlaces.add(place);
        }
        return false;
    }

    /**
     * Removes a Place from the Agenda.
     * 
     * @param place The Place to remove
     * @return True if place was removed, false otherwise
     */
    public boolean remove(Place place) {
        return mPlaces.remove(place);
    }

    /**
     * Determine whether this Agenda contains a Place
     * 
     * @param place The Place to check
     * @return True if place was on this Agenda, false otherwise
     */
    public boolean isOnAgenda(Place place) {
        return mPlaces.contains(place);
    }

    /**
     * Overwrites the contents of this Agenda with the contents of the given
     * Agenda. This Agenda will lose all of its previous contents.
     * 
     * @param agenda The Agenda whose contents will replace this Agenda's.
     */
    public void overwrite(Agenda agenda) {
        this.mPlaces.clear();
        Collections.copy(agenda.mPlaces, this.mPlaces);
    }

    /**
     * Adds all of the items on the given Agenda that are not already present on
     * this Agenda to this Agenda.
     * 
     * @param agenda The Agenda whose contents will be coalesced with this
     *            Agenda
     */
    public void coalesce(Agenda agenda) {
        for (Place place : agenda.mPlaces) {
            if (!this.mPlaces.contains(place)) {
                this.mPlaces.add(place);
            }
        }
    }

    /**
     * Gets a Place at the given index
     * 
     * @param index The index of the Place to get
     * @return The specified Place
     */
    public Place get(int index) {
        return mPlaces.get(index);
    }

    /**
     * @return number of Places on this Agenda
     */
    public int size() {
        return mPlaces.size();
    }

    @Override
    public Iterator<Place> iterator() {
        return mPlaces.iterator();
    }

    /**
     * Sort the agenda alphabetically, by place name
     */
    public void sortAlphabetically() {
        Collections.sort(mPlaces, new AlphabeticPlaceComparator());
    }

    public void sortByDistance() {
        Collections.sort(mPlaces, new DistancePlaceComparator());
    }

    private class AlphabeticPlaceComparator implements Comparator<Place> {

        @Override
        public int compare(Place plc1, Place plc2) {
            return plc1.getName().compareTo(plc2.getName());
        }

    }

    private class DistancePlaceComparator implements Comparator<Place> {

        @Override
        public int compare(Place arg0, Place arg1) {
            // TODO
            return 0;
        }

    }

    public void write(JsonWriter writer) throws IOException {
        writer.beginArray();
        
        for (Place plc : mPlaces) {
            writer.value(plc.getUniqueId());
        }
        
        writer.endArray();
    }
    
    public static Agenda build(Context ctx, JsonReader reader) throws IOException {
        
        GuideDBOpenHelper helper = new GuideDBOpenHelper(ctx);
        Cursor cursor = DBUtils.getAllPlaces(new String[]{
                GuideDBConstants.PlaceTable.NAME_COL, 
                GuideDBConstants.PlaceTable.CATEGORY_COL,
                GuideDBConstants.PlaceTable.LATITUDE_COL,
                GuideDBConstants.PlaceTable.LONGITUDE_COL, 
                GuideDBConstants.PlaceTable.ID_COL,
                GuideDBConstants.PlaceTable.DESCRIPTION_COL,
                GuideDBConstants.PlaceTable.IMAGE_LOC_COL
        }, helper.getReadableDatabase());
        
        Agenda agenda = new Agenda();
        reader.beginArray();
        int id;
        int colIx = cursor.getColumnIndex(GuideDBConstants.PlaceTable.ID_COL);
        
        while (reader.hasNext()) {
            id = reader.nextInt();
            //Log.i("Agenda", "got PlaceID: " + id);
            cursor.moveToFirst();
            do {
                
                if (id == cursor.getInt(colIx)) {
                    agenda.add(DBUtils.getPlaceFromCursor(cursor));
                    //Log.i("Agenda", "got a DB match: " + id);
                    break;
                }
                
            } while (cursor.moveToNext());
            
        }
        
        reader.endArray();
        helper.close();
        cursor.close();
        return agenda;
    }

    @Override
    public String toString() {
        return mPlaces.toString();
    }
    
}












