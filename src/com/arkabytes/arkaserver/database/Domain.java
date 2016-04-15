package com.arkabytes.arkaserver.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Information about a domain a everything related to
 * @author Santiago Faci
 *
 */
public class Domain {

	private long id;
	private String name;
	
	private User user;
	private List<EmailAccount> accounts;
	
	public Domain(long id, String name, User user) {
		this.id = id;
		this.name = name;
		this.user = user;
		accounts = new ArrayList<EmailAccount>();
	}
	
	public Domain() {
		accounts = new ArrayList<EmailAccount>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<EmailAccount> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<EmailAccount> accounts) {
		this.accounts = accounts;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
