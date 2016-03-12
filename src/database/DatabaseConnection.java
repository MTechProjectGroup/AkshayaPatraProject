package database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DatabaseConnection class creates and manages the database connection for Android
 * application. This is a Singleton class. Current implementation is only for
 * MySQL database.
 * 
 * @author Abhishek Joshi, Anand Tirthgirikar, Sandesh Prabhu
 */
public class DatabaseConnection {
	private Connection con;
	private PreparedStatement st;
	private int count;
	private String hostName;
	private String port;
	private String databaseName;
	private String username;
	private String password;

	private static DatabaseConnection databaseConnection = new DatabaseConnection();

	/**
	 * This constructor creates MySQL database connection with host name, host
	 * port, database name, user name and password as variables defined in this
	 * class.
	 */
	private DatabaseConnection() {
		try {
			count = 0;
			hostName = "localhost";
			port = "3306";
			databaseName = "androidlogin";
			username = "root";
			password = "root";
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + hostName + ":"
					+ port + "/" + databaseName, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This is a factory method that returns the only database connection
	 * created.
	 * 
	 * @return The database connection
	 */
	public static DatabaseConnection getInstance() {
		return databaseConnection;
	}

	/**
	 * This method deletes all the entries from the tables user_route_mapping,
	 * user and routes.
	 * 
	 * @throws SQLException Some error while deleting entries from the table
	 */
	public void clearTables() throws SQLException {
		st = con.prepareStatement("delete from user_route_mapping");
		st.execute();
		st = con.prepareStatement("delete from user");
		st.execute();
		st = con.prepareStatement("delete from routes");
		st.execute();
	}

	/**
	 * This method inserts route details in the table routes.
	 * 
	 * @param schoolCount Contains the number of schools
	 * @param route Contains the route details
	 * @throws SQLException Some error while insertion
	 */
	public void insertRoute(int schoolCount, String route) throws SQLException {
		count++;
		st = con.prepareStatement("INSERT INTO routes VALUES (" + count + ",'"
				+ route + "'," + schoolCount + ")");
		st.execute();
	}

	/**
	 * This method inserts user details in the table user.
	 * 
	 * @param userCount Contains the number of users
	 */
	public void insertUser(int userCount) {
		try {
			st = con.prepareStatement("ALTER TABLE user AUTO_INCREMENT = 1");
			st.execute();

			for (int i = 1; i <= userCount; i++) {
				String u = "Driver" + i;
				String q = "INSERT INTO user(username,password) VALUES ('" + u
						+ "','" + u + "')";
				st = con.prepareStatement(q);
				st.execute();
			}
			st = con.prepareStatement("INSERT INTO user(username,password) VALUES ('Manager','Manager')");
			st.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method inserts the mapping of the users with their routes.
	 * 
	 * @param userRouteMappingCount Contains the mapping from user to routes
	 */
	public void insertUserRouteMapping(int userRouteMappingCount) {
		try {
			for (int i = 1; i <= userRouteMappingCount; i++) {
				String q = "INSERT INTO user_route_mapping VALUES ("
						+ (userRouteMappingCount - i + 1) + "," + i + ",0)";
				st = con.prepareStatement(q);
				st.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
