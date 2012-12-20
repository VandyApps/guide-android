package edu.vanderbilt.vm.guide.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a list of places that the user plans to visit.
 * @author nick
 *
 */
public class Agenda implements Iterable<Place> {

	private List<Place> mPlaces = new ArrayList<Place>();
	
	public Agenda() { }
	
	public Agenda(List<Place> places) {
		Collections.copy(mPlaces, places);
	}
	
	
	/**
	 * Add a Place to the Agenda.  If the Place was already on the
	 * Agenda, then it will not be added and this method returns false.
	 * @param place The Place to add
	 * @return true if place was added, false otherwise
	 */
	public boolean add(Place place) {
		if(!mPlaces.contains(place)) {
			return mPlaces.add(place);
		}
		return false;
	}
	
	
	/**
	 * Removes a Place from the Agenda.
	 * @param place The Place to remove
	 * @return True if place was removed, false otherwise
	 */
	public boolean remove(Place place) {
		return mPlaces.remove(place);
	}
	
	
	/**
	 * Determine whether this Agenda contains a Place
	 * @param place The Place to check
	 * @return True if place was on this Agenda, false otherwise
	 */
	public boolean isOnAgenda(Place place) {
		return mPlaces.contains(place);
	}
	
	
	/**
	 * Overwrites the contents of this Agenda with the
	 * contents of the given Agenda.  This Agenda will lose
	 * all of its previous contents.
	 * @param agenda The Agenda whose contents will replace this Agenda's.
	 */
	public void overwrite(Agenda agenda) {
		this.mPlaces.clear();
		Collections.copy(agenda.mPlaces, this.mPlaces);
	}
	
	
	/**
	 * Adds all of the items on the given Agenda that are not already present
	 * on this Agenda to this Agenda.
	 * @param agenda The Agenda whose contents will be coalesced with this Agenda
	 */
	public void coalesce(Agenda agenda) {
		for(Place place : agenda.mPlaces) {
			if(!this.mPlaces.contains(place)) {
				this.mPlaces.add(place);
			}
		}
	}
	
	
	/**
	 * Gets a Place at the given index
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
	
}
