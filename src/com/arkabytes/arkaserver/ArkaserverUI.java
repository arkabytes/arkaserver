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
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ArkaserverUI.class, widgetset = "com.arkabytes.arkaserver.widgetset.ArkaserverWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		
		getPage().setTitle("Arkabytes Control Panel");
		
		db = new Database();
		navigator = new Navigator(this, this);
		
		LoginView lView = new LoginView(navigator, db);
		PasswordLostView plView = new PasswordLostView(navigator);
		ControlPanelView cpView = new ControlPanelView(navigator, db);
		navigator.addView(Constants.LOGIN_VIEW, lView);
		navigator.addView(Constants.PASSWORD_LOST_VIEW, plView);
		navigator.addView(Constants.CONTROL_PANEL_VIEW, cpView);
		navigator.addViewChangeListener(new ViewChangeListener() {

			@Override
			public void afterViewChange(ViewChangeEvent event) {
			}

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				if (event.getNewView() instanceof ControlPanelView) {
					if (getSession().getAttribute(User.class) == null) {
						Notification.show("Permission denied", Notification.Type.ERROR_MESSAGE);
						return false;
					}
				}
				
				return true;
			}
		});
		
		ControlPanelController cpController = new ControlPanelController(db, cpView);
		PasswordLostController plController = new PasswordLostController(db, plView);
	}
}