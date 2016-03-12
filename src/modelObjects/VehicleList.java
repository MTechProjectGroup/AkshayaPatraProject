package modelObjects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import models.Vehicle;

/**
 * VehicleList is a Singleton class. It stores the list of vehicles
 * available.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class VehicleList {
	private List<Vehicle> vehicleDetailsList;
	private static Iterator<Vehicle> vehicleDetailsIterator;
	private Properties configFile;
	private int BIG_MEDIUM;
	private static VehicleList vehicleList = new VehicleList();

	/**
	 * This constructor loads the vehicle details.
	 */
	private VehicleList() {
		super();
		try {
			configFile = new Properties();
			configFile.load(VehicleList.class.getClassLoader()
					.getResourceAsStream("config.properties"));
			BIG_MEDIUM = Integer.parseInt(configFile
					.getProperty("bigMediumConversion"));
			readVehicleDetails();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is a factory method that returns the only instance of this class.
	 * 
	 * @return The instance of type VehicleList
	 */
	public static VehicleList getInstance() {
		return vehicleList;
	}

	/**
	 * This method reads the vehicle details from the file and stores in the
	 * local list.
	 * 
	 * @throws IOException Some error in IO
	 */
	private void readVehicleDetails() throws IOException {
		String projectPath = configFile.getProperty("projectPath");
		BufferedReader readCSV = new BufferedReader(new FileReader(new File(
				projectPath + "input\\VehicleDetails.csv")));
		vehicleDetailsList = new ArrayList<Vehicle>();
		String vDetails = "";
		int index, nov, bigVessel, mediumVessel;
		while ((vDetails = readCSV.readLine()) != null) {
			String[] details = vDetails.split(",");
			index = Integer.parseInt(details[0]);
			nov = Integer.parseInt(details[2]);
			bigVessel = Integer.parseInt(details[3]);
			mediumVessel = Integer.parseInt(details[4]);
			mediumVessel = capacityConversion(bigVessel, mediumVessel);
			vehicleDetailsList.add(new Vehicle(index, nov, mediumVessel));
		}
		readCSV.close();
		sortByVehicleCapacity();
	}

	/**
	 * This method sorts the vehicles in descending order of the capacity of the
	 * vehicle. 
	 */
	private void sortByVehicleCapacity() {
		Collections.sort(vehicleDetailsList, new Comparator<Object>() {
			public int compare(final Object o1, final Object o2) {
				int a = ((Vehicle) o1).getVehicleCapacity();
				int b = ((Vehicle) o2).getVehicleCapacity();
				return a < b ? 1 : a > b ? -1 : 0;
			}
		});
	}

	/**
	 * This method checks for the availability of a vehicle. If yes, it returns
	 * the capacity it can carry.
	 * 
	 * @return The capacity of selected vehicle
	 */
	public int getVehicle() {
		Vehicle vehicle;
		int capacity = 0;
		int vehicleCount;
		vehicleDetailsIterator = vehicleDetailsList.iterator();
		while (vehicleDetailsIterator.hasNext()) {
			vehicle = vehicleDetailsIterator.next();
			if (vehicle.isAvailable()) {
				capacity = vehicle.getVehicleCapacity();
				vehicleCount = vehicle.getNoOFVehicles();
				vehicle.setNoOFVehicles(vehicleCount - 1);
				if(vehicleCount == 1)
					vehicle.setAvailable(false);								
				break;
			}
		}
		return capacity;
	}

	/**
	 * This method converts the number of big vessels to number of medium
	 * vessels.
	 * 
	 * @param bigVesselCount Contains the number of big vessels
	 * @param mediumVesselCount Contains the number of medium vessels
	 * 
	 * @return The conversion of big to medium vessels
	 */
	private int capacityConversion(int bigVesselCount, int mediumVesselCount) {
		return (bigVesselCount * BIG_MEDIUM) + mediumVesselCount;
	}
	
	/**
	 * This method resets the vehicle list.
	 * 
	 * @throws IOException Some error in IO
	 */
	public void reinitializeVehicles() throws IOException{
		readVehicleDetails();
	}
}
