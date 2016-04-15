package com.arkabytes.arkaserver;

import com.arkabytes.arkaserver.database.Database;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class PasswordLostController implements ClickListener {

	private PasswordLostView view;
	private Database model;
	
	public PasswordLostController(Database model, PasswordLostView view) {
		this.model = model;
		this.view = view;
			
		addClickListeners(this);
	}
	
	private void requestPassword() {
		
		String email = view.tfEmail.getValue();
		
	}
	
	
	
	private void addClickListeners(ClickListener listener) {
		view.btRequest.addClickListener(listener);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		requestPassword();
	}
}
