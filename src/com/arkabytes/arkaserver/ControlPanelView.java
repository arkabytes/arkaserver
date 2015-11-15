package com.arkabytes.arkaserver;

import java.sql.SQLException;

import org.vaadin.activelink.ActiveLink;
import org.vaadin.activelink.ActiveLink.LinkActivatedEvent;
import org.vaadin.activelink.ActiveLink.LinkActivatedListener;

import com.arkabytes.arkaserver.database.Database;
import com.arkabytes.arkaserver.database.User;
import com.arkabytes.arkaserver.util.Constants;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

/**
 * Panel Control View. It shows a sidebar menu and the user can access to 
 * any information about server configuration
 * 
 * @author Santiago Faci
 * @version April 2015
 */
@SuppressWarnings("serial")
public class ControlPanelView extends AbstractView {

	private Tree tree;
	private HorizontalSplitPanel splitPanel;
	private ActiveLink logoutLink;
	
	public ControlPanelView(final Navigator navigator, Database db) {
		
		super(navigator, db);
		
		logoutLink = new ActiveLink("Sign out", new ExternalResource(""));
		logoutLink.addListener(new LinkActivatedListener() {
			public void linkActivated(LinkActivatedEvent event) {
				Notification.show("Bye " + ((String) getSession().getAttribute("username")) + "!", Notification.Type.HUMANIZED_MESSAGE);
				getSession().setAttribute("username", null);
				navigator.navigateTo(Constants.LOGIN_VIEW);
			}
		});
		addComponent(logoutLink);
		setComponentAlignment(logoutLink, Alignment.TOP_RIGHT);
		
		Panel panel = new Panel("Arkabytes Control Panel");
		panel.setContent(createHorizontalSplitPanel());
		addComponent(panel);
		
		tree.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				String item = tree.getItemCaption(event.getItemId());
				loadLayout(item);	
			}
		});
	}
	
	private HorizontalSplitPanel createHorizontalSplitPanel() {
		
		splitPanel = new HorizontalSplitPanel();
		splitPanel.setSplitPosition(20);
		splitPanel.setLocked(true);
		splitPanel.setImmediate(true);
		
		splitPanel.setFirstComponent(createTreeMenu());
		loadLayout("Home");
		tree.setValue("Home");
		
		return splitPanel;
	}
	
	private Component createTreeMenu() {
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		
		tree = new Tree("");
		final Object[][] treeItems = new Object[][]{
				new Object[]{"Home"},
				new Object[]{"Personal Information"},
				new Object[]{"Your services", "Mail", "FTP", "Web"}
		};
		
		for (int i = 0; i < treeItems.length; i++) {
			String item = (String) treeItems[i][0];
			tree.addItem(item);
			
			if (treeItems[i].length == 1) {
				tree.setChildrenAllowed(item, false);
			} else {
				for (int j = 1; j < treeItems[i].length; j++) {
					String child = (String) treeItems[i][j];
					tree.addItem(child);
					tree.setParent(child, item);
					tree.setChildrenAllowed(child, false);
				}
				tree.expandItemsRecursively(item);
			}
		}
		
		layout.addComponent(tree);
		
		return layout;
	}
	
	private void loadLayout(String item) {
		
		if (splitPanel.getSecondComponent() != null)
			splitPanel.removeComponent(splitPanel.getSecondComponent());
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		
		splitPanel.setSecondComponent(layout);
		
		switch (item) {
		case "Home":
			Label label = new Label("Wellcome to Arkabytes Control Panel. Here, you can check and change your personal and configuration data");
			layout.addComponent(label);
			break;
		case "Personal Information":
			User user = null;
			try {
				user = db.getUser(getSession().getAttribute("username").toString());
			} catch (Exception e) {
				Notification.show("Session error. Try signin again", Notification.Type.ERROR_MESSAGE);
				getSession().setAttribute("username", null);
				navigator.navigateTo(Constants.LOGIN_VIEW);
			}
			
			HorizontalLayout hLayout = new HorizontalLayout();
			hLayout.setCaption("<strong>Login Information</strong>");
			hLayout.setCaptionAsHtml(true);
			hLayout.setSpacing(true);
			final TextField tfUsername = new TextField("Username");
			tfUsername.setValue(user.getUsername());
			tfUsername.setReadOnly(true);
			hLayout.addComponent(tfUsername);
			final PasswordField tfPassword1 = new PasswordField("Password");
			final PasswordField tfPassword2 = new PasswordField("Repeat Password");
			hLayout.addComponent(tfPassword1);
			hLayout.addComponent(tfPassword2);
			
			final TextField tfName = new TextField("Name");
			tfName.setValue(user.getName());
			
			HorizontalLayout hLayout2 = new HorizontalLayout();
			hLayout2.setSpacing(true);
			final TextField tfPrimaryEmail = new TextField("Primary email");
			tfPrimaryEmail.setValue(user.getPrimaryEmail());
			tfPrimaryEmail.setWidth("250px");
			hLayout2.addComponent(tfPrimaryEmail);
			final TextField tfSecondaryEmail = new TextField("Secondary email");
			tfSecondaryEmail.setValue(user.getSecondaryEmail());
			tfSecondaryEmail.setWidth("250px");
			hLayout2.addComponent(tfSecondaryEmail);
			
			final TextField tfWeb = new TextField("Website");
			tfWeb.setValue(user.getWeb());
			tfWeb.setWidth("250px");
			final TextField tfPhone = new TextField("Phone");
			tfPhone.setValue(user.getPhone());
			Button btSave = new Button("Save changes");
			btSave.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
					User user = new User();
					user.setUsername(tfUsername.getValue());
					String password1 = tfPassword1.getValue();
					String password2 = tfPassword2.getValue();
					if ((password1.equals("") && (password2.equals("")))) {
						password1 = null;
						password2 = null;
					} else {
						if (!password1.equals(password2)) {
							Notification.show("Password doesn't match!", Notification.Type.ERROR_MESSAGE);
							tfPassword1.setValue("");
							tfPassword2.setValue("");
							return;
						}	
					}
					user.setPassword(password1);
					
					user.setName(tfName.getValue());
					user.setPrimaryEmail(tfPrimaryEmail.getValue());
					user.setSecondaryEmail(tfSecondaryEmail.getValue());
					user.setWeb(tfWeb.getValue());
					user.setPhone(tfPhone.getValue());
					
					try {
						db.updateUser(user);
						Notification.show("Data saved!", Notification.Type.TRAY_NOTIFICATION);
					} catch (SQLException sqle) {
						Notification.show("Error saving data! Try again " + sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
					} finally {
						tfPassword1.setValue("");
						tfPassword2.setValue("");
					}
				}
				
			});
			
			layout.addComponent(hLayout);
			layout.addComponent(tfName);
			layout.addComponent(tfPhone);
			layout.addComponent(hLayout2);
			layout.addComponent(tfWeb);
			layout.addComponent(btSave);
		
			break;
		case "Your services":
			
			break;
		case "Mail":
			layout.setCaption("Mail service configuration");
			
			final ListSelect listDomains = new ListSelect("Domains");
			listDomains.setRows(5);
			listDomains.addItems("arkabytes.es", "arkabytes.com", "videosdeinformatica.com", "codeandcoke.com");
			listDomains.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					String selectedDomain = (String) listDomains.getValue();
					if (selectedDomain != null)
						Notification.show(selectedDomain, Notification.Type.HUMANIZED_MESSAGE);
					
				}
			});
			layout.addComponent(listDomains);
			
			HorizontalLayout belowLayout = new HorizontalLayout();
			belowLayout.setSpacing(true);
			
			final ListSelect listAddresses = new ListSelect("Addresses");
			listAddresses.setRows(5);
			listAddresses.setNullSelectionAllowed(true);
			listAddresses.setNewItemsAllowed(true);
			listAddresses.setNewItemHandler(new NewItemHandler() {
				@Override
				public void addNewItem(String newItemCaption) {
			
					EmailValidator emailValidator = new EmailValidator("Invalid email address");
					
					if (listDomains.getValue() == null) {
						Notification.show("Please select a domain first", Notification.Type.WARNING_MESSAGE);
						return;
					}
					
					try {
						emailValidator.validate(newItemCaption);
					} catch (InvalidValueException ive) {
						Notification.show(emailValidator.getErrorMessage(), Notification.Type.ERROR_MESSAGE);
						return;
					}
					
					String domain = listDomains.getValue().toString();
					if (!newItemCaption.endsWith(domain)) {
						Notification.show("Mail address is not for the selected domain", Notification.Type.ERROR_MESSAGE);
						return;
					}
					
					listAddresses.addItem(newItemCaption);
				}
				
			});
			
			belowLayout.addComponent(listAddresses);
			layout.addComponent(belowLayout);
			
			VerticalLayout vBelowLayout = new VerticalLayout();
			vBelowLayout.setSpacing(true);
			Label mailLabel = new Label("To change your password, select domain and address, fill both fields and press 'Save changes' button");
			final PasswordField tfMailPassword1 = new PasswordField("Password");
			tfMailPassword1.setReadOnly(true);
			final PasswordField tfMailPassword2 = new PasswordField("Repeat password");
			tfMailPassword2.setReadOnly(true);
			vBelowLayout.addComponent(mailLabel);
			vBelowLayout.addComponent(tfMailPassword1);
			vBelowLayout.addComponent(tfMailPassword2);
			belowLayout.addComponent(vBelowLayout);
			
			listAddresses.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					if (listAddresses.getValue() != null) {
						tfMailPassword1.setReadOnly(false);
						tfMailPassword2.setReadOnly(false);
					} else {
						tfMailPassword1.setReadOnly(false);
						tfMailPassword2.setReadOnly(false);
					}
				}
			});
			
			Button btSaveMail = new Button("Save changes");
			btSaveMail.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
				}
			});
			
			
			
			layout.addComponent(btSaveMail);
			
			break;
		case "FTP":
			layout.setCaption("FTP service configuration");
			break;
		case "Web":
			layout.setCaption("Web service configuration");
			break;
		default:
			break;
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
		
	}
}
