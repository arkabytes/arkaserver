package com.arkabytes.arkaserver.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;

/**
 * Manage database connection
 * 
 * @author Santiago Faci
 * @version April 2015
 */
public class Database {

	private Connection connection;
	private final String DB_CONF = "db.properties";
	
	/**
	 * Empty constructor
	 * @throws Exception
	 */
	public Database() {
	}
	
	/**
	 * Read database config file and connect to database
	 * @throws SQLException
	 * @throws IOException
	 */
	public void connect(String database) throws SQLException, IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource fileResource = new FileResource(new File(basepath + "/WEB-INF/conf/" + DB_CONF));
		Properties props = new Properties();
		props.load(fileResource.getStream().getStream());
		String host = props.getProperty("host");
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
			
			result.close();
			return user;
		}
		
		return null;
	}
	
	/**
	 * Get an user based on his username
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public User getUser(String username) throws SQLException {
		
		User user = null;
		String sql = "SELECT * FROM users WHERE username = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, username);
		
		ResultSet result = statement.executeQuery();
		if (result.first()) {
			user = new User();
			user.setUsername(result.getString("username"));
			user.setName(result.getString("name"));
			user.setPrimaryEmail(result.getString("primary_email"));
			user.setSecondaryEmail(result.getString("secondary_email"));
			user.setWeb(result.getString("web"));
			user.setPhone(result.getString("phone"));
		}
		
		return user;
	}
	
	/**
	 * Update info about an user
	 * @param user
	 * @throws SQLException
	 */
	public void updateUser(User user) 
		throws SQLException {
		
		String sql = null;
		if (user.getPassword() == null) {
			sql = "UPDATE users SET name = ?, primary_email = ?, secondary_email = ?, " +
					"web = ?, phone = ? WHERE username = ?";
		} else {
			sql = "UPDATE users SET password = MD5(?), name = ?, primary_email = ?, secondary_email = ?, " +
					"web = ?, phone = ? WHERE username = ?";
		}
		PreparedStatement statement = connection.prepareStatement(sql);
		int pos = 1;
		if (user.getPassword() != null) {
			statement.setString(1, user.getPassword());
			pos++;
		}
		statement.setString(pos++, user.getName());
		statement.setString(pos++, user.getPrimaryEmail());
		statement.setString(pos++, user.getSecondaryEmail());
		statement.setString(pos++, user.getWeb());
		statement.setString(pos++, user.getPhone());
		statement.setString(pos++, user.getUsername());
		
		statement.executeUpdate();
		statement.close();
	}
	
	/**
	 * Get all domains managed by the server
	 * @return
	 * @throws SQLException
	 */
	public List<Domain> getDomains() throws SQLException {
		
		List<Domain> domains = new ArrayList<Domain>();
		String sql = "SELECT id, name from virtual_domains";
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet result = statement.executeQuery();
		
		Domain domain = null;
		while (result.next()) {
			domain = new Domain();
			domain.setId(result.getInt("id"));
			domain.setName(result.getString("name"));
			domain.setAccounts(getEmailAccounts(domain.getName()));
			domains.add(domain);
		}
		
		return domains;
	}
	
	/**
	 * Add a domain to the database
	 * @param name
	 * @throws SQLException
	 */
	public void addDomain(String name) throws SQLException {
		
		String sql = "INSERT INTO virtual_domains (name) VALUES (?)";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, name);
		statement.executeUpdate();
	}
	
	/**
	 * Add a new email account to the database
	 * @param domain
	 * @param email
	 * @param password
	 * @throws SQLException
	 */
	public void addEmailAccount(Domain domain, String email, String password) throws SQLException {
		
		String sql = "INSERT INTO virtual_users (domain_id, email, password) VALUES (?, ?, MD5(?))";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setLong(1, domain.getId());
		statement.setString(2, email);
		statement.setString(3, password);
		statement.executeUpdate();
	}
	
	/**
	 * Change account password
	 * @param email
	 * @param password
	 * @throws SQLException
	 */
	public void changeEmailAccountPassword(String email, String password) throws SQLException {
		
		String sql = "UPDATE virtual_users SET password = MD5(?) WHERE email = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, password);
		statement.setString(2, email);
		statement.executeUpdate();
	}
	
	/**
	 * Get email accounts related to the domain specified
	 * @param domainName
	 * @return
	 * @throws SQLException
	 */
	public List<EmailAccount> getEmailAccounts(String domainName) throws SQLException {
		
		List<EmailAccount> accounts = new ArrayList<EmailAccount>();
		String sql = "SELECT password, email from virtual_users vu, virtual_domains vd " +
				"WHERE vu.domain_id = vd.id AND vd.name = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, domainName);
		ResultSet result = statement.executeQuery();
		
		EmailAccount account = null;
		while (result.next()) {
			account = new EmailAccount();
			account.setEmail(result.getString("email"));
			account.setPassword(result.getString("password"));
			
			accounts.add(account);
		}
		
		return accounts;
	}
}
