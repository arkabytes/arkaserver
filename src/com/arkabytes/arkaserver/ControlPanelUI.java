package com.arkabytes.arkaserver;

import javax.servlet.annotation.WebServlet;

import com.arkabytes.arkaserver.util.Util;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ControlPanelUI extends UI {
	
	@WebServlet(value = "/controlpanel/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ControlPanelUI.class, widgetset = "com.arkabytes.arkaserver.widgetset.ArkaserverWidgetset")
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
	}
}
