package models;

/**
 * Location class contains the latitude and longitude of the location. Location
 * can be a school or a kitchen.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class Location {
	private double latitude;
	private double longitude;

	/**
	 * This is the constructor to set the latitude and longitude.
	 * 
	 * @param latitude Contains the latitude of the location
	 * @param longitude Contains the longitude of the location
	 */
	public Location(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * This method sets the latitude of a location.
	 * 
	 * @param latitude Contains the latitude of the location
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * This method sets the longitude of a location.
	 * 
	 * @param longitude Contains the longitude of the location
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * This method returns the latitude of a location.
	 * 
	 * @return The latitude of the location
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * This method returns the longitude of a location.
	 * 
	 * @return The longitude of the location
	 */
	public double getLongitude() {
		return longitude;
	}
}
