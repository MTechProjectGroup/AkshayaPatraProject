package graphAlgorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import modelObjects.SchoolList;
import models.School;

/**
 * InitialClusterFormation class forms clusters around each school. The output
 * is list of clusters.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class InitialClusterFormation {
	private final double CLUSTER_BOX_SIZE_INCREMENT = 0.0001;
	private final double CLUSTER_SCHOOL_LIMIT = 20;
	private final double CLUSTER_BOX_INITIAL_SIZE = 0.0125;
	private String[] schoolClusterURL;
	private final int API_LIMIT = 90;
	private String[] apiKey = { "AIzaSyDuQHT8biSPJa_tlGU1IYBZsc5lZ69au4Q",
			"AIzaSyCzMkl6xOuNEJlhy2xKQ-HOgTB0QQBEoN4",
			"AIzaSyCMY9xhpG7hCU-CZmFiM_A5Qpj1v_AmzFs",
			"AIzaSyCjgycferCnJS7R8hahsyCl_bYJdzIzrpY",
			"AIzaSyBd726ckN-J03NKGBKV0MWZWmG0mH2Jy9I",
			"AIzaSyBd726ckN-J03NKGBKV0MWZWmG0mH2Jy9I" };

	/**
	 * This method creates a list of all the initial clusters for each school.
	 * Also, it forms the URL for each of these clusters.
	 */
	public void formInitialClusters() {
		double clusterBoxIncrement, countOfDestinations, initialClusterBoxSize;
		int schoolIndex, schoolListSize, destinationCount, schoolListIndex;
		double clusterBoxTopLeftLatitude, clusterBoxTopLeftLongitude, clusterBoxBottomRightLatitude, clusterBoxBottomRightLongitude;
		School currentSchool, currentDestinationSchool;
		String currentLatitudeLongitude = "";
		SchoolList schoolListObject = SchoolList.getInstance();
		List<School> schoolList = schoolListObject.getSchoolKitchenList();
		List<School> schoolsInClusterCurrentList = null;
		List<List<School>> schoolsInClusterList = schoolListObject
				.getSchoolClusterList();

		schoolListSize = schoolList.size();
		Iterator<School> schoolListIterator = schoolList
				.iterator();
		schoolClusterURL = new String[schoolListSize];

		while (schoolListIterator.hasNext()) {
			clusterBoxIncrement = CLUSTER_BOX_SIZE_INCREMENT;
			countOfDestinations = CLUSTER_SCHOOL_LIMIT;
			initialClusterBoxSize = CLUSTER_BOX_INITIAL_SIZE;
			currentSchool = schoolListIterator.next();
			schoolIndex = currentSchool.getSchoolIndex() - 1;

			schoolClusterURL[schoolIndex] = "https://maps.googleapis.com/maps/api/distancematrix/json?key=";
			int apiKeyIndex = schoolIndex / API_LIMIT;
			schoolClusterURL[schoolIndex] += apiKey[apiKeyIndex];
			/*
			 * if (schoolIndex <= 90) schoolClusterURL[schoolIndex] =
			 * schoolClusterURL[schoolIndex] +
			 * "AIzaSyDuQHT8biSPJa_tlGU1IYBZsc5lZ69au4Q"; if (schoolIndex > 90
			 * && schoolIndex <= 180) schoolClusterURL[schoolIndex] =
			 * schoolClusterURL[schoolIndex] +
			 * "AIzaSyCzMkl6xOuNEJlhy2xKQ-HOgTB0QQBEoN4"; if (schoolIndex > 180
			 * && schoolIndex <= 270) schoolClusterURL[schoolIndex] =
			 * schoolClusterURL[schoolIndex] +
			 * "AIzaSyCMY9xhpG7hCU-CZmFiM_A5Qpj1v_AmzFs"; if (schoolIndex > 270
			 * && schoolIndex <= 360) schoolClusterURL[schoolIndex] =
			 * schoolClusterURL[schoolIndex] +
			 * "AIzaSyCjgycferCnJS7R8hahsyCl_bYJdzIzrpY"; if (schoolIndex > 360
			 * && schoolIndex <= 450) schoolClusterURL[schoolIndex] =
			 * schoolClusterURL[schoolIndex] +
			 * "AIzaSyBd726ckN-J03NKGBKV0MWZWmG0mH2Jy9I"; if (schoolIndex > 450)
			 * schoolClusterURL[schoolIndex] = schoolClusterURL[schoolIndex] +
			 * "AIzaSyBd726ckN-J03NKGBKV0MWZWmG0mH2Jy9I";
			 */

			schoolClusterURL[schoolIndex] = schoolClusterURL[schoolIndex]
					+ "&origins=" + currentSchool.getSchoolLatitude() + ","
					+ currentSchool.getSchoolLongitude() + "&destinations=";

			destinationCount = 0;
			clusterBoxTopLeftLongitude = currentSchool.getSchoolLatitude()
					+ initialClusterBoxSize;
			clusterBoxTopLeftLatitude = currentSchool.getSchoolLongitude()
					+ initialClusterBoxSize;

			clusterBoxBottomRightLongitude = currentSchool.getSchoolLatitude()
					- initialClusterBoxSize;
			clusterBoxBottomRightLatitude = currentSchool.getSchoolLongitude()
					- initialClusterBoxSize;

			schoolsInClusterCurrentList = null;
			while (destinationCount <= countOfDestinations) {
				currentLatitudeLongitude = "";
				destinationCount = 0;
				schoolsInClusterCurrentList = new ArrayList<School>();
				for (schoolListIndex = schoolIndex - 1; schoolListIndex >= 0; schoolListIndex--) {
					currentDestinationSchool = schoolList
							.get(schoolListIndex);

					if (currentDestinationSchool.getSchoolLatitude() < clusterBoxBottomRightLongitude)
						break;
					if (currentDestinationSchool.getSchoolLongitude() >= clusterBoxBottomRightLatitude
							&& currentDestinationSchool.getSchoolLongitude() <= clusterBoxTopLeftLatitude) {
						schoolsInClusterCurrentList.add(currentDestinationSchool);
						if (currentLatitudeLongitude.equals(""))
							currentLatitudeLongitude = currentDestinationSchool
									.getSchoolLatitude()
									+ ","
									+ currentDestinationSchool
											.getSchoolLongitude();
						else
							currentLatitudeLongitude = currentLatitudeLongitude
									+ "|"
									+ currentDestinationSchool
											.getSchoolLatitude()
									+ ","
									+ currentDestinationSchool
											.getSchoolLongitude();
						destinationCount++;
					}
				}

				for (schoolListIndex = schoolIndex + 1; schoolListIndex < schoolListSize; schoolListIndex++) {
					currentDestinationSchool = schoolList
							.get(schoolListIndex);

					if (currentDestinationSchool.getSchoolLatitude() > clusterBoxTopLeftLongitude)
						break;
					if (currentDestinationSchool.getSchoolLongitude() >= clusterBoxBottomRightLatitude
							&& currentDestinationSchool.getSchoolLongitude() <= clusterBoxTopLeftLatitude) {
						schoolsInClusterCurrentList.add(currentDestinationSchool);
						if (currentLatitudeLongitude.equals(""))
							currentLatitudeLongitude = currentDestinationSchool
									.getSchoolLatitude()
									+ ","
									+ currentDestinationSchool
											.getSchoolLongitude();
						else
							currentLatitudeLongitude = currentLatitudeLongitude
									+ "|"
									+ currentDestinationSchool
											.getSchoolLatitude()
									+ ","
									+ currentDestinationSchool
											.getSchoolLongitude();
						destinationCount++;
					}
				}
				clusterBoxTopLeftLatitude += clusterBoxIncrement;
				clusterBoxTopLeftLongitude += clusterBoxIncrement;
				clusterBoxBottomRightLatitude -= clusterBoxIncrement;
				clusterBoxBottomRightLongitude -= clusterBoxIncrement;
			}
			schoolsInClusterList.add(schoolsInClusterCurrentList);
			schoolClusterURL[schoolIndex] += currentLatitudeLongitude;
		}
	}

	/**
	 * This method returns the sortedSchoolsCloserToFarthestPoint of URLs. Each URL corresponds to one school
	 * and its cluster.
	 * 
	 * @return The array of URLs for each school
	 */
	public String[] getSchoolClusterUrl() {
		return schoolClusterURL;
	}
}
