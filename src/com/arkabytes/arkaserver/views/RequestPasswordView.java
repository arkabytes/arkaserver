package com.arkabytes.arkaserver.views;

public interface RequestPasswordView {
	
	public void sendPassword();
	
	interface RequestPasswordListener {
		void buttonClick(char operation);
	}
	
	public void addListener(RequestPasswordListener listener);
}
