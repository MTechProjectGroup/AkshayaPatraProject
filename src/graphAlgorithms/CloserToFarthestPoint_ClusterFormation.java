package graphAlgorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import projectStart.IndexServlet;
import database.DatabaseConnection;
import modelObjects.SchoolList;
import modelObjects.VehicleList;
import models.Kitchen;
import models.School;

/**
 * CloserToFarthestPoint_ClusterFormation class forms the clusters with schools
 * closer to the farthest schools. It gives the route to be traversed from the
 * kitchen through this each of the clusters formed.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class CloserToFarthestPoint_ClusterFormation {
	private boolean visitedSchool[];
	private static List<School> currentCluster;
	private Properties configFile;
	private static int tspDistances[][];
	private int schoolVisitingOrder[];
	private int sortedSchoolsCloserToFarthestPoint[][];
	private float totalTime = 0.0f;
	private DatabaseConnection databaseConnectivity;

	private int SCHOOL_COUNT;
	private float TOTAL_TIME_LIMIT;
	private String projectPath;
	private String folderPath;
	private int INFINITY = 999999998;

	/**
	 * This is the default constructor.
	 */
	public CloserToFarthestPoint_ClusterFormation() {
		try {
			configFile = new Properties();
			configFile.load(CloserToFarthestPoint_ClusterFormation.class
					.getClassLoader().getResourceAsStream("config.properties"));
			SCHOOL_COUNT = Integer.parseInt(configFile
					.getProperty("schoolCount"));
			projectPath = configFile.getProperty("projectPath");
			TOTAL_TIME_LIMIT = Float.parseFloat(configFile
					.getProperty("timeLimit"));
			visitedSchool = new boolean[SCHOOL_COUNT];
			folderPath = projectPath + "output\\SchoolsCloserToFarthestPoint_routes\\Route_";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method gives the routes formed for each cluster.
	 * 
	 * @throws SQLException Some error in database
	 * @throws FileNotFoundException Some error in file handling
	 */
	public void formClusterCloserToFarthestPoints() throws SQLException,
			FileNotFoundException {
		School currentFarthestSchool;
		School currentSchool;
		Kitchen kitchenObject = Kitchen.getInstance();
		School kitchen = new School(0, kitchenObject.getKitchenLatitude(),
				kitchenObject.getKitchenLongitude(), 0, 0, "Kitchen");

		SchoolList schoolListObject = SchoolList.getInstance();
		List<School> schoolListSortedOnDistance = schoolListObject
				.getSchoolKitchenList();
		kitchenObject
				.sortSchoolListInDescendingOrderOfDistances(schoolListSortedOnDistance);
		Iterator<School> schoolListSortedOnDistanceIterator = schoolListSortedOnDistance
				.iterator();

		AllPairsShortestTime apstObject = AllPairsShortestTime.getInstance();

		List<School> clusterAroundCurrentFarthestPoint;
		PrintWriter writeTheRouteDetailsToCSV;

		int numberOfSchoolsInRouteForDatabase = 0;
		int routeNumber = 1;
		float currentServiceTime;
		float currentTravelTime;
		int schoolVisitingOrderIndex;
		int visitedSchoolIndex;
		File routesFile;

		for (visitedSchoolIndex = 0; visitedSchoolIndex < SCHOOL_COUNT; visitedSchoolIndex++)
			visitedSchool[visitedSchoolIndex] = false;

		// Ignoring incorrect coordinates
		for (visitedSchoolIndex = 0; visitedSchoolIndex < 16; visitedSchoolIndex++)
			visitedSchool[visitedSchoolIndex] = true;
		visitedSchool[509] = true;

		if (IndexServlet.enableDatabase) {
			databaseConnectivity = DatabaseConnection.getInstance();
			databaseConnectivity.clearTables();
		}

		while (schoolListSortedOnDistanceIterator.hasNext()) {
			currentFarthestSchool = schoolListSortedOnDistanceIterator.next();
			if (visitedSchool[currentFarthestSchool.getSchoolIndex() - 1])
				continue;

			routesFile = new File(folderPath + routeNumber + ".csv");
			writeTheRouteDetailsToCSV = new PrintWriter(routesFile);
			writeTheRouteDetailsToCSV
					.append("\"Latitude,Longitude\",ID,SchoolName,ServiceTime,TravelTime,TotalTime\n");
			writeTheRouteDetailsToCSV.append("\""
					+ kitchenObject.getKitchenLatitude() + ","
					+ kitchenObject.getKitchenLongitude() + "\",,Kitchen,,\n");

			visitedSchool[currentFarthestSchool.getSchoolIndex() - 1] = true;
			currentCluster = new ArrayList<School>();
			currentCluster.add(kitchen);

			clusterAroundCurrentFarthestPoint = clusterFormation(currentFarthestSchool);
			if (clusterAroundCurrentFarthestPoint == null) {
				writeTheRouteDetailsToCSV.close();
				routesFile.delete();
				return;
			}

			currentCluster.add(kitchen);
			currentCluster.addAll(clusterAroundCurrentFarthestPoint);

			String route = kitchenObject.getKitchenLatitude() + "$"
					+ kitchenObject.getKitchenLongitude();
			numberOfSchoolsInRouteForDatabase = 1;

			if (clusterAroundCurrentFarthestPoint.size() == 1){
				currentSchool = currentFarthestSchool;
				totalTime = currentSchool.getServiceTimeAtSchool() + currentSchool.getTravelTime();
			}
			else
				currentSchool = currentCluster.get(schoolVisitingOrder[0]);
			currentServiceTime = currentSchool.getServiceTimeAtSchool();
			currentTravelTime = currentSchool.getTravelTime();
			writeTheRouteDetailsToCSV.append("\""
					+ currentSchool.getSchoolLatitude() + ","
					+ currentSchool.getSchoolLongitude() + "\","
					+ currentSchool.getSchoolIndex() + ","
					+ currentSchool.getSchoolName() + "," + currentServiceTime
					+ "," + currentTravelTime + ","
					+ (currentServiceTime + currentTravelTime) + "\n");
			route = route + "@" + currentSchool.getSchoolLatitude() + "$"
					+ currentSchool.getSchoolLongitude();
			numberOfSchoolsInRouteForDatabase++;

			if (clusterAroundCurrentFarthestPoint.size() > 1) {
				for (schoolVisitingOrderIndex = 1; schoolVisitingOrderIndex < schoolVisitingOrder.length; schoolVisitingOrderIndex++) {
					currentSchool = currentCluster
							.get(schoolVisitingOrder[schoolVisitingOrderIndex]);
					currentServiceTime = currentSchool.getServiceTimeAtSchool();
					currentTravelTime = apstObject
							.getTime(
									currentCluster
											.get(schoolVisitingOrder[schoolVisitingOrderIndex - 1])
											.getSchoolIndex() - 1,
									currentCluster
											.get(schoolVisitingOrder[schoolVisitingOrderIndex])
											.getSchoolIndex() - 1);
					writeTheRouteDetailsToCSV.append("\""
							+ currentSchool.getSchoolLatitude() + ","
							+ currentSchool.getSchoolLongitude() + "\","
							+ currentSchool.getSchoolIndex() + ","
							+ currentSchool.getSchoolName() + ","
							+ currentServiceTime + "," + currentTravelTime
							+ "," + (currentServiceTime + currentTravelTime)
							+ "\n");
					route = route + "@" + currentSchool.getSchoolLatitude()
							+ "$" + currentSchool.getSchoolLongitude();
					numberOfSchoolsInRouteForDatabase++;
				}
			}

			if (IndexServlet.enableDatabase)
				databaseConnectivity.insertRoute(
						numberOfSchoolsInRouteForDatabase, route);

			routeNumber++;
			writeTheRouteDetailsToCSV
					.append("\n\n,,Route Details\n,,Total time," + totalTime
							+ " min");
			schoolListSortedOnDistanceIterator = schoolListSortedOnDistance
					.iterator();
			writeTheRouteDetailsToCSV.flush();
			writeTheRouteDetailsToCSV.close();
		}

		if (IndexServlet.enableDatabase) {
			databaseConnectivity.insertUser(routeNumber);
			databaseConnectivity.insertUserRouteMapping(routeNumber);
		}
	}

	/**
	 * This method gives the cluster of schools closer to the farthestSchool. It
	 * checks if new schools can be added to the cluster around current farthest
	 * school using the school demands and vehicle capacity remaining.
	 * 
	 * @param farthestSchool Contains the current farthest point
	 * 
	 * @return The list containing all the schools in current cluster
	 */
	public List<School> clusterFormation(School farthestSchool) {
		List<School> currentClusterAroundFarthestPointList = new ArrayList<School>();
		currentClusterAroundFarthestPointList.add(farthestSchool);
		VehicleList vehicleList = VehicleList.getInstance();
		AllPairsShortestPath apspObject = AllPairsShortestPath.getInstance();

		int vehicleCapacity = vehicleList.getVehicle();
		if (vehicleCapacity == 0) {
			System.out.println("####No more vehicles available. All schools are not covered####");
			return null;
		}
		boolean currentlyVisitedSchools[] = new boolean[SCHOOL_COUNT];
		int currentlyVisitedSchoolIndex;
		int currentClusterAroundFarthestPointListIndex = 0;
		int currentSchoolIndex;
		int currentSchoolDemand;
		int schoolsCloserToFarthestPoint[][];
		School currentSchool;

		// Ignoring incorrect coordinates
		for (currentlyVisitedSchoolIndex = 0; currentlyVisitedSchoolIndex < 16; currentlyVisitedSchoolIndex++)
			currentlyVisitedSchools[currentlyVisitedSchoolIndex] = true;
		currentlyVisitedSchools[509] = true;

		while (vehicleCapacity > 0) {
			currentSchoolIndex = -1;
			currentSchoolDemand = 0;

			schoolsCloserToFarthestPoint = new int[2][SCHOOL_COUNT];

			currentSchool = currentClusterAroundFarthestPointList
					.get(currentClusterAroundFarthestPointListIndex);
			schoolsCloserToFarthestPoint[1] = apspObject
					.getDistance(currentSchool.getSchoolIndex() - 1);

			for (int i = 0; i < SCHOOL_COUNT; i++)
				schoolsCloserToFarthestPoint[0][i] = i;

			sortSchoolsInAscendingOrderOfDistance(schoolsCloserToFarthestPoint);

			for (int y = 0; y < SCHOOL_COUNT && vehicleCapacity > 0; y++) {
				if (schoolsCloserToFarthestPoint[1][y] == INFINITY)
					continue;

				currentSchoolIndex = schoolsCloserToFarthestPoint[0][y];

				if (!visitedSchool[currentSchoolIndex]
						&& !currentlyVisitedSchools[currentSchoolIndex]) {
					currentSchoolDemand = getSchoolDemandGivenSchoolIndex(currentSchoolIndex);
					if (vehicleCapacity >= currentSchoolDemand) {
						if (canCurrentSchoolBeAddedToCurrentFarthestPointCluster(
								currentClusterAroundFarthestPointList,
								getSchoolGivenSchoolIndex(currentSchoolIndex))) {
							currentClusterAroundFarthestPointList
									.add(getSchoolGivenSchoolIndex(currentSchoolIndex));
							visitedSchool[currentSchoolIndex] = true;
							vehicleCapacity = vehicleCapacity
									- currentSchoolDemand;
						} else
							currentlyVisitedSchools[currentSchoolIndex] = true;
					}
				}
			}

			currentClusterAroundFarthestPointListIndex++;
			if (currentClusterAroundFarthestPointListIndex == currentClusterAroundFarthestPointList
					.size())
				break;
		}
		currentCluster.clear();
		return currentClusterAroundFarthestPointList;
	}

	/**
	 * This method checks if the newSchool can be added to the cluster around
	 * the current farthest school. It checks this based on the service time and
	 * travel time. It uses Traveling Salesman Problem algorithm.
	 * 
	 * @param currentFarthestPointCluster Contains the list of schools in current cluster
	 * @param newSchool Contains the new school that needs to be added to the list
	 * 
	 * @return true or false
	 */
	public boolean canCurrentSchoolBeAddedToCurrentFarthestPointCluster(
			List<School> currentFarthestPointCluster, School newSchool) {
		boolean canBeAdded = false;
		Iterator<School> currentFarthestPointClusterIterator;
		Iterator<School> destinationSchoolIterator;
		Kitchen kitchenObject = Kitchen.getInstance();
		School kitchen = new School(0, kitchenObject.getKitchenLatitude(),
				kitchenObject.getKitchenLongitude(), 0, 0, "Kitchen");
		School sourceSchool;
		School destinationSchool;

		currentCluster.clear();
		currentCluster.add(kitchen);
		currentCluster.addAll(currentFarthestPointCluster);
		currentCluster.add(newSchool);
		Iterator<School> currentClusterIterator = currentCluster.iterator();
		currentClusterIterator.next();
		AllPairsShortestPath apspObject = AllPairsShortestPath.getInstance();
		AllPairsShortestTime apstObject = AllPairsShortestTime.getInstance();

		SchoolList schoolListObject = SchoolList.getInstance();
		List<School> schoolList = schoolListObject.getSchoolKitchenList();
		Iterator<School> schoolListIterator;

		tspDistances = new int[currentCluster.size()][currentCluster.size()];
		tspDistances[0][0] = 0;
		int visitingOrderBeforeChanges[] = new int[currentCluster.size() - 1];
		currentFarthestPointClusterIterator = currentCluster.iterator();
		int tspDistancesDestinationIndex = 0;
		int tspDistancesIndex = 0;
		int tspDistancesSourceIndex = 0;
		int schoolIndex;
		int visitingOrderBeforeChangesIndex;
		int schoolsInSourceSchoolCluster[] = new int[SCHOOL_COUNT];
		float currentTotalTime = 0.0f;

		currentFarthestPointClusterIterator.next();
		while (currentFarthestPointClusterIterator.hasNext()) {
			sourceSchool = currentFarthestPointClusterIterator.next();
			schoolIndex = sourceSchool.getSchoolIndex();
			schoolListIterator = schoolList.iterator();
			while (schoolListIterator.hasNext()) {
				destinationSchool = schoolListIterator.next();
				if (destinationSchool.getSchoolIndex() == schoolIndex) {
					tspDistances[0][++tspDistancesIndex] = destinationSchool
							.getDistanceFromSchool();
					break;
				}
			}

			schoolsInSourceSchoolCluster = apspObject
					.getDistance(schoolIndex - 1);
			destinationSchoolIterator = currentCluster.iterator();
			tspDistancesSourceIndex++;
			tspDistancesDestinationIndex = 0;
			destinationSchoolIterator.next();
			while (destinationSchoolIterator.hasNext()) {
				destinationSchool = destinationSchoolIterator.next();
				tspDistancesDestinationIndex++;
				tspDistances[tspDistancesSourceIndex][tspDistancesDestinationIndex] = schoolsInSourceSchoolCluster[destinationSchool
						.getSchoolIndex() - 1];
			}
		}

		for (tspDistancesSourceIndex = 1; tspDistancesSourceIndex < currentCluster
				.size(); tspDistancesSourceIndex++)
			tspDistances[tspDistancesSourceIndex][0] = tspDistances[0][tspDistancesSourceIndex];

		TravelingSalesmanProblem tspObject = new TravelingSalesmanProblem();
		visitingOrderBeforeChanges = tspObject.tsp(tspDistances);

		schoolListIterator = schoolList.iterator();
		while (schoolListIterator.hasNext()) {
			sourceSchool = schoolListIterator.next();
			if (sourceSchool.getSchoolIndex() == currentCluster.get(
					visitingOrderBeforeChanges[0]).getSchoolIndex()) {
				currentTotalTime = sourceSchool.getServiceTimeAtSchool()
						+ sourceSchool.getTravelTime();
				break;
			}
		}

		for (visitingOrderBeforeChangesIndex = 1; visitingOrderBeforeChangesIndex < visitingOrderBeforeChanges.length; visitingOrderBeforeChangesIndex++) {
			schoolListIterator = schoolList.iterator();
			while (schoolListIterator.hasNext()) {
				sourceSchool = schoolListIterator.next();
				if (sourceSchool.getSchoolIndex() == currentCluster
						.get(visitingOrderBeforeChanges[visitingOrderBeforeChangesIndex])
						.getSchoolIndex()) {
					currentTotalTime += sourceSchool.getServiceTimeAtSchool();
					break;
				}
			}
			currentTotalTime += apstObject
					.getTime(
							currentCluster
									.get(visitingOrderBeforeChanges[visitingOrderBeforeChangesIndex - 1])
									.getSchoolIndex() - 1,
							currentCluster
									.get(visitingOrderBeforeChanges[visitingOrderBeforeChangesIndex])
									.getSchoolIndex() - 1);
		}

		if (currentTotalTime <= TOTAL_TIME_LIMIT) {
			totalTime = currentTotalTime;
			canBeAdded = true;
			schoolVisitingOrder = new int[visitingOrderBeforeChanges.length];
			schoolVisitingOrder = visitingOrderBeforeChanges;
		}
		return canBeAdded;
	}

	/**
	 * This method calls the quicksort function on the given array
	 * schoolsCloserToFarthestPoint.
	 * 
	 * @param schoolsCloserToFarthestPoint Contains the index and distances of schools closer for a given school 
	 */
	private void sortSchoolsInAscendingOrderOfDistance(
			int[][] schoolsCloserToFarthestPoint) {
		this.sortedSchoolsCloserToFarthestPoint = schoolsCloserToFarthestPoint;
		sortSchoolsInAscendingOrderOfDistanceUsingQuickSort(0, SCHOOL_COUNT - 1);
	}

	/**
	 * This method is slight modification of the quicksort algorithm.
	 * 
	 * @param lowerIndex Contains the lower bound
	 * @param higherIndex Contains the upper bound
	 */
	private void sortSchoolsInAscendingOrderOfDistanceUsingQuickSort(
			int lowerIndex, int higherIndex) {
		int lowerBound = lowerIndex;
		int upperBound = higherIndex;
		int pivot = sortedSchoolsCloserToFarthestPoint[1][lowerIndex
				+ (higherIndex - lowerIndex) / 2];
		while (lowerBound <= upperBound) {
			while (sortedSchoolsCloserToFarthestPoint[1][lowerBound] < pivot)
				lowerBound++;
			while (sortedSchoolsCloserToFarthestPoint[1][upperBound] > pivot)
				upperBound--;
			if (lowerBound <= upperBound) {
				exchangeDistanceandIndex(lowerBound, upperBound);
				lowerBound++;
				upperBound--;
			}
		}
		if (lowerIndex < upperBound)
			sortSchoolsInAscendingOrderOfDistanceUsingQuickSort(lowerIndex,
					upperBound);
		if (lowerBound < higherIndex)
			sortSchoolsInAscendingOrderOfDistanceUsingQuickSort(lowerBound,
					higherIndex);
	}

	/**
	 * This method swaps the value of the distance and school index for two
	 * schools.
	 * 
	 * @param distanceIndex Contains the school index of distance array
	 * @param schoolIndex Contains the school index of index array
	 */
	private void exchangeDistanceandIndex(int distanceIndex, int schoolIndex) {
		int changingIndexAndDistance = sortedSchoolsCloserToFarthestPoint[1][distanceIndex];
		sortedSchoolsCloserToFarthestPoint[1][distanceIndex] = sortedSchoolsCloserToFarthestPoint[1][schoolIndex];
		sortedSchoolsCloserToFarthestPoint[1][schoolIndex] = changingIndexAndDistance;
		changingIndexAndDistance = sortedSchoolsCloserToFarthestPoint[0][distanceIndex];
		sortedSchoolsCloserToFarthestPoint[0][distanceIndex] = sortedSchoolsCloserToFarthestPoint[0][schoolIndex];
		sortedSchoolsCloserToFarthestPoint[0][schoolIndex] = changingIndexAndDistance;
	}

	/**
	 * This method returns the demand of the school given the school index.
	 * 
	 * @param schoolIndex Contains the school index of which demand is needed
	 * 
	 * @return The school demand
	 */
	public int getSchoolDemandGivenSchoolIndex(int schoolIndex) {
		int schoolDemand = 0;
		School currentSchool;
		SchoolList schoolListObject = SchoolList.getInstance();
		List<School> schoolList = schoolListObject.getSchoolKitchenList();
		Iterator<School> schoolListIterator = schoolList.iterator();
		while (schoolListIterator.hasNext()) {
			currentSchool = schoolListIterator.next();
			if (currentSchool.getSchoolIndex() - 1 == schoolIndex)
				schoolDemand = currentSchool.getSchoolDemand();
		}
		return schoolDemand;
	}

	/**
	 * This method returns the school details given the school index.
	 * 
	 * @param schoolIndex Contains the school index of which details are needed
	 * 
	 * @return The School object
	 */
	public School getSchoolGivenSchoolIndex(int schoolIndex) {
		School currentSchool = null;
		SchoolList schoolListObject = SchoolList.getInstance();
		List<School> schoolList = schoolListObject.getSchoolKitchenList();
		Iterator<School> schoolListIterator = schoolList.iterator();
		while (schoolListIterator.hasNext()) {
			currentSchool = schoolListIterator.next();
			if ((currentSchool.getSchoolIndex() - 1) == schoolIndex)
				break;
		}
		return currentSchool;
	}
}
