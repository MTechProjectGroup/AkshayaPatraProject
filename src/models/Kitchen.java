package models;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import modelObjects.SchoolList;

/**
 * Kitchen class stores the latitude and longitude of the kitchen.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class Kitchen {
	private Location kitchenLocation;
	private String kitchenSchoolUrl[];
	private Properties configFile;
	private static Kitchen kitchen = new Kitchen();
	private int countOfURLs;
	private final int API_LIMIT = 90;
	private String apiKey = "AIzaSyDuQHT8biSPJa_tlGU1IYBZsc5lZ69au4Q";

	/**
	 * This constructor loads the kitchen details.
	 */
	private Kitchen() {
		super();
		try {
			countOfURLs = 0;
			configFile = new Properties();
			configFile.load(Kitchen.class.getClassLoader()
					.getResourceAsStream("config.properties"));
			String[] kitchenDetails = configFile.getProperty("kitchenDetails").split(",");
			double kitchenLatitude = Double.parseDouble(kitchenDetails[0]);
			double kitchenLongitude = Double.parseDouble(kitchenDetails[1]);
			kitchenLocation = new Location(kitchenLatitude, kitchenLongitude);
			
			int numberOfSchools = Integer.parseInt(configFile.getProperty("schoolCount"));
			countOfURLs = (int)Math.ceil((float)numberOfSchools / (float)API_LIMIT);
			kitchenSchoolUrl = new String[countOfURLs];
			formQueryToGoogleAPIforKitchen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is a factory method that returns the only instance of this class.
	 * 
	 * @return The instance of type Kitchen
	 */
	public static Kitchen getInstance() {
		return kitchen;
	}
	
	/**
	 * This method forms the URLs that need to be queried for the kitchen.
	 */
	private void formQueryToGoogleAPIforKitchen() {
		int kitchenSchoolUrlIndex = 0;
		kitchenSchoolUrl[0] = "https://maps.googleapis.com/maps/api/distancematrix/json?key=";
		kitchenSchoolUrl[0] += apiKey;
		kitchenSchoolUrl[0] += "&origins=12.88812,77.54838&destinations=";

		for (int i = 0; i < countOfURLs; i++)
			kitchenSchoolUrl[i] = kitchenSchoolUrl[0];

		School school = null;		
		Iterator<School> schoolKitchenListIterator = SchoolList.getInstance().getSchoolKitchenList()
				.iterator();

		for (int i = 0; schoolKitchenListIterator.hasNext(); i++, kitchenSchoolUrlIndex = i / 90) {
			school = schoolKitchenListIterator.next();
			kitchenSchoolUrl[kitchenSchoolUrlIndex] += school.getSchoolLatitude() + ","
					+ school.getSchoolLongitude() + "|";
		}
	}
	
	/**
	 * This method returns the location object of the kitchen.
	 * 
	 * @return The Location object
	 */
	private Location getKitchenLocation() {
		return kitchenLocation;
	}
	
	/**
	 * This method returns the latitude of the kitchen.
	 * 
	 * @return The kitchen latitude
	 */
	public double getKitchenLatitude() {
		return getKitchenLocation().getLatitude();
	}

	/**
	 * This method returns the longitude of the kitchen.
	 * 
	 * @return The kitchen longitude
	 */
	public double getKitchenLongitude() {
		return getKitchenLocation().getLongitude();
	}
	
	/**
	 * This method sorts the school in the descending order of the distances from kitchen.
	 * 
	 * @param schoolList Contains the list of schools that is to be sorted
	 */
	public void sortSchoolListInDescendingOrderOfDistances(List<School> schoolList) {
		Collections.sort(schoolList, new Comparator<Object>() {
			public int compare(final Object school1, final Object school2) {
				int distanceOfSchool1 = ((School) school1).getDistanceFromSchool();
				int distanceOfSchool2 = ((School) school2).getDistanceFromSchool();
				return distanceOfSchool1 < distanceOfSchool2 ? 1 : distanceOfSchool1 > distanceOfSchool2 ? -1 : 0;
			}
		});
	}

	/**
	 * This method returns the sortedSchoolsCloserToFarthestPoint of URLs for kitchen to all the schools.
	 * 
	 * @return The URLs for the kitchen
	 */
	public String[] getKitchenSchoolUrl() {
		return kitchenSchoolUrl;
	}
}
