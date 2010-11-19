package br.usp.ime.cogroo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public final class BuildUtil {
	
	private static final Logger LOG = Logger
		.getLogger(BuildUtil.class);
	
	private static final ResourceBundle PROPERTIES = ResourceBundle.getBundle("build");
	
	public static final String APP_NAME = "CoGrOO Comunidade";
	
	public static final String POM_VERSION = getString("POM_VERSION");
	
	public static final Date BUILD_TIME = getDate(getString("BUILD_TIME"));
	
	public static final String ANALYTICS_USR = getString("ANALYTICS_USR");
	public static final String ANALYTICS_PWD = getString("ANALYTICS_PWD");
	
	private static String getString(String key) {
		return PROPERTIES.getString(key);
	}
	
	
	private static Date getDate(String date) {
		try {
			return new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").parse( date );
		} catch (ParseException e) {
			LOG.error("Error parsing date: " + date);
			return null;
		}
	}

}
