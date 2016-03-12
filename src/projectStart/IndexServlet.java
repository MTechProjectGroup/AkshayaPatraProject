package projectStart;

import fileOperations.ManageOutputDirectory;
import graphAlgorithms.AllPairsShortestPath;
import graphAlgorithms.AllPairsShortestTime;
import graphAlgorithms.PrimsMST_ClusterFormation;
import graphAlgorithms.InitialClusterFormation;
import graphAlgorithms.CloserToFarthestPoint_ClusterFormation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import json.ReadJSONData;
import modelObjects.VehicleList;
import models.Kitchen;

/**
 * IndexServlet initializes all the data structures, forms initial clusters,
 * gets required metrics from Google API and finally forms the routes.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
@WebServlet("/IndexServlet")
public class IndexServlet extends HttpServlet {
	public static boolean enableDatabase = false;
	private boolean usePrim = true;
	private InitialClusterFormation initialCluster;
	private Kitchen kitchenObject = Kitchen.getInstance();

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		System.out.println("Creating output directory......");
		ManageOutputDirectory outputDirectory = new ManageOutputDirectory();
		outputDirectory.createOutputDirectory();

		System.out.println("Forming initial clusters.....");
		initialCluster = new InitialClusterFormation();
		initialCluster.formInitialClusters();
		System.out.println("Formed initial clusters.....");

		System.out
				.println("Call Google API to get the distances from a school to other schools in the cluster.....");
		getMetricsFromGoogleAPIandSaveJSONFiles("For school");
		System.out.println("Created JSON files for the distances.....");

		System.out
				.println("Call Google API to get the distances from kitchen to all schools.........");
		getMetricsFromGoogleAPIandSaveJSONFiles("For kitchen");

		System.out
				.println("Creating csv files for All Pairs Shortest Path and Time.......");
		ReadJSONData readJSON = ReadJSONData.getInstance();
		readJSON.readSchoolToOtherSchoolsMetrics();
		System.out.println("Created CSV.......");

		System.out.println("Add Kitchen Details in origins list.......");
		readJSON.readKitchenToAllSchoolsMetrics();

		System.out
				.println("Find all pairs shortest path among the clusters.......");
		AllPairsShortestPath apspObject = AllPairsShortestPath.getInstance();
		apspObject.allPairsPath();
		apspObject.writeAPSPToFile();

		System.out
				.println("Find all pairs shortest time among the clusters.......");
		AllPairsShortestTime apstObject = AllPairsShortestTime.getInstance();
		apstObject.allPairsTime();
		apstObject.writeAPSTToFile();

		try {
			System.out.println("Form cluster using Prim's MST.......");
			if (usePrim) {
				System.out.println("Using Prim's MST.........");
				PrimsMST_ClusterFormation primsCluster = new PrimsMST_ClusterFormation();
				primsCluster.formClustersUsingPrimsMST();
				usePrim = false;
				VehicleList vehicleList = VehicleList.getInstance();
				vehicleList.reinitializeVehicles();
			}
			System.out
					.println("Form cluster closer to the farthest point.......");
			if (!usePrim) {
				System.out.println("Using CloserToFarthestPoint.......");
				CloserToFarthestPoint_ClusterFormation closerToFarthestCluster = new CloserToFarthestPoint_ClusterFormation();
				closerToFarthestCluster.formClusterCloserToFarthestPoints();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("All the clusters have been formed......");
		System.out.println("!!!!!!!!Completed!!!!!!!!!");
	}

	/**
	 * This method calls the Google Distance Matrix API with the URLs and gets
	 * the response in the form of JSON.
	 * 
	 * @param source Contains either school or kitchen
	 */
	private void getMetricsFromGoogleAPIandSaveJSONFiles(String source) {
		try {
			String[] URLs;
			int URL_INDEX;
			String fileToWriteJSONData;
			BufferedReader responseJSON;
			Properties configFile;
			String projectPath;
			String inputLine;
			PrintWriter writeJSON;

			configFile = new Properties();
			configFile.load(ManageOutputDirectory.class.getClassLoader()
					.getResourceAsStream("config.properties"));
			projectPath = configFile.getProperty("projectPath");

			if (source.contains("For school")) {
				fileToWriteJSONData = projectPath
						+ "input\\json\\SchoolCluster\\distance";
				URLs = initialCluster.getSchoolClusterUrl();
			} else {
				fileToWriteJSONData = projectPath
						+ "input\\json\\KitchenToSchool\\kitchen";
				URLs = kitchenObject.getKitchenSchoolUrl();
			}

			for (URL_INDEX = 0; URL_INDEX < URLs.length; URL_INDEX++) {
				URL gmaps = new URL(URLs[URL_INDEX]);
				URLConnection connectGMaps = gmaps.openConnection();
				responseJSON = new BufferedReader(new InputStreamReader(
						connectGMaps.getInputStream()));
				writeJSON = new PrintWriter(new File(fileToWriteJSONData
						+ URL_INDEX + ".json"));
				while ((inputLine = responseJSON.readLine()) != null) {
					writeJSON.append(inputLine);
				}
				writeJSON.close();
				responseJSON.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}
}
