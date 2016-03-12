package graphAlgorithms;

import java.util.Stack;

/**
 * TravelingSalesmanProblem class is the implementation of traditional Traveling
 * Salesman Problem algorithm using tspStack.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 * 
 */
public class TravelingSalesmanProblem {
	private int numberOfSchools;
	private Stack<Integer> tspStack;

	/**
	 * This is the default constructor.
	 */
	public TravelingSalesmanProblem() {
		tspStack = new Stack<Integer>();
	}

	/**
	 * This method applies TSP algorithm on given source-destination matrix and
	 * returns the visiting order.
	 * 
	 * @param sourceDestinationSchoolMatrix Contains the distances from each school to every other school in consideration
	 * 
	 * @return The visiting order of the schools
	 */
	public int[] tsp(int sourceDestinationSchoolMatrix[][]) {
		numberOfSchools = sourceDestinationSchoolMatrix[1].length - 1;
		int[] schoolVisitingOrder = new int[numberOfSchools];
		boolean[] isVisited = new boolean[numberOfSchools + 1];
		int sourceSchool;
		int currentSchool = 0;
		int destinationSchool;
		int schoolVisitingOrderIndex = -1;
		int currentMinimumDistance = Integer.MAX_VALUE;
		boolean schoolWithMinimumDistanceFound = false;

		isVisited[0] = true;
		tspStack.push(0);
		while (!tspStack.isEmpty()) {
			sourceSchool = tspStack.peek();
			destinationSchool = 1;
			currentMinimumDistance = Integer.MAX_VALUE;
			while (destinationSchool <= numberOfSchools) {
				if (sourceDestinationSchoolMatrix[sourceSchool][destinationSchool] > 1
						&& !isVisited[destinationSchool]) {
					if (currentMinimumDistance > sourceDestinationSchoolMatrix[sourceSchool][destinationSchool]) {
						currentMinimumDistance = sourceDestinationSchoolMatrix[sourceSchool][destinationSchool];
						currentSchool = destinationSchool;
						schoolWithMinimumDistanceFound = true;
					}
				}
				destinationSchool++;
			}
			if (schoolWithMinimumDistanceFound) {
				isVisited[currentSchool] = true;
				tspStack.push(currentSchool);
				schoolVisitingOrder[++schoolVisitingOrderIndex] = currentSchool;
				schoolWithMinimumDistanceFound = false;
				continue;
			}
			tspStack.pop();
		}
		return schoolVisitingOrder;
	}
}