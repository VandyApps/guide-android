package edu.vanderbilt.vm.guide.util;

import java.util.List;

public class Tour {

	private Agenda mAgenda;
	private String mTimeRequired;
	private String mDistance;
	private String mDescription;
	private String mName;
	
	/* package */ Tour() { }
	
	public Tour(Agenda agenda) {
		mAgenda = agenda;
	}
	
	private Tour(Tour.Builder builder) {
		this.mAgenda = builder.mAgenda;
		this.mTimeRequired = builder.mTimeRequired;
		this.mDescription = builder.mDescription;
		this.mDistance = builder.mDistance;
		this.mName = builder.mName;
	}
	
	public Tour(List<Place> placesOnTour) {
		mAgenda = new Agenda(placesOnTour);
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public void setTimeReq(String timeReq) {
		mTimeRequired = timeReq;
	}
	
	public void setDistance(String dist) {
		mDistance = dist;
	}
	
	public void setDescription(String desc) {
		mDescription = desc;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getTimeReq() {
		return mTimeRequired;
	}
	
	public String getDistance() {
		return mDistance;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public Agenda getAgenda() {
		return mAgenda;
	}
	
	public static class Builder {
		private Agenda mAgenda;
		private String mTimeRequired;
		private String mDistance;
		private String mDescription;
		private String mName;
		
		public Builder() { }
		
		public Builder setAgenda(Agenda agenda) {
			mAgenda = agenda;
			return this;
		}
		
		public Builder setTimeReq(String timeReq) {
			mTimeRequired = timeReq;
			return this;
		}
		
		public Builder setDistance(String distance) {
			mDistance = distance;
			return this;
		}
		
		public Builder setDescription(String desc) {
			mDescription = desc;
			return this;
		}
		
		public Builder setName(String name) {
			mName = name;
			return this;
		}
		
		public Tour build() {
			return new Tour(this);
		}
		
	}
	
}
