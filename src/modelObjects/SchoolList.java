package modelObjects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import models.School;

/**
 * SchoolList is a Singleton class. It stores the list of schools.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class SchoolList {
	private List<School> schoolKitchenList;
	private List<List<School>> schoolClusterList;
	private Properties configFile;
	private int BIG_MEDIUM;
	private static SchoolList schoolList = new SchoolList();
	
	/**
	 * This constructor loads the school list.
	 */
	private SchoolList() {
		super();
		try {
			configFile = new Properties();
			configFile.load(VehicleList.class.getClassLoader()
					.getResourceAsStream("config.properties"));
			BIG_MEDIUM = Integer.parseInt(configFile
					.getProperty("bigMediumConversion"));
			schoolKitchenList = new ArrayList<School>();
			schoolClusterList = new ArrayList<List<School>>();
			readSchoolDetails();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This is a factory method that returns the only instance of this class.
	 * 
	 * @return The instance of type SchoolList
	 */
	public static SchoolList getInstance() {
		return schoolList;
	}
	
	/**
	 * This method reads the vehicle details from the file and stores in the
	 * local list.
	 * 
	 * @throws IOException Some error in IO
	 */
	private void readSchoolDetails() throws IOException {
		String projectPath = configFile.getProperty("projectPath");
		BufferedReader readCSV = new BufferedReader(new FileReader(new File(
				projectPath + "input\\LocationDetails.csv")));
		schoolKitchenList = new ArrayList<School>();
		String latlong = "";
		double latitude, longitude;
		int schoolIndex, bigVesselsCount, mediumVesselsCount;
		float schoolServiceTime;
		String schoolName;
		readCSV.readLine();
		while ((latlong = readCSV.readLine()) != null) {
			String[] details = latlong.split(",");
			schoolIndex = Integer.parseInt(details[0]);
			schoolName = details[1];
			latitude = Double.parseDouble(details[2]);
			longitude = Double.parseDouble(details[3]);
			bigVesselsCount = Integer.parseInt(details[4]);
			mediumVesselsCount = Integer.parseInt(details[5]);				
			mediumVesselsCount = capacityConversion(bigVesselsCount, mediumVesselsCount);
			schoolServiceTime = 3.0f + (0.25f * mediumVesselsCount);
			schoolKitchenList.add(new School(schoolIndex, latitude, longitude, mediumVesselsCount, schoolServiceTime, schoolName));
		}
		readCSV.close();
	}

	/**
	 * This method returns the list of the schools.
	 * 
	 * @return The list of schools
	 */
	public List<School> getSchoolKitchenList() {
		return schoolKitchenList;
	}

	/**
	 * This method returns the list of schools and its cluster.
	 * 
	 * @return The cluster of each school
	 */
	public List<List<School>> getSchoolClusterList() {
		return schoolClusterList;
	}
	
	/**
	 * This method sets the list of school and its cluster.
	 * 
	 * @param schoolClusterList Contains the cluster of each school
	 */
	public void setSchoolClusterList(List<List<School>> schoolClusterList) {
		this.schoolClusterList = schoolClusterList;
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
	public int capacityConversion(int bigVesselCount, int mediumVesselCount) {
		return (bigVesselCount * BIG_MEDIUM) + mediumVesselCount;
	}
}
