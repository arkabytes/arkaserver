package com.arkabytes.arkaserver;

import org.vaadin.activelink.ActiveLink;
import org.vaadin.activelink.ActiveLink.LinkActivatedEvent;
import org.vaadin.activelink.ActiveLink.LinkActivatedListener;

import com.arkabytes.arkaserver.util.Constants;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

/**
 * A view where users write their new password after request it
 * 
 * @author Santiago Faci
 * @version April 2016
 */
@SuppressWarnings("serial")
public class RecoverPasswordView extends AbstractView {

	public TextField tfPassword1;
	public TextField tfPassword2;
	public Button btSave;
	
	public RecoverPasswordView(final Navigator navigator) {
		
		super(navigator, null);
				
		Panel recoverPanel = new Panel("Write your new password");
		recoverPanel.setWidth("400px");
		addComponent(recoverPanel);

		tfPassword1 = new TextField("Password");
		tfPassword2 = new TextField("Re-write Password");
		btSave = new Button("Save");
		btSave.setWidth("250px");
		btSave.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				Notification.show("Your new password have been saved");
				navigator.navigateTo(Constants.LOGIN_VIEW);
			}
		});
		
		FormLayout requestForm = new FormLayout();
		requestForm.addStyleName("requestpassword");
		requestForm.addComponent(tfPassword1);
		requestForm.addComponent(tfPassword2);
		requestForm.addComponent(btSave);
		requestForm.setSizeUndefined();
		requestForm.setMargin(true);
		recoverPanel.setContent(requestForm);
	
				
		setComponentAlignment(recoverPanel, Alignment.TOP_CENTER);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
		
	}

}

