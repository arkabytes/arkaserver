package com.arkabytes.arkaserver;

import java.sql.SQLException;

import org.vaadin.activelink.ActiveLink;
import org.vaadin.activelink.ActiveLink.LinkActivatedEvent;
import org.vaadin.activelink.ActiveLink.LinkActivatedListener;

import com.arkabytes.arkaserver.database.Database;
import com.arkabytes.arkaserver.database.User;
import com.arkabytes.arkaserver.util.Constants;
import com.arkabytes.arkaserver.util.Util;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * A view where user can log in
 * 
 * @author Santiago Faci
 * @version April 2015
 */
@SuppressWarnings("serial")
public class LoginView extends AbstractView {

	private TextField tfUser;
	private PasswordField tfPassword;
	private Button btLogin;
	
	public LoginView(final Navigator navigator, Database db) {
		
		super(navigator, db);
		
		Panel loginPanel = new Panel("Login");
		loginPanel.setWidth("400px");
		addComponent(loginPanel);

		tfUser = new TextField("User");
		//tfUser.setInputPrompt("User");
		tfPassword = new PasswordField("Password");
		//tfPassword.setInputPrompt("Password");
		tfPassword.addShortcutListener(new ShortcutListener("ENTER", ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				btLogin.click();
			}
		});
		btLogin = new Button("Sign in");
		btLogin.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				
				trySignin();
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
		
		ActiveLink passwordLink = new ActiveLink("Have you forgotten your password?", new ExternalResource(""));
		passwordLink.addListener(new LinkActivatedListener() {
			public void linkActivated(LinkActivatedEvent event) {
				navigator.navigateTo(Constants.PASSWORD_LOST_VIEW);
			}
		});
		addComponent(passwordLink);
		
		setComponentAlignment(loginPanel, Alignment.TOP_CENTER);
		setComponentAlignment(passwordLink, Alignment.TOP_CENTER);
	}
	
	private void trySignin() {
		try {
			db.connect("arkaserver");
			
			User user = db.signin(tfUser.getValue(), tfPassword.getValue());
			if (user == null) {
				Notification.show("Sign in error", "username or password invalid", Notification.Type.ERROR_MESSAGE);
				tfPassword.clear();
				tfPassword.setValue("");
			} else {
				tfUser.clear();
				tfPassword.clear();
				tfPassword.setValue("");
				getSession().setAttribute("username", user.getUsername());
				Notification.show("Sign in ok!", "Wellcome " + user.getName(), Notification.Type.HUMANIZED_MESSAGE);
				navigator.navigateTo(Constants.CONTROL_PANEL_VIEW);
			}
			
		} catch (SQLException sqle) {
			Notification.show("Sign in error", sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
		} catch (Exception e) {
			Notification.show("Connection error", "Contact with your provider to check database connection", Notification.Type.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		tfUser.focus();
	}
}
