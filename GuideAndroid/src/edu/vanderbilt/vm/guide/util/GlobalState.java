
package edu.vanderbilt.vm.guide.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.vanderbilt.vm.guide.annotations.NeedsTesting;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.container.Place;

/**
 * This class holds singletons of certain objects we need to share throughout
 * the application, such as the user's agenda. This is simpler and easier than
 * using a SQLite database to hold the agenda and allows us to use several
 * methods to make data transactions with the agenda easier.
 * 
 * @author nicholasking
 */
@NeedsTesting(lastModifiedDate = "12/22/12")
public class GlobalState {

    private static Agenda userAgendaSingleton = new Agenda();

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger("util.GlobalState");

    private GlobalState() {
        throw new AssertionError("Do not instantiate this class.");
    }

    public static Agenda getUserAgenda() {
        return userAgendaSingleton;
    }

    // ---------- History Singleton ---------- //
    private static Agenda userHistory = new Agenda();

    private static final int BOGUS_ID = 1000;

    private static final int HISTORY_LIMIT = 10;

    static {
        resetHistory();
    }

    /**
     * Returns the user's history.
     * 
     * @return user History
     */
    public static Agenda getUserHistory() {
        return userHistory;
    }

    /**
     * Add a place to history. The list is arranged most recent first
     * 
     * @param plc
     */
    public static void addHistory(Place plc) {
        if (userHistory.size() == 0) {
            userHistory.add(plc);
        } else if (userHistory.get(0).getUniqueId() == BOGUS_ID) {
            userHistory.overwrite(new Agenda());
            userHistory.add(plc);
        } else {

            if (userHistory.size() > HISTORY_LIMIT) {
                // TODO
            }

            // userHistory.addToTop(plc); TODO
        }

    }

    /**
     * Empties the history.
     */
    public static void resetHistory() {
        if (userHistory == null) {
            userHistory = new Agenda();
        } else {
            userHistory.overwrite(new Agenda());
        }

        Place temp = (new Place.Builder()).setName("History is Empty").setUniqueId(BOGUS_ID)
                .build(); // TODO
        userHistory.add(temp);
    }

    // ---------- END History Singleton ---------- //

    // Coordinate lookup table

}
