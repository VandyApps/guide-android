
package edu.vanderbilt.vm.guide.container;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.vanderbilt.vm.guide.annotations.NeedsTesting;

/**
 * In-memory representation of a place. Use this class when you want to deal
 * with a nicer interface than SQLite cursors. This class is also useful for
 * data transactions between other Guide container classes.
 * 
 * @author nicholasking
 */
@NeedsTesting(lastModifiedDate = "12/22/12")
public class Place {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("container.Place");

    private static final int DEFAULT_ID = -1;

    private double mLatitude;

    private double mLongitude;

    private String mImageLoc;

    private String mAudioLoc;

    private String mVideoLoc;

    private String mName;

    private String mDescription;

    private String mHours;

    private List<String> mCategories;

    private int mUniqueId;

    /* package */Place() {
    }

    private Place(Place.Builder builder) {
        if (builder.mUniqueId == DEFAULT_ID) {
            throw new IllegalArgumentException("Unique ID must not be default value (" + DEFAULT_ID
                    + ")");
        }
        mLatitude = builder.mLatitude;
        mLongitude = builder.mLongitude;
        mImageLoc = builder.mImageLoc;
        mAudioLoc = builder.mAudioLoc;
        mVideoLoc = builder.mVideoLoc;
        mName = builder.mName;
        mDescription = builder.mDescription;
        mHours = builder.mHours;
        mUniqueId = builder.mUniqueId;
        mCategories = builder.mCategories;
    }

    /**
     * A class for creating Place objects. You must use this class in order to
     * create a place. Chain setter method calls where appropriate. Call build()
     * when you have finished setting all of the fields. Any unset fields will
     * be given a default value.
     * <p/>
     * <b>Note:</b> You must set a uniqueId for every place. Failing to do so
     * will result in an exception.
     * 
     * @author nicholasking
     */
    public static class Builder {
        private double mLatitude = 0;

        private double mLongitude = 0;

        private String mImageLoc;

        private String mAudioLoc;

        private String mVideoLoc;

        private String mName;

        private String mDescription;

        private String mHours;

        private List<String> mCategories = new ArrayList<String>();

        private int mUniqueId = DEFAULT_ID;

        public Builder() {
        }

        public Builder setLatitude(double lat) {
            mLatitude = lat;
            return this;
        }

        public Builder setLongitude(double lon) {
            mLongitude = lon;
            return this;
        }

        public Builder setImageLoc(String imageLoc) {
            mImageLoc = imageLoc;
            return this;
        }

        public Builder setAudioLoc(String audioLoc) {
            mAudioLoc = audioLoc;
            return this;
        }

        public Builder setVideoLoc(String videoLoc) {
            mVideoLoc = videoLoc;
            return this;
        }

        public Builder setName(String name) {
            mName = name;
            return this;
        }

        public Builder setDescription(String desc) {
            mDescription = desc;
            return this;
        }

        public Builder addCategory(String category) {
            mCategories.add(category);
            return this;
        }

        public Builder setHours(String hours) {
            mHours = hours;
            return this;
        }

        public Builder setUniqueId(int uniqueId) {
            mUniqueId = uniqueId;
            return this;
        }

        public Place build() {
            return new Place(this);
        }

    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getPictureLoc() {
        return mImageLoc;
    }

    public String getAudioLoc() {
        return mAudioLoc;
    }

    public String getVideoLoc() {
        return mVideoLoc;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getHours() {
        return mHours;
    }

    public List<String> getCategories() {
        List<String> copy = new ArrayList<String>(mCategories);
        return copy;
    }

    public int getUniqueId() {
        return mUniqueId;
    }
    
    @Override
    public String toString() {
        return "{ id: " + this.getUniqueId() + ", name: " + getName() + " }";
    }
    
    @Override
    public int hashCode() {
        // We just return the unique ID for efficiency,
        // hoping that the client has actually made the ID unique
        return mUniqueId;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Place)) {
            return false;
        }
        Place otherPlace = (Place)other;
        // We shouldn't have to compare anything other than the
        // unique ID.
        return this.mUniqueId == otherPlace.mUniqueId;
    }

}
