package org.ttdc.util;

 
import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {
	private static ApplicationProperties me = new ApplicationProperties();

	public static Properties getProperties(String filename) throws IOException{
		java.util.Properties props = new java.util.Properties();
		java.net.URL url = me.getClass().getResource(filename);
	    props.load(url.openStream());
	    return props;
	}
	
	public static Properties getAppProperties() throws IOException{
		return getProperties("application.properties");
	}
}

