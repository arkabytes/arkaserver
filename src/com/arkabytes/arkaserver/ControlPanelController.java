package com.arkabytes.arkaserver;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ControlPanelController implements ClickListener, ValueChangeListener {
	
	private ControlPanelView view;
	private ControlPanelModel model;
	
	public ControlPanelController(ControlPanelModel model, ControlPanelView view) {
		this.model = model;
		this.view = view;
		
		addClickListeners(this);
		addValueChangeListeners(this);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		
		if (event.getButton() == view.btAddAccount) {
			
		}
		else if (event.getButton() == view.btSaveMail) {
			
		}
		
	}
	
	@Override
	public void valueChange(ValueChangeEvent event) {
		
		
	}
	
	private void addClickListeners(ClickListener listener) {
		view.btAddAccount.addClickListener(this);
		view.btSaveMail.addClickListener(this);
	}
	
	private void addValueChangeListeners(ValueChangeListener listener) {
		view.listDomains.addValueChangeListener(this);
	}

	
}
