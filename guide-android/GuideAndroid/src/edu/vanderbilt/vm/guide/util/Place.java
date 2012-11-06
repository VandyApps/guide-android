package edu.vanderbilt.vm.guide.util;

import android.net.Uri;

public class Place {

	private double mLatitude;
	private double mLongitude;
	private Uri mPictureUri;
	private Uri mAudioUri;
	private Uri mVideoUri;
	private String mName;
	private String mDescription;
	private String mHours;
	private String mCategory;
	private int mUniqueId;

	/* package */Place() {
	}

	private Place(Place.Builder builder) {
		if (builder.mUniqueId == -1) {
			throw new IllegalArgumentException(
					"Unique ID must not be default value (-1)");
		}
		mLatitude = builder.mLatitude;
		mLongitude = builder.mLongitude;
		mPictureUri = builder.mPictureUri;
		mAudioUri = builder.mAudioUri;
		mVideoUri = builder.mVideoUri;
		mName = builder.mName;
		mDescription = builder.mDescription;
		mHours = builder.mHours;
		mUniqueId = builder.mUniqueId;
		mCategory = builder.mCategory;
	}

	public static class Builder {
		private double mLatitude = 0;
		private double mLongitude = 0;
		private Uri mPictureUri;
		private Uri mAudioUri;
		private Uri mVideoUri;
		private String mName;
		private String mDescription;
		private String mHours;
		private String mCategory;
		private int mUniqueId = -1; // An exception will be thrown if this id is
									// used

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

		public Builder setPictureUri(Uri pictureUri) {
			mPictureUri = pictureUri;
			return this;
		}

		public Builder setAudioUri(Uri audioUri) {
			mAudioUri = audioUri;
			return this;
		}
		
		public Builder setVideoUri(Uri videoUri) {
			mVideoUri = videoUri;
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

		public Builder setCategory(String category) {
			mCategory = category;
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

	public Uri getPictureUri() {
		return mPictureUri;
	}

	public Uri getAudioUri() {
		return mAudioUri;
	}
	
	public Uri getVideoUri() {
		return mVideoUri;
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

	public String getCategory() {
		return mCategory;
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
		if (!(other instanceof Place)) {
			return false;
		}
		Place otherPlace = (Place) other;
		// We shouldn't have to compare anything other than the
		// unique ID.
		return this.mUniqueId == otherPlace.mUniqueId;
	}

}
