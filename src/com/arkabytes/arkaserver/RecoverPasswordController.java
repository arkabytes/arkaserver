package com.arkabytes.arkaserver;

import com.arkabytes.arkaserver.database.Database;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class RecoverPasswordController implements ClickListener {

	private RecoverPasswordView view;
	private Database model;
	
	public RecoverPasswordController(Database model, RecoverPasswordView view) {
		this.model = model;
		this.view = view;
			
		addClickListeners(this);
	}
	
	private void savePassword() {
		
		String password1 = view.tfPassword1.getValue();
		String password2 = view.tfPassword2.getValue();
		
		
	}
	
	private void addClickListeners(ClickListener listener) {
		view.btSave.addClickListener(listener);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		savePassword();
	}
}