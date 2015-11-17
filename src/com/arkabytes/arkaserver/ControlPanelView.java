package com.arkabytes.arkaserver;

import java.sql.SQLException;
import java.util.List;

import org.vaadin.activelink.ActiveLink;
import org.vaadin.activelink.ActiveLink.LinkActivatedEvent;
import org.vaadin.activelink.ActiveLink.LinkActivatedListener;

import com.arkabytes.arkaserver.database.Database;
import com.arkabytes.arkaserver.database.EmailAccount;
import com.arkabytes.arkaserver.database.User;
import com.arkabytes.arkaserver.database.Domain;
import com.arkabytes.arkaserver.util.Constants;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
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
	ListSelect listDomains;
	ListSelect listAddresses;
	Button btSaveMail;
	PasswordField tfMailPassword1;
	PasswordField tfMailPassword2;
	TextField tfAccount;
	Button btAddAccount;
	Label mailLabel;
	
	public ControlPanelView(final Navigator navigator, Database db) {
		
		super(navigator, db);
		
		logoutLink = new ActiveLink("Sign out", new ExternalResource(""));
		logoutLink.addListener(new LinkActivatedListener() {
			public void linkActivated(LinkActivatedEvent event) {
				String username = getSession().getAttribute(User.class).getName();
				Notification.show("Bye " + username + "!", Notification.Type.HUMANIZED_MESSAGE);
				getSession().setAttribute(User.class, null);
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
				user = getSession().getAttribute(User.class);
			} catch (Exception e) {
				Notification.show("Session error. Try signin again", Notification.Type.ERROR_MESSAGE);
				getSession().setAttribute(User.class, null);
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
			listDomains = new ListSelect("Domains");
			listAddresses = new ListSelect("Addresses");
			btSaveMail = new Button("Save changes");
			tfAccount = new TextField();
			btAddAccount = new Button("+");
			mailLabel = new Label("To change your password, select domain and address, fill both fields and press 'Save changes' button");
			tfMailPassword1 = new PasswordField("Password");
			tfMailPassword2 = new PasswordField("Repeat password");
			
			layout.addComponent(listDomains);
				HorizontalLayout belowLayout = new HorizontalLayout();
				belowLayout.setSpacing(true);
				VerticalLayout vBelowLayoutLeft = new VerticalLayout();
				vBelowLayoutLeft.addComponent(listAddresses);
				vBelowLayoutLeft.setSpacing(true);
				belowLayout.addComponent(vBelowLayoutLeft);
			layout.addComponent(belowLayout);
				HorizontalLayout newEmailLayout = new HorizontalLayout();
				newEmailLayout.addComponent(tfAccount);
				newEmailLayout.addComponent(btAddAccount);
				vBelowLayoutLeft.addComponent(newEmailLayout);
			
			VerticalLayout vBelowLayoutRight = new VerticalLayout();
				vBelowLayoutRight.setSpacing(true);
				vBelowLayoutRight.addComponent(mailLabel);
				vBelowLayoutRight.addComponent(tfMailPassword1);
				vBelowLayoutRight.addComponent(tfMailPassword2);
				vBelowLayoutRight.addComponent(btSaveMail);
			belowLayout.addComponent(vBelowLayoutRight);
			
			listDomains.setRows(5);
			try {
				user = getSession().getAttribute(User.class);
				listDomains.addItems((Object[]) db.getDomains(user).toArray());
			} catch (SQLException sqle) {
				Notification.show("There was an error reading server domains" + sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
			listDomains.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					Domain selectedDomain = (Domain) listDomains.getValue();
					if (selectedDomain != null) {
						try {
							listAddresses.removeAllItems();
							List<EmailAccount> accounts = db.getEmailAccounts(selectedDomain.getName());
							for (EmailAccount account : accounts) {
								listAddresses.addItem(account);
							}
							
							Notification.show(selectedDomain.getName(), Notification.Type.HUMANIZED_MESSAGE);
						} catch (SQLException sqle) {
							Notification.show("Selected domains has not email accounts related to" + sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
						}
					}
					
				}
			});
			
			listAddresses.setRows(5);
			listAddresses.setWidth("300px");
			
			tfAccount.setInputPrompt("Enter email address");
			btAddAccount.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					EmailValidator emailValidator = new EmailValidator("Invalid email address");
					String newEmailAddress = tfAccount.getValue();
					
					if (listDomains.getValue() == null) {
						Notification.show("Please select a domain first", Notification.Type.WARNING_MESSAGE);
						return;
					}
					
					try {
						emailValidator.validate(newEmailAddress);
					} catch (InvalidValueException ive) {
						Notification.show(emailValidator.getErrorMessage(), Notification.Type.ERROR_MESSAGE);
						return;
					}
					
					String domain = listDomains.getValue().toString();
					if (!newEmailAddress.endsWith(domain)) {
						Notification.show("Mail address is not for the selected domain", Notification.Type.ERROR_MESSAGE);
						return;
					}
					
					if (tfMailPassword1.getValue().equals("") || tfMailPassword2.getValue().equals("")) {
						Notification.show("Password can't be empty", Notification.Type.ERROR_MESSAGE);
						return;
					}
					
					if (!tfMailPassword1.getValue().equals(tfMailPassword2.getValue())) {
						Notification.show("Password fields do not match", Notification.Type.ERROR_MESSAGE);
						return;
					}
					
					try {
						db.addEmailAccount((Domain) listDomains.getValue(), newEmailAddress, tfMailPassword2.getValue());
						listAddresses.addItem(newEmailAddress);
						tfAccount.setValue("");
					} catch (SQLException sqle) {
						listAddresses.removeItem(newEmailAddress);
						Notification.show("There was an error registering new domain. Try again " + sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			});
			
			tfMailPassword2.setTextChangeEventMode(TextChangeEventMode.EAGER);
			tfMailPassword1.setTextChangeEventMode(TextChangeEventMode.EAGER);
			btSaveMail.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
					if (tfMailPassword1.getValue().equals("") || tfMailPassword2.getValue().equals("")) {
						Notification.show("Password fields are empty!", Notification.Type.ERROR_MESSAGE);
						return;
					}
					
					if (!tfMailPassword1.getValue().equals(tfMailPassword2.getValue())) {
						Notification.show("Password do not match!", Notification.Type.ERROR_MESSAGE);
						return;
					}
					
					if (listAddresses.getValue() == null) {
						Notification.show("You have to select an email account to change the password", Notification.Type.ERROR_MESSAGE);
						return;
					}
					
					try {
						db.changeEmailAccountPassword(listAddresses.getValue().toString(), tfMailPassword1.getValue());
						Notification.show("Changes have been saved!", Notification.Type.HUMANIZED_MESSAGE);
						tfMailPassword1.setValue("");
						tfMailPassword2.setValue("");
					} catch (SQLException sqle) {
						Notification.show("There was an error saving changes. Try again" + sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
					}
				}
			});
			
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
