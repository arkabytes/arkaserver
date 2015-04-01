package com.arkabytes.arkaserver.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;

/**
 * Manage database connection
 * @author Santiago Faci
 * @version April 2015
 */
public class Database {

	private Connection connection;
	private final String DB_CONF = "db.properties";
	
	/**
	 * Load database driver
	 * @throws Exception
	 */
	public Database() throws Exception {
			
		Class.forName("com.mysql.jdbc.Driver").newInstance(); 
	}
	
	/**
	 * Read database config file and connect to database
	 * @throws SQLException
	 * @throws IOException
	 */
	public void connect() throws SQLException, IOException {
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource fileResource = new FileResource(new File(basepath + "/WEB-INF/conf/" + DB_CONF));
		Properties props = new Properties();
		props.load(fileResource.getStream().getStream());
		String host = props.getProperty("host");
		String database = props.getProperty("database");
		String username = props.getProperty("username");
		String password = props.getProperty("password");
	
		connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
	}
	
	/**
	 * Sign in if username and password are correct
	 * @param username
	 * @param password
	 * @return null if username or/and password are incorrect
	 * @throws SQLException
	 */
	public User signin(String username, String password) throws SQLException {
		
		String sql = "SELECT * FROM users WHERE username = ? AND password = MD5(?)";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, username);
		statement.setString(2, password);
		
		ResultSet result = statement.executeQuery();
		if (result.next()) {
			User user = new User();
			user.setUsername(result.getString("username"));
			user.setPassword(result.getString("password"));
			user.setPrimaryEmail(result.getString("primary_email"));
			user.setSecondaryEmail(result.getString("secondary_email"));
			user.setName(result.getString("name"));
			user.setPhone(result.getString("phone"));
			user.setWeb(result.getString("web"));
			user.setIcon(result.getString("icon"));
			
			return user;
		}
		
		return null;
	}
}
