package com.arkabytes.arkaserver.util;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Image;

/**
 * Helper class
 * 
 * @author Santiago Faci
 * @version April 2015
 */
public class Util {
	
	public static Image getImage(String name) {
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource fileResource = new FileResource(new File(basepath + "/WEB-INF/img/" + name));
		Image image = new Image(null, fileResource);
		
		return image;	
	}
	
	public static String generateLinkCode() {
		
		UUID uuid = UUID.randomUUID();
		return String.valueOf(uuid);	
	}
}
