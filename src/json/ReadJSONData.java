package json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import modelObjects.SchoolList;
import models.School;
import graphAlgorithms.AllPairsShortestPath;
import graphAlgorithms.AllPairsShortestTime;

/**
 * ReadJSONData class reads from JSON files received from Google Distance Matrix
 * API. This is a Singleton class.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class ReadJSONData {
	private Properties configFile;
	private static ReadJSONData jsonData = new ReadJSONData();
	private String projectPath;

	/**
	 * This constructor reads all the time and distance details from JSON files
	 * for both kitchen to schools and school to other schools.
	 */
	private ReadJSONData() {
		super();
		try {
			configFile = new Properties();
			configFile.load(ReadJSONData.class.getClassLoader()
					.getResourceAsStream("config.properties"));
			projectPath = configFile.getProperty("projectPath");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is a factory method that returns the only instance of this class.
	 * 
	 * @return The instance of type ReadJSONData
	 */
	public static ReadJSONData getInstance() {
		return jsonData;
	}

	/**
	 * This method reads the distances and travel times for each school to every
	 * other school in the cluster.
	 * 
	 * @throws FileNotFoundException Some error in file handling
	 */
	public void readSchoolToOtherSchoolsMetrics() throws FileNotFoundException {
		int currentDistance;
		int currentTravelTime;
		int apspSourceIndex = -1;
		int apspDestinationIndex = -1;
		int apstSourceIndex = -1;
		int apstDestinationIndex = -1;
		int fileIndex = -1;
		School sourceSchool;
		School destinationSchool;
		SchoolList schoolListObject;
		List<School> schoolList;
		List<List<School>> schoolsInClusterList;
		Iterator<School> schoolListIterator;
		Iterator<List<School>> schoolsInClusterListIterator;
		Iterator<School> destinationSchoolListIterator;

		Response jsonResponseObject;
		Item[] jsonItems;
		Element[] jsonElements;
		int jsonElementsIndex;
		int jsonItemsIndex;

		AllPairsShortestPath apspObject = AllPairsShortestPath.getInstance();
		AllPairsShortestTime apstObject = AllPairsShortestTime.getInstance();

		JsonReader readSchoolToOtherSchoolsJSON;
		PrintWriter writeSchoolToOtherSchoolDistances = new PrintWriter(
				new File(projectPath + "output\\SchoolCluster.csv"));

		schoolListObject = SchoolList.getInstance();
		schoolList = schoolListObject.getSchoolKitchenList();
		schoolListIterator = schoolList.iterator();

		schoolsInClusterList = schoolListObject.getSchoolClusterList();
		schoolsInClusterListIterator = schoolsInClusterList.iterator();

		while (schoolListIterator.hasNext()
				&& schoolsInClusterListIterator.hasNext()) {
			sourceSchool = schoolListIterator.next();
			apspSourceIndex++;
			apstSourceIndex++;
			fileIndex++;
			destinationSchoolListIterator = schoolsInClusterListIterator.next()
					.iterator();

			writeSchoolToOtherSchoolDistances.append(sourceSchool
					.getSchoolLatitude()
					+ "@"
					+ sourceSchool.getSchoolLongitude());

			readSchoolToOtherSchoolsJSON = new JsonReader(new BufferedReader(
					new FileReader(projectPath
							+ "input\\json\\SchoolCluster\\distance"
							+ fileIndex + ".json")));
			readSchoolToOtherSchoolsJSON.setLenient(true);
			jsonResponseObject = (new Gson().fromJson(
					readSchoolToOtherSchoolsJSON, Response.class));

			jsonItems = jsonResponseObject.getRows();
			for (jsonItemsIndex = 0; jsonItemsIndex < jsonItems.length; jsonItemsIndex++) {
				jsonElements = jsonItems[jsonItemsIndex].getElements();
				jsonElementsIndex = -1;
				while (destinationSchoolListIterator.hasNext()) {
					destinationSchool = destinationSchoolListIterator.next();
					apspDestinationIndex = destinationSchool.getSchoolIndex();
					apstDestinationIndex = destinationSchool.getSchoolIndex();
					currentDistance = Integer
							.parseInt(jsonElements[++jsonElementsIndex]
									.getDistance().getValue());
					currentTravelTime = Integer
							.parseInt(jsonElements[jsonElementsIndex]
									.getDuration().getValue()) / 60;

					destinationSchool.setDistanceFromSchool(currentDistance);
					destinationSchool.setTravelTime(currentTravelTime);
					apspObject.setDistance(apspSourceIndex,
							apspDestinationIndex - 1, currentDistance);
					apstObject.setTime(apstSourceIndex,
							apstDestinationIndex - 1, currentTravelTime);

					writeSchoolToOtherSchoolDistances.append(","
							+ destinationSchool.getSchoolIndex() + "@"
							+ destinationSchool.getSchoolLatitude() + "@"
							+ destinationSchool.getSchoolLongitude() + "@"
							+ destinationSchool.getSchoolDemand() + "@"
							+ currentDistance + "@"
							+ destinationSchool.getServiceTimeAtSchool() + "@"
							+ currentTravelTime);
				}
				writeSchoolToOtherSchoolDistances.append("\n");
				writeSchoolToOtherSchoolDistances.flush();
			}
		}
		writeSchoolToOtherSchoolDistances.close();
		schoolListObject.setSchoolClusterList(schoolsInClusterList);
	}

	/**
	 * This method reads the distances and travel times for each school from the
	 * kitchen.
	 * 
	 * @throws FileNotFoundException Some error in file handling
	 */
	public void readKitchenToAllSchoolsMetrics() throws FileNotFoundException {
		int currentDistance;
		int currentTravelTime;
		School destinationSchool;
		SchoolList schoolListObject = SchoolList.getInstance();
		List<School> schoolList = schoolListObject.getSchoolKitchenList();
		Iterator<School> schoolListIterator = schoolList.iterator();

		JsonReader readKitchenToSchoolJSON;
		Response jsonResponseObject;
		File jsonFiles = new File(projectPath + "input\\json\\KitchenToSchool");
		int countOfJSONFiles = jsonFiles.listFiles().length;
		int jsonFileIndex = 0;

		Item[] jsonItems;
		Element[] jsonElements;
		int jsonElementsIndex;
		int jsonItemsIndex;

		while (jsonFileIndex < countOfJSONFiles) {
			readKitchenToSchoolJSON = new JsonReader(new BufferedReader(
					new FileReader(projectPath
							+ "input\\json\\KitchenToSchool\\kitchen"
							+ jsonFileIndex + ".json")));
			readKitchenToSchoolJSON.setLenient(true);
			jsonResponseObject = (new Gson().fromJson(readKitchenToSchoolJSON,
					Response.class));
			jsonItems = jsonResponseObject.getRows();
			for (jsonItemsIndex = 0; jsonItemsIndex < jsonItems.length; jsonItemsIndex++) {
				jsonElements = jsonItems[jsonItemsIndex].getElements();
				for (jsonElementsIndex = 0; jsonElementsIndex < jsonElements.length; jsonElementsIndex++) {
					currentTravelTime = Integer
							.parseInt(jsonElements[jsonElementsIndex]
									.getDuration().getValue()) / 60;
					currentDistance = Integer
							.parseInt(jsonElements[jsonElementsIndex]
									.getDistance().getValue());
					if (!schoolListIterator.hasNext())
						break;
					destinationSchool = schoolListIterator.next();
					destinationSchool.setTravelTime(currentTravelTime);
					destinationSchool.setDistanceFromSchool(currentDistance);
				}
			}
			jsonFileIndex++;
		}
	}
}
