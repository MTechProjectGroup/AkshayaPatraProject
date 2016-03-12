package graphAlgorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Properties;

/**
 * AllPairsShortestPath class implements the standard All Pairs Shortest Path
 * algorithm. This is a Singleton class.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class AllPairsShortestPath {
	private int distanceAPSP[][];
	private Properties configFile;
	private static AllPairsShortestPath apsp = new AllPairsShortestPath();
	private int SCHOOL_COUNT = 0;
	private int INFINITY = 999999998;

	/**
	 * This is the default constructor that initializes distance matrix.
	 */
	private AllPairsShortestPath() {
		try {
			configFile = new Properties();

			configFile.load(AllPairsShortestPath.class.getClassLoader()
					.getResourceAsStream("config.properties"));

			SCHOOL_COUNT = Integer.parseInt(configFile
					.getProperty("schoolCount"));
			distanceAPSP = new int[SCHOOL_COUNT][SCHOOL_COUNT];
			for (int[] row : distanceAPSP)
				Arrays.fill(row, INFINITY);

			for (int i = 0; i < SCHOOL_COUNT; i++)
				distanceAPSP[i][i] = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is a factory method that returns the only instance of this class.
	 * 
	 * @return The object of type AllPairsShortestPath
	 */
	public static AllPairsShortestPath getInstance() {
		return apsp;
	}

	/**
	 * This method sets the distance for given two school indices sourceSchool
	 * and destinationSchool.
	 * 
	 * @param sourceSchool Contains the source school index
	 * @param destinationSchool Contains the destination school index
	 * @param inbetweenDistance Contains the distance between source school and destination school
	 */
	public void setDistance(int sourceSchool, int destinationSchool,
			int inbetweenDistance) {
		distanceAPSP[sourceSchool][destinationSchool] = inbetweenDistance;
	}

	/**
	 * This method returns the sortedSchoolsCloserToFarthestPoint of shortest path for given school index.
	 * 
	 * @param schoolIndex Contains the school index
	 * 
	 * @return The array of distances from school with index schoolIndex to other schools
	 */
	public int[] getDistance(int schoolIndex) {
		return distanceAPSP[schoolIndex];
	}

	/**
	 * This method calculates the All Pairs Shortest Path.
	 */
	public void allPairsPath() {
		for (int k = 0; k < SCHOOL_COUNT; k++)
			for (int i = 0; i < SCHOOL_COUNT; i++)
				for (int j = 0; j < SCHOOL_COUNT; j++)
					if (distanceAPSP[i][j] > (distanceAPSP[i][k] + distanceAPSP[k][j]))
						distanceAPSP[i][j] = distanceAPSP[i][k]
								+ distanceAPSP[k][j];
	}

	/**
	 * This method writes the All Pairs Shortest Path distances to a file.
	 * 
	 * @throws FileNotFoundException Some error in file handling
	 */
	public void writeAPSPToFile() throws FileNotFoundException {
		String projectPath = configFile.getProperty("projectPath");
		PrintWriter writeToAPSPcsvFile = new PrintWriter(new File(projectPath
				+ "output\\apsp.csv"));
		for (int i = 0; i < SCHOOL_COUNT; i++) {
			for (int j = 0; j < SCHOOL_COUNT; j++)
				writeToAPSPcsvFile.append(distanceAPSP[i][j] + ",");
			writeToAPSPcsvFile.append("\n");
		}
		writeToAPSPcsvFile.close();
	}
}
