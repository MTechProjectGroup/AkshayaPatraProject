package models;

/**
 * Vehicle class stores the details of a particular vehicle such as its
 * capacity, count of vehicles of a particular type, etc.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class Vehicle {
	private int vehicleIndex;
	private int noOFVehicles;
	private int vehicleCapacity;
	private boolean available;

	/**
	 * This is the constructor with following parameters.
	 * 
	 * @param vehicleIndex Contains the index of the vehicle
	 * @param noOFVehicles Contains the number of vehicles of this index
	 * @param vehicleCapacity Contains the capacity of the vehicle
	 */
	public Vehicle(int vehicleIndex, int noOFVehicles, int vehicleCapacity) {
		super();
		this.available = true;
		this.vehicleIndex = vehicleIndex;
		this.noOFVehicles = noOFVehicles;
		this.vehicleCapacity = vehicleCapacity;
	}

	/**
	 * This method returns the index of the vehicle.
	 * 
	 * @return The index of the vehicle
	 */
	public int getVehicleIndex() {
		return vehicleIndex;
	}

	/**
	 * This method returns the total number of vehicles of this type.
	 * 
	 * @return The count of the vehicles
	 */
	public int getNoOFVehicles() {
		return noOFVehicles;
	}

	/**
	 * This method sets the total number of vehicles of this type.
	 * 
	 * @param noOFVehicles Contains the count of the vehicles
	 */
	public void setNoOFVehicles(int noOFVehicles) {
		this.noOFVehicles = noOFVehicles;
	}

	/**
	 * This method returns the capacity of the vehicle.
	 * 
	 * @return The capacity of the vehicle
	 */
	public int getVehicleCapacity() {
		return vehicleCapacity;
	}

	/**
	 * This method sets the capacity of the vehicle.
	 * 
	 * @param vehicleCapacity Contains the capacity of the vehicle
	 */
	public void setVehicleCapacity(int vehicleCapacity) {
		this.vehicleCapacity = vehicleCapacity;
	}

	/**
	 * This method returns the availability of the vehicle.
	 * 
	 * @return The availability of the vehicle
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * This method sets the availability of the vehicle.
	 * 
	 * @param available Contains the availability of the vehicle
	 */
	public void setAvailable(boolean available) {
		this.available = available;
	}
}
