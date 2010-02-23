package org.ttdc.util;

import java.io.IOException;
import java.util.Properties;

public class AppConfig {
	private static AppConfig me = new AppConfig();
	
	public static Properties getProperties(String filename) throws IOException{
		java.util.Properties props = new java.util.Properties();
		java.net.URL url = me.getClass().getResource(filename);
	    props.load(url.openStream());
	    return props;
	}
}

