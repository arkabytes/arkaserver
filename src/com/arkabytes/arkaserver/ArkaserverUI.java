package com.arkabytes.arkaserver;

import javax.servlet.annotation.WebServlet;

import com.arkabytes.arkaserver.database.Database;
import com.arkabytes.arkaserver.database.User;
import com.arkabytes.arkaserver.util.Constants;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

/**
 * Main UI
 * 
 * @author Santiago Faci
 * @version April 2015
 */
@SuppressWarnings("serial")
@Theme("arkaserver")
@PreserveOnRefresh
public class ArkaserverUI extends UI {

	private Navigator navigator;
	private Database db;
	private boolean userLoggedIn;
	private User currentUser;
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ArkaserverUI.class, widgetset = "com.arkabytes.arkaserver.widgetset.ArkaserverWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		
		getPage().setTitle("Arkabytes Control Panel");
		
		db = new Database();
		
		navigator = new Navigator(this, this);
		navigator.addView(Constants.LOGIN_VIEW, new LoginView(navigator, db));
		navigator.addView(Constants.PASSWORD_LOST_VIEW, new PasswordLostView(navigator));
		navigator.addView(Constants.CONTROL_PANEL_VIEW, new ControlPanelView(navigator, db));
		navigator.addViewChangeListener(new ViewChangeListener() {

			@Override
			public void afterViewChange(ViewChangeEvent event) {
			}

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				if (event.getNewView() instanceof ControlPanelView) {
					if (getSession().getAttribute("username") == null) {
						Notification.show("Permission denied", Notification.Type.ERROR_MESSAGE);
						return false;
					}
				}
				
				return true;
			}
		});
	}
}