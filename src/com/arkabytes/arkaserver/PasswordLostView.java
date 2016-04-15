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
 * A view where users can request a new password if they have missed it
 * 
 * @author Santiago Faci
 * @version April 2015
 */
@SuppressWarnings("serial")
public class PasswordLostView extends AbstractView {

	public TextField tfEmail;
	public Button btRequest;
	
	public PasswordLostView(final Navigator navigator) {
		
		super(navigator, null);
				
		Panel requestPanel = new Panel("Request Password");
		requestPanel.setWidth("400px");
		addComponent(requestPanel);

		tfEmail = new TextField("E-Mail");
		btRequest = new Button("Send my password");
		btRequest.setWidth("250px");
		btRequest.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				Notification.show("Check your email address");
				navigator.navigateTo(Constants.LOGIN_VIEW);
			}
		});
		
		FormLayout requestForm = new FormLayout();
		requestForm.addStyleName("requestpassword");
		requestForm.addComponent(tfEmail);
		requestForm.addComponent(btRequest);
		requestForm.setSizeUndefined();
		requestForm.setMargin(true);
		requestPanel.setContent(requestForm);
		
		ActiveLink homeLink = new ActiveLink("Return home", new ExternalResource(""));
		homeLink.addListener(new LinkActivatedListener() {
			public void linkActivated(LinkActivatedEvent event) {
				navigator.navigateTo(Constants.LOGIN_VIEW);
			}
		});
		addComponent(homeLink);
				
		setComponentAlignment(requestPanel, Alignment.TOP_CENTER);
		setComponentAlignment(homeLink, Alignment.TOP_CENTER);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
		
	}

}
