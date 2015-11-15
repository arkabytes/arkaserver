package com.arkabytes.arkaserver;

import com.arkabytes.arkaserver.database.Database;
import com.arkabytes.arkaserver.util.Util;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

/**
 * An abstract view to reuse code
 * 
 * @author Santiago Faci
 * @version April 2015
 */
@SuppressWarnings("serial")
public abstract class AbstractView extends VerticalLayout implements View {

	protected Database db;
	protected Navigator navigator;
	
	public AbstractView(final Navigator navigator, Database db) {
		
		this.navigator = navigator;
		this.db = db;
		
		setMargin(true);
		setSpacing(true);
		
		Image logo = Util.getImage("logo_arkabytes.png");
		addComponent(logo);

		setComponentAlignment(logo, Alignment.TOP_CENTER);
	}
}

