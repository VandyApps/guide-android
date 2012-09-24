package edu.vanderbilt.vm.guide.util;

import android.net.Uri;

public class Place {

	private double mLatitude;
	private double mLongitude;
	private Uri mPictureUri;
	private Uri mAudioUri;
	private String mName;
	private String mDescription;
	private String mHours;
	private int mUniqueId;
	
	/* package */ Place() { }
	
	/**
	 * Constructs a Place with the given parameters.  It is recommended
	 * that you do not use this constructor, but instead use the Place.Builder class
	 * to make a Place.
	 * @param lat latitude
	 * @param lon longitude
	 * @param picUri Uri pointing to this Place's picture
	 * @param audioUri Uri pointing to this place's audio
	 * @param name Name of this Place
	 * @param desc Description of this Place
	 * @param hours Hours of operation of this Place
	 * @param uniqueId The unique ID for this Place.  It is very important that
	 * 					this is actually unique.
	 */
	public Place(double lat,
			double lon,
			Uri picUri,
			Uri audioUri,
			String name,
			String desc,
			String hours,
			int uniqueId) {
		mLatitude = lat;
		mLongitude = lon;
		mPictureUri = picUri;
		mAudioUri = audioUri;
		mName = name;
		mDescription = desc;
		mHours = hours;
		mUniqueId = uniqueId;
	}
	
	public static class Builder {
		private double mLatitude = 0;
		private double mLongitude = 0;
		private Uri mPictureUri;
		private Uri mAudioUri;
		private String mName;
		private String mDescription;
		private String mHours;
		private int mUniqueId;
		
		public Builder() { }
		
		public Builder setLatitude(double lat) {
			mLatitude = lat;
			return this;
		}
		
		public Builder setLongitude(double lon) {
			mLongitude = lon;
			return this;
		}
		
		public Builder setPictureUri(Uri pictureUri) {
			mPictureUri = pictureUri;
			return this;
		}
		
		public Builder setAudioUri(Uri audioUri) {
			mAudioUri = audioUri;
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
		
		public Builder setHours(String hours) {
			mHours = hours;
			return this;
		}
		
		public Builder setUniqueId(int uniqueId) {
			mUniqueId = uniqueId;
			return this;
		}
		
		public Place build() {
			return new Place(
					mLatitude,
					mLongitude,
					mPictureUri,
					mAudioUri,
					mName,
					mDescription,
					mHours,
					mUniqueId);
		}
		
	}

	public double getLatitude() {
		return mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public Uri getPictureUri() {
		return mPictureUri;
	}

	public Uri getAudioUri() {
		return mAudioUri;
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
	
	public int getUniqueId() {
		return mUniqueId;
	}
	
	public int hashCode() {
		// We just return the unique ID for efficiency,
		// hoping that the client has actually made the ID unique
		return mUniqueId;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Place)) {
			return false;
		}
		Place otherPlace = (Place) other;
		// We shouldn't have to compare anything other than the
		// unique ID.
		return this.mUniqueId == otherPlace.mUniqueId;
	}
	
}
