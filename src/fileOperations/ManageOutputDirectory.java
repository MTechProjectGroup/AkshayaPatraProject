package fileOperations;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.tomcat.util.http.fileupload.FileUtils;

/**
 * ManageOutputDirectory class handles the creation of output folders.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class ManageOutputDirectory {
	private Properties configFile;
	private String projectPath;
	/**
	 * This is a default constructor that returns the object of type
	 * ManageOutputDirectory.
	 */
	public ManageOutputDirectory() {
		try {
			configFile = new Properties();
			configFile.load(ManageOutputDirectory.class.getClassLoader()
					.getResourceAsStream("config.properties"));
			projectPath = configFile
					.getProperty("projectPath");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method checks if the folder output exists already. If yes, it
	 * deletes the folder and creates a new one with sub-folders
	 * Dijikstra_routes and SchoolsCloserToFarthestPoint_routes.
	 * 
	 * @throws IOException Some error in file handling
	 */
	public void createOutputDirectory() throws IOException {
		File outputFolder = new File(projectPath + "output");
		if (outputFolder.exists())
			FileUtils.deleteDirectory(outputFolder);
		outputFolder = new File(projectPath + "output");
		outputFolder.mkdir();
		File subdir = new File(projectPath + "output\\PrimsMST_routes");
		subdir.mkdir();
		subdir = new File(projectPath
				+ "output\\SchoolsCloserToFarthestPoint_routes");
		subdir.mkdir();
	}
}
