package com.arkabytes.arkaserver;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.arkabytes.arkaserver.database.Database;
import com.arkabytes.arkaserver.database.User;
import com.arkabytes.arkaserver.util.Util;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("arkaserver")
public class ArkaserverUI extends UI {

	private TextField tfUser;
	private PasswordField tfPassword;
	private Button btLogin;
	private Database db;
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ArkaserverUI.class, widgetset = "com.arkabytes.arkaserver.widgetset.ArkaserverWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);
		
		if (request.getParameter("title") != null) {
			String title = request.getParameter("title");
			String message = request.getParameter("message");
			Notification.show(title, message, Notification.Type.TRAY_NOTIFICATION);
		}
		
		Image logo = Util.getImage("logo_arkabytes.png");
		layout.addComponent(logo);

		Panel loginPanel = new Panel("Login");
		loginPanel.setWidth("400px");
		layout.addComponent(loginPanel);

		tfUser = new TextField("User");
		tfPassword = new PasswordField("Password");
		btLogin = new Button("Sign in");
		btLogin.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				
				try {
					if (db == null)
						db = new Database();
					
					db.connect();
					
					User user = db.signin(tfUser.getValue(), tfPassword.getValue());
					if (user == null) {
						Notification.show("Sign in error", "username or password invalid", Notification.Type.ERROR_MESSAGE);
					} else {
						Notification.show("Sign in ok!", "Wellcome " + user.getName(), Notification.Type.TRAY_NOTIFICATION);
						final String path = VaadinServlet.getCurrent().getServletContext().getContextPath() + "/controlpanel";
						getCurrent().getPage().setLocation(path);
					}
					
				} catch (SQLException sqle) {
					Notification.show("Sign in error", sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
				} catch (Exception e) {
					Notification.show("Connection error", "Contact with your provider to check database connection", Notification.Type.ERROR_MESSAGE);
				}
			}
		});
		
		FormLayout loginForm = new FormLayout();
		loginForm.addStyleName("loginlayout");
		loginForm.addComponent(tfUser);
		loginForm.addComponent(tfPassword);
		loginForm.addComponent(btLogin);
		loginForm.setSizeUndefined();
		loginForm.setMargin(true);
		loginPanel.setContent(loginForm);
		
		Link passwordLink = new Link("Have you forgotten your password?", new ExternalResource("/arkaserver/request_password"));
		layout.addComponent(passwordLink);
		
		layout.setComponentAlignment(logo, Alignment.TOP_CENTER);
		layout.setComponentAlignment(loginPanel, Alignment.TOP_CENTER);
		layout.setComponentAlignment(passwordLink, Alignment.TOP_CENTER);
	}
}