package projectStart;

import java.io.IOException;
import java.sql.SQLException;

import json.ReadJSONData;
import modelObjects.VehicleList;
import models.Kitchen;
import fileOperations.ManageOutputDirectory;
import graphAlgorithms.AllPairsShortestPath;
import graphAlgorithms.AllPairsShortestTime;
import graphAlgorithms.CloserToFarthestPoint_ClusterFormation;
import graphAlgorithms.PrimsMST_ClusterFormation;
import graphAlgorithms.InitialClusterFormation;

/**
 * Index initializes all the data structures, forms initial clusters and finally
 * forms the routes.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class Index {
	public static boolean enableDatabase = false;
	public static boolean usePrim = true;

	public static void main(String[] args) throws IOException, SQLException {
		InitialClusterFormation initialCluster;
		@SuppressWarnings("unused")
		Kitchen kitchenObject = Kitchen.getInstance();

		System.out.println("Creating output directory......");
		ManageOutputDirectory outputDirectory = new ManageOutputDirectory();
		outputDirectory.createOutputDirectory();

		System.out.println("Forming initial clusters.....");
		initialCluster = new InitialClusterFormation();
		initialCluster.formInitialClusters();
		System.out.println("Formed initial clusters.....");

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

		System.out.println("Form cluster using Prim's MST.......");
		if (Index.usePrim) {
			System.out.println("Using Prim's MST.........");
			PrimsMST_ClusterFormation primsCluster = new PrimsMST_ClusterFormation();
			primsCluster.formClustersUsingPrimsMST();
			Index.usePrim = false;
			VehicleList vehicleList = VehicleList.getInstance();
			vehicleList.reinitializeVehicles();
		}
		System.out.println("Form cluster closer to the farthest point.......");
		if (!Index.usePrim) {
			System.out.println("Using CloserToFarthestPoint.......");
			CloserToFarthestPoint_ClusterFormation closerToFarthestCluster = new CloserToFarthestPoint_ClusterFormation();
			closerToFarthestCluster.formClusterCloserToFarthestPoints();
		}

		System.out.println("All the clusters have been formed......");
		System.out.println("!!!!!!!!Completed!!!!!!!!!");
	}
}
