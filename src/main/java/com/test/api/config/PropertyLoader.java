package com.test.api.config;

import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {
	private Properties properties;
	
	public PropertyLoader() {
		this.properties = loadProperties();
	}
	
	public String getProperty(String name) {
		return this.properties.getProperty(name);
	}
	
	private Properties loadProperties() {
	    Properties props = new Properties();
	    
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return props;
	    	
	}
}
