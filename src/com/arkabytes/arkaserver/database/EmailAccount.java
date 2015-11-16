package com.arkabytes.arkaserver.database;

/**
 * A domain user (email address)
 * 
 * @author Santiago Faci
 * @version November 2015
 */
public class EmailAccount {

	private long id;
	private String email;
	private String password;
	
	private Domain domain;
	
	public EmailAccount(String email, String password, Domain domain) {
		this.email = email;
		this.password = password;
		this.domain = domain;
	}
	
	public EmailAccount() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	@Override
	public String toString() {
		return email;
	}
	
}
