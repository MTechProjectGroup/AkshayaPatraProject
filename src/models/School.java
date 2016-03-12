package models;

/**
 * School class stores the details of a particular school such as its name,
 * location, demand, service time at the school, etc.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class School {
	private Location schoolLocation;
	private int schoolIndex;
	private int distanceFromSchool;
	private int schoolDemand;
	private float serviceTimeAtSchool;
	private int travelTime;
	private String schoolName;

	/**
	 * This is the constructor with following parameters.
	 * 
	 * @param index Contains the school index
	 * @param latitude Contains the latitude of the school
	 * @param longitude Contains the longitude of the school
	 * @param demand Contains the demand of the school
	 * @param serviceT Contains the service time at the school
	 * @param sName Contains the school name
	 */
	public School(int index, double latitude, double longitude, int demand,
			float serviceT, String sName) {
		super();
		this.schoolIndex = index;
		this.schoolLocation = new Location(latitude, longitude);
		this.schoolDemand = demand;
		this.serviceTimeAtSchool = serviceT;
		this.schoolName = sName;
	}

	/**
	 * This method returns the location object of the school.
	 * 
	 * @return The Location object of school
	 */
	public Location getSchoolLocation() {
		return schoolLocation;
	}

	/**
	 * This method returns the latitude of the school.
	 * 
	 * @return The latitude of the school
	 */
	public double getSchoolLatitude() {
		return getSchoolLocation().getLatitude();
	}

	/**
	 * This method returns the longitude of the school.
	 * 
	 * @return The longitude of the school
	 */
	public double getSchoolLongitude() {
		return getSchoolLocation().getLongitude();
	}

	/**
	 * This method returns the school index.
	 * 
	 * @return The index of the school
	 */
	public int getSchoolIndex() {
		return schoolIndex;
	}

	/**
	 * This method returns the in between school distance.
	 * 
	 * @return The distance of the school
	 */
	public int getDistanceFromSchool() {
		return distanceFromSchool;
	}

	/**
	 * This method sets the in between school distance.
	 * 
	 * @param dist Contains the distance of the school
	 */
	public void setDistanceFromSchool(int dist) {
		this.distanceFromSchool = dist;
	}

	/**
	 * This method returns the school demand.
	 * 
	 * @return The demand of the school
	 */
	public int getSchoolDemand() {
		return schoolDemand;
	}

	/**
	 * This method sets the school demand.
	 * 
	 * @param schoolDemand Contains the demand of the school
	 */
	public void setSchoolDemand(int schoolDemand) {
		this.schoolDemand = schoolDemand;
	}

	/**
	 * This method returns the service time.
	 * 
	 * @return The service time at the school
	 */
	public float getServiceTimeAtSchool() {
		return serviceTimeAtSchool;
	}

	/**
	 * This method sets the service time.
	 * 
	 * @param serviceTime Contains the service time at the achool
	 */
	public void setServiceTimeAtSchool(float serviceTime) {
		this.serviceTimeAtSchool = serviceTime;
	}

	/**
	 * This method returns the travel time.
	 * 
	 * @return The travel time of the school
	 */
	public int getTravelTime() {
		return travelTime;
	}

	/**
	 * This method sets the travel time.
	 * 
	 * @param travelTime Contains the travel time of the school
	 */
	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}

	/**
	 * This method returns the school name.
	 * 
	 * @return Contains the name of the school
	 */
	public String getSchoolName() {
		return schoolName;
	}

	/**
	 * This method sets the school name.
	 * 
	 * @param schoolName The name of the school
	 */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
}
