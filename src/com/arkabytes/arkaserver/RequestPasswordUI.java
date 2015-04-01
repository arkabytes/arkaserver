package com.arkabytes.arkaserver;

import javax.servlet.annotation.WebServlet;

import com.arkabytes.arkaserver.util.Util;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
@Theme("arkaserver")
public class RequestPasswordUI extends UI {

	Button btRequest;
	
	@WebServlet(value = "/request_password/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = RequestPasswordUI.class)
	public static class Servlet extends VaadinServlet {
	}
	
	@Override
	protected void init(VaadinRequest request) {
		
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);
			
		Image logo = Util.getImage("logo_arkabytes.png");
		layout.addComponent(logo);
		
		Panel requestPanel = new Panel("Request Password");
		requestPanel.setWidth("400px");
		layout.addComponent(requestPanel);

		TextField tfEmail = new TextField("E-Mail");
		btRequest = new Button("Send my password");
		btRequest.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				getUI().getPage().setLocation("/arkaserver/?title=Password sent!&message=Check your email address");
			}
		});
		
		FormLayout requestForm = new FormLayout();
		requestForm.addStyleName("requestpassword");
		requestForm.addComponent(tfEmail);
		requestForm.addComponent(btRequest);
		requestForm.setSizeUndefined();
		requestForm.setMargin(true);
		requestPanel.setContent(requestForm);
		
		Link homeLink = new Link("Return Home", new ExternalResource("/arkaserver"));
		layout.addComponent(homeLink);
				
		layout.setComponentAlignment(logo, Alignment.TOP_CENTER);
		layout.setComponentAlignment(requestPanel, Alignment.TOP_CENTER);
		layout.setComponentAlignment(homeLink, Alignment.TOP_CENTER);
	}
}
