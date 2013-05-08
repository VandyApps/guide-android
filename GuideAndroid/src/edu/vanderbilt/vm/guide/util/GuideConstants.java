
package edu.vanderbilt.vm.guide.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public interface GuideConstants {

    public static final int BAD_PLACE_ID = -1;

    public static final int DEFAULT_ID = 10;

    // This is the gold usually found on sports apparel
    public static final ColorDrawable DECENT_GOLD = new ColorDrawable(Color.rgb(182, 144, 0));

    // Not really gold, just a soft color for shading
    public static final ColorDrawable LIGHT_GOLD = new ColorDrawable(Color.rgb(255, 255, 220));

    // This is the gold found on the official "V" logo
    public static final ColorDrawable DARK_GOLD = new ColorDrawable(Color.rgb(162, 132, 72));

    // The original gold used. It was meant to be just a placeholder
    public static final ColorDrawable OLD_GOLD = new ColorDrawable(Color.rgb(189, 187, 14));

    // The color of pure gold, according to Wikipedia
    public static final ColorDrawable PURE_GOLD = new ColorDrawable(Color.rgb(240, 206, 46));

    // white
    public static final ColorDrawable WHITE = new ColorDrawable(Color.WHITE);
    
    public enum PlaceCategories {
        
        RESIDENCE_HALL ("Residence Hall"),
        RECREATION ("Recreation"),
        DINING ("Dining"),
        ACADEMICS ("Academics"),
        EVERYTHING ("Everything"),
        FACILITY ("Facility"),
        MEDICAL ("Medical"),
        LOCAL ("Local"),
        ATHLETICS ("Athetics"),
        GREEK_LIFE ("Greek Life"),
        STUDENT_LIFE ("Student Life"),
        LIBRARY ("Library"),
        MISC ("Misc.");
        
        private final String text;
        
        PlaceCategories(String s) {
            this.text = s;
        }
        
        PlaceCategories() {
            this.text = "Khaz Modan";
        }
        
        public String text() {
            return this.text;
        }
    }
}
