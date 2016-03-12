package graphAlgorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Properties;

/**
 * AllPairsShortestTime class is slight variation of the standard All Pairs
 * Shortest Path algorithm. Instead of finding distances it calculates All Pairs
 * Shortest Time for the travel time. This is a Singleton class.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class AllPairsShortestTime {
	private int timeAPST[][];
	private Properties configFile;
	private static AllPairsShortestTime apst = new AllPairsShortestTime();
	private int SCHOOL_COUNT = 0;
	private int INFINITY = 999999998;

	/**
	 * This is the default constructor that initializes time matrix.
	 */
	private AllPairsShortestTime() {
		try {
			configFile = new Properties();

			configFile.load(AllPairsShortestTime.class.getClassLoader()
					.getResourceAsStream("config.properties"));

			SCHOOL_COUNT = Integer.parseInt(configFile
					.getProperty("schoolCount"));
			timeAPST = new int[SCHOOL_COUNT][SCHOOL_COUNT];
			for (int[] row : timeAPST)
				Arrays.fill(row, INFINITY);

			for (int i = 0; i < SCHOOL_COUNT; i++)
				timeAPST[i][i] = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is a factory method that returns the only instance of this class.
	 * 
	 * @return The object of type AllPairsShortestTime
	 */
	public static AllPairsShortestTime getInstance() {
		return apst;
	}

	/**
	 * This method sets the distance for given two school indices sourceSchool
	 * and destinationSchool.
	 * 
	 * @param sourceSchool Contains the source school index
	 * @param destinationSchool Contains the destination school index
	 * @param inbetweenTravelTime Contains the travel time between source school and destination school
	 */
	public void setDistance(int sourceSchool, int destinationSchool,
			int inbetweenTravelTime) {
		timeAPST[sourceSchool][destinationSchool] = inbetweenTravelTime;
	}

	/**
	 * This method returns the sortedSchoolsCloserToFarthestPoint of shortest time for given source and destination schools.
	 * 
	 * @param sourceSchool Contains the source school index
	 * @param destinationSchool Contains the destination school index
	 * 
	 * @return The travel time between source school and destination school
	 */
	public int getTime(int sourceSchool, int destinationSchool) {
		return timeAPST[sourceSchool][destinationSchool];
	}

	/**
	 * This method sets the time for given source and destination schools.
	 * 
	 * @param sourceSchool Contains the source school index
	 * @param destinationSchool Contains the destination school index
	 * @param time Contains the travel time between source school and destination school
	 */
	public void setTime(int sourceSchool, int destinationSchool, int time) {
		timeAPST[sourceSchool][destinationSchool] = time;
	}

	/**
	 * This method calculates the All Pairs Shortest Time for travel time.
	 */
	public void allPairsTime() {
		for (int k = 0; k < SCHOOL_COUNT; k++)
			for (int i = 0; i < SCHOOL_COUNT; i++)
				for (int j = 0; j < SCHOOL_COUNT; j++)
					if (timeAPST[i][j] > (timeAPST[i][k] + timeAPST[k][j]))
						timeAPST[i][j] = timeAPST[i][k] + timeAPST[k][j];
	}

	/**
	 * This method writes the All Pairs Shortest Time to a file.
	 * 
	 * @throws FileNotFoundException Some error in file handling
	 */
	public void writeAPSTToFile() throws FileNotFoundException {
		String projectPath = configFile.getProperty("projectPath");
		PrintWriter writeToAPSTcsvFile = new PrintWriter(new File(projectPath
				+ "output\\apst.csv"));
		for (int i = 0; i < SCHOOL_COUNT; i++) {
			for (int j = 0; j < SCHOOL_COUNT; j++)
				writeToAPSTcsvFile.append(timeAPST[i][j] + ",");
			writeToAPSTcsvFile.append("\n");
		}
		writeToAPSTcsvFile.close();
	}
}