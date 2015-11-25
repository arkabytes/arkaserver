package com.arkabytes.arkaserver;

import org.vaadin.activelink.ActiveLink;

import com.arkabytes.arkaserver.database.Database;
import static com.arkabytes.arkaserver.util.Constants.HOME_AREA;
import static com.arkabytes.arkaserver.util.Constants.PROFILE_AREA;
import static com.arkabytes.arkaserver.util.Constants.MAIL_AREA;
import static com.arkabytes.arkaserver.util.Constants.FTP_AREA;
import static com.arkabytes.arkaserver.util.Constants.WEB_AREA;
import static com.arkabytes.arkaserver.util.Constants.SERVICES;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
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

	Tree tree;
	HorizontalSplitPanel splitPanel;
	ActiveLink lnLogout;
	ListSelect listEmailDomains, listAddresses, listFtpDomains, listFtpAccounts;
	PasswordField tfPassword1, tfPassword2, tfMailPassword1, tfMailPassword2, tfFtpPassword1, tfFtpPassword2;
	TextField tfUsername, tfName, tfPhone, tfEmailAccount, tfWeb, tfPrimaryEmail, tfSecondaryEmail, tfFtpAccount;
	Button btAddEmailAccount, btSaveProfile, btSaveMail, btSaveFtp, btAddFtpAccount;
	Label lbMail, lbTitle, lbFtp;
	Panel panel;
	
	public ControlPanelView(final Navigator navigator, Database db) {
		
		super(navigator, db);
		
		initViews();
		
		addComponent(lnLogout);
		setComponentAlignment(lnLogout, Alignment.TOP_RIGHT);
		addComponent(panel);
	}
	
	/**
	 * Init every UI components
	 */
	private void initViews() {
		
		tfUsername = new TextField("Username");
		tfName = new TextField("Name");
		tfPrimaryEmail = new TextField("Primary email");
		tfSecondaryEmail = new TextField("Secondary email");
		tfPhone = new TextField("Phone");
		tfWeb = new TextField("Website");
		tfEmailAccount = new TextField();
		
		tfPassword1 = new PasswordField("Password");
		tfPassword2 = new PasswordField("Repeat Password");
		tfMailPassword1 = new PasswordField("Password");
		tfMailPassword2 = new PasswordField("Repeat password");
		tfFtpPassword1 = new PasswordField("Password");
		tfFtpPassword2 = new PasswordField("Repeat password");
		
		btSaveProfile = new Button("Save changes");
		btSaveMail = new Button("Save changes");
		btAddEmailAccount = new Button("+");
		btSaveFtp = new Button("Save changes");
		btAddFtpAccount = new Button("+");
		
		listEmailDomains = new ListSelect("Domains");
		listAddresses = new ListSelect("Addresses");
		listFtpDomains = new ListSelect("Domains");
		listFtpAccounts = new ListSelect("Accounts");
		
		lbMail = new Label("To change your password, select a domain and address, fill both fields and press 'Save changes' button");
		lbFtp = new Label("To change your password, select a domain and FTP account, fill both fields and press 'Save changes' button");
		lbTitle = new Label("Wellcome to Arkabytes Control Panel. Here, you can check and change your personal and configuration data");
		
		panel = new Panel("Arkabytes Control Panel");
		panel.setContent(createHorizontalSplitPanel());
		
		lnLogout = new ActiveLink("Sign out", new ExternalResource(""));
	}
	
	/**
	 * Create the main split panel
	 * @return
	 */
	private HorizontalSplitPanel createHorizontalSplitPanel() {
		
		splitPanel = new HorizontalSplitPanel();
		splitPanel.setSplitPosition(20);
		splitPanel.setLocked(true);
		splitPanel.setImmediate(true);
		
		splitPanel.setFirstComponent(createTreeMenu());
		loadLayout(HOME_AREA);
		tree.setValue(HOME_AREA);
		
		return splitPanel;
	}
	
	/**
	 * Create the tree menu located in the left sidebar
	 * @return
	 */
	private Component createTreeMenu() {
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		
		tree = new Tree("");
		final Object[][] treeItems = new Object[][]{
				new Object[]{HOME_AREA},
				new Object[]{PROFILE_AREA},
				new Object[]{SERVICES, MAIL_AREA, FTP_AREA, WEB_AREA}
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
	
	/**
	 * Load the current layout depending the item tree where user click on
	 * @param item
	 */
	void loadLayout(String item) {
		
		// Remove the current view at right of the splitPanel, to load the new view that the user has selected
		if (splitPanel.getSecondComponent() != null)
			splitPanel.removeComponent(splitPanel.getSecondComponent());
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		
		splitPanel.setSecondComponent(layout);
		
		switch (item) {
		case HOME_AREA:
			layout.addComponent(lbTitle);
			break;
		case PROFILE_AREA:
			HorizontalLayout hLayout = new HorizontalLayout();
			hLayout.setCaption("<strong>Login Information</strong>");
			hLayout.setCaptionAsHtml(true);
			hLayout.setSpacing(true);
			
			hLayout.addComponent(tfUsername);
			hLayout.addComponent(tfPassword1);
			hLayout.addComponent(tfPassword2);
			
			HorizontalLayout hLayout2 = new HorizontalLayout();
			hLayout2.setSpacing(true);

			tfPrimaryEmail.setWidth("250px");
			hLayout2.addComponent(tfPrimaryEmail);
			tfSecondaryEmail.setWidth("250px");
			hLayout2.addComponent(tfSecondaryEmail);
			tfWeb.setWidth("250px");
			
			layout.addComponent(hLayout);
			layout.addComponent(tfName);
			layout.addComponent(tfPhone);
			layout.addComponent(hLayout2);
			layout.addComponent(tfWeb);
			layout.addComponent(btSaveProfile);
			break;
		case "Your services":
			break;
		case MAIL_AREA:
			layout.setCaption("Mail service configuration");
			
			layout.addComponent(listEmailDomains);
				HorizontalLayout belowLayout = new HorizontalLayout();
				belowLayout.setSpacing(true);
				VerticalLayout vBelowLayoutLeft = new VerticalLayout();
				vBelowLayoutLeft.addComponent(listAddresses);
				vBelowLayoutLeft.setSpacing(true);
				belowLayout.addComponent(vBelowLayoutLeft);
			layout.addComponent(belowLayout);
				HorizontalLayout newEmailLayout = new HorizontalLayout();
				newEmailLayout.addComponent(tfEmailAccount);
				newEmailLayout.addComponent(btAddEmailAccount);
				vBelowLayoutLeft.addComponent(newEmailLayout);
			
			VerticalLayout vBelowLayoutRight = new VerticalLayout();
				vBelowLayoutRight.setSpacing(true);
				vBelowLayoutRight.addComponent(lbMail);
				vBelowLayoutRight.addComponent(tfMailPassword1);
				vBelowLayoutRight.addComponent(tfMailPassword2);
				vBelowLayoutRight.addComponent(btSaveMail);
			belowLayout.addComponent(vBelowLayoutRight);
			
			listEmailDomains.setRows(5);
			listAddresses.setRows(5);
			listAddresses.setWidth("300px");
			
			tfEmailAccount.setInputPrompt("Enter email address");
			tfMailPassword2.setTextChangeEventMode(TextChangeEventMode.EAGER);
			tfMailPassword1.setTextChangeEventMode(TextChangeEventMode.EAGER);
			break;
		case FTP_AREA:
			layout.setCaption("FTP service configuration");
			
			layout.addComponent(listFtpDomains);
				HorizontalLayout belowLayoutFtp = new HorizontalLayout();
				belowLayoutFtp.setSpacing(true);
				VerticalLayout vBelowLayoutLeftFtp = new VerticalLayout();
				vBelowLayoutLeftFtp.addComponent(listFtpAccounts);
				vBelowLayoutLeftFtp.setSpacing(true);
				belowLayoutFtp.addComponent(vBelowLayoutLeftFtp);
			layout.addComponent(belowLayoutFtp);
				HorizontalLayout newFtpAccountLayout = new HorizontalLayout();
				newFtpAccountLayout.addComponent(tfFtpAccount);
				newFtpAccountLayout.addComponent(btAddFtpAccount);
				vBelowLayoutLeftFtp.addComponent(newFtpAccountLayout);
			
			VerticalLayout vBelowLayoutRightFtp = new VerticalLayout();
				vBelowLayoutRightFtp.setSpacing(true);
				vBelowLayoutRightFtp.addComponent(lbFtp);
				vBelowLayoutRightFtp.addComponent(tfFtpPassword1);
				vBelowLayoutRightFtp.addComponent(tfFtpPassword2);
				vBelowLayoutRightFtp.addComponent(btSaveFtp);
			belowLayoutFtp.addComponent(vBelowLayoutRightFtp);
			
			listFtpDomains.setRows(5);
			listFtpAccounts.setRows(5);
			listFtpAccounts.setWidth("300px");
			
			tfFtpAccount.setInputPrompt("Enter FTP username");
			tfFtpPassword1.setTextChangeEventMode(TextChangeEventMode.EAGER);
			tfFtpPassword2.setTextChangeEventMode(TextChangeEventMode.EAGER);
			break;
		case WEB_AREA:
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
