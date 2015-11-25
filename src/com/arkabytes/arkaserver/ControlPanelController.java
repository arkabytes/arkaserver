package com.arkabytes.arkaserver;

import java.sql.SQLException;
import java.util.List;

import org.vaadin.activelink.ActiveLink.LinkActivatedEvent;
import org.vaadin.activelink.ActiveLink.LinkActivatedListener;

import com.arkabytes.arkaserver.database.Database;
import com.arkabytes.arkaserver.database.Domain;
import com.arkabytes.arkaserver.database.EmailAccount;
import com.arkabytes.arkaserver.database.User;
import com.arkabytes.arkaserver.util.Constants;
import static com.arkabytes.arkaserver.util.Constants.HOME_AREA;
import static com.arkabytes.arkaserver.util.Constants.PROFILE_AREA;
import static com.arkabytes.arkaserver.util.Constants.MAIL_AREA;
import static com.arkabytes.arkaserver.util.Constants.FTP_AREA;
import static com.arkabytes.arkaserver.util.Constants.WEB_AREA;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class ControlPanelController implements ClickListener, ValueChangeListener, LinkActivatedListener, ItemClickListener {
	
	private ControlPanelView view;
	private Database model;
	
	public ControlPanelController(Database model, ControlPanelView view) {
		this.model = model;
		this.view = view;
		
		addItemClickListeners(this);
		addClickListeners(this);
		addValueChangeListeners(this);
		addLinkListeners(this);
	}
	
	/**
	 * Ends user session
	 */
	private void logout() {
		
		String username = view.getUI().getSession().getAttribute(User.class).getName();
		Notification.show("Bye " + username + "!", Notification.Type.HUMANIZED_MESSAGE);
		view.getUI().getSession().setAttribute(User.class, null);
		view.navigator.navigateTo(Constants.LOGIN_VIEW);
	}
	
	/**
	 * Register a new email account
	 */
	private void addEmailAccount() {
		
		EmailValidator emailValidator = new EmailValidator("Invalid email address");
		String newEmailAddress = view.tfEmailAccount.getValue();
		
		if (view.listEmailDomains.getValue() == null) {
			Notification.show("Please select a domain first", Notification.Type.WARNING_MESSAGE);
			return;
		}
		
		try {
			emailValidator.validate(newEmailAddress);
		} catch (InvalidValueException ive) {
			Notification.show(emailValidator.getErrorMessage(), Notification.Type.ERROR_MESSAGE);
			return;
		}
		
		String domain = view.listEmailDomains.getValue().toString();
		if (!newEmailAddress.endsWith(domain)) {
			Notification.show("Mail address is not for the selected domain", Notification.Type.ERROR_MESSAGE);
			return;
		}
		
		if (view.tfMailPassword1.getValue().equals("") || view.tfMailPassword2.getValue().equals("")) {
			Notification.show("Password can't be empty", Notification.Type.ERROR_MESSAGE);
			return;
		}
		
		if (!view.tfMailPassword1.getValue().equals(view.tfMailPassword2.getValue())) {
			Notification.show("Password fields do not match", Notification.Type.ERROR_MESSAGE);
			return;
		}
		
		try {
			model.addEmailAccount((Domain) view.listEmailDomains.getValue(), newEmailAddress, view.tfMailPassword2.getValue());
			view.listAddresses.addItem(newEmailAddress);
			view.tfEmailAccount.setValue("");
		} catch (SQLException sqle) {
			view.listAddresses.removeItem(newEmailAddress);
			Notification.show("There was an error registering new domain. Try again " + sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Save email accounts data
	 */
	private void saveMail() {
		
		if (view.tfMailPassword1.getValue().equals("") || view.tfMailPassword2.getValue().equals("")) {
			Notification.show("Password fields are empty!", Notification.Type.ERROR_MESSAGE);
			return;
		}
		
		if (!view.tfMailPassword1.getValue().equals(view.tfMailPassword2.getValue())) {
			Notification.show("Password do not match!", Notification.Type.ERROR_MESSAGE);
			return;
		}
		
		if (view.listAddresses.getValue() == null) {
			Notification.show("You have to select an email account to change the password", Notification.Type.ERROR_MESSAGE);
			return;
		}
		
		try {
			model.changeEmailAccountPassword(view.listAddresses.getValue().toString(), view.tfMailPassword1.getValue());
			Notification.show("Changes have been saved!", Notification.Type.HUMANIZED_MESSAGE);
			view.tfMailPassword1.setValue("");
			view.tfMailPassword2.setValue("");
		} catch (SQLException sqle) {
			Notification.show("There was an error saving changes. Try again" + sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Save user profile data
	 */
	private void saveProfile() {
		
		User user = new User();
		user.setUsername(view.tfUsername.getValue());
		String password1 = view.tfPassword1.getValue();
		String password2 = view.tfPassword2.getValue();
		if ((password1.equals("") && (password2.equals("")))) {
			password1 = null;
			password2 = null;
		} else {
			if (!password1.equals(password2)) {
				Notification.show("Password doesn't match!", Notification.Type.ERROR_MESSAGE);
				view.tfPassword1.setValue("");
				view.tfPassword2.setValue("");
				return;
			}	
		}
		user.setPassword(password1);
		
		user.setName(view.tfName.getValue());
		user.setPrimaryEmail(view.tfPrimaryEmail.getValue());
		user.setSecondaryEmail(view.tfSecondaryEmail.getValue());
		user.setWeb(view.tfWeb.getValue());
		user.setPhone(view.tfPhone.getValue());
		
		try {
			model.updateUser(user);
			Notification.show("Data saved!", Notification.Type.TRAY_NOTIFICATION);
		} catch (SQLException sqle) {
			Notification.show("Error saving data! Try again " + sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
		} finally {
			view.tfPassword1.setValue("");
			view.tfPassword2.setValue("");
		}
	}
	
	/**
	 * Load the right panel depending the tree item that user click on
	 * @param item
	 */
	private void loadPanel(String item) {
		view.loadLayout(item);
		
		User user = null;
		switch (item) {
			case HOME_AREA:
				break;
			case PROFILE_AREA:
					user = view.getUI().getSession().getAttribute(User.class);
					if (user == null) {
						Notification.show("Session error. Try signin again", Notification.Type.ERROR_MESSAGE);
						view.getUI().getSession().setAttribute(User.class, null);
						view.navigator.navigateTo(Constants.LOGIN_VIEW);
					}
					view.tfName.setValue(user.getName());
					view.tfUsername.setValue(user.getUsername());
					view.tfUsername.setReadOnly(true);
					view.tfPrimaryEmail.setValue(user.getPrimaryEmail());
					view.tfSecondaryEmail.setValue(user.getSecondaryEmail());
					view.tfWeb.setValue(user.getWeb());
					view.tfPhone.setValue(user.getPhone());
				break;
			case MAIL_AREA:
				try {
					user = view.getUI().getSession().getAttribute(User.class);
					view.listEmailDomains.removeAllItems();
					view.listEmailDomains.addItems((Object[]) model.getDomains(user).toArray());
				} catch (SQLException sqle) {
					Notification.show("There was an error reading server domains" + sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
				}
				break;
			case FTP_AREA:
				break;
			case WEB_AREA:
				break;
			default:
				break;
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {
		
		if (event.getButton() == view.btAddEmailAccount) {
			addEmailAccount();
		}
		else if (event.getButton() == view.btSaveMail) {
			saveMail();
		}
		else if (event.getButton() == view.btSaveProfile) {
			saveProfile();
		}
	}
	
	@Override
	public void valueChange(ValueChangeEvent event) {
		
		if (event.getProperty() == view.listEmailDomains) {
			Domain selectedDomain = (Domain) view.listEmailDomains.getValue();
			if (selectedDomain != null) {
				try {
					view.listAddresses.removeAllItems();
					List<EmailAccount> accounts = model.getEmailAccounts(selectedDomain.getName());
					for (EmailAccount account : accounts) {
						view.listAddresses.addItem(account);
					}
					
					Notification.show(selectedDomain.getName(), Notification.Type.HUMANIZED_MESSAGE);
				} catch (SQLException sqle) {
					Notification.show("Selected domains has not email accounts related to" + sqle.getMessage(), Notification.Type.ERROR_MESSAGE);
				}
			}
		}
	}
	

	@Override
	public void linkActivated(LinkActivatedEvent event) {
		
		if (event.getActiveLink() == view.lnLogout) {
			logout();
		}
	}
	

	@Override
	public void itemClick(ItemClickEvent event) {
		if (event.getSource() == view.tree) {
			String item = view.tree.getItemCaption(event.getItemId());
			loadPanel(item);
		}
	}
	
	private void addItemClickListeners(ItemClickListener listener) {
		view.tree.addItemClickListener(listener);
	}
	
	private void addClickListeners(ClickListener listener) {
		view.btAddEmailAccount.addClickListener(this);
		view.btSaveMail.addClickListener(this);
		view.btSaveProfile.addClickListener(this);
	}
	
	private void addLinkListeners(LinkActivatedListener listener) {
		view.lnLogout.addListener(listener);
	}
	
	private void addValueChangeListeners(ValueChangeListener listener) {
		view.listEmailDomains.addValueChangeListener(this);
	}
}
