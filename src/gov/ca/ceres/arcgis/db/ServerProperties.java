package gov.ca.ceres.arcgis.db;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ServerProperties {
	
	public Properties configFile;
	
	public String DB_URL;
	public String DB_USERNAME;
	public String DB_PASSWORD;


	private boolean error = false;
	
	public ServerProperties(String propFile){
		configFile = new Properties();
		try {
		     configFile.load(new FileInputStream(new File(propFile)));
		} catch (Exception e) {
			System.out.println("Prop file error: "+e.getMessage());
		     error = true;
		}
		if( !error ){
			// DB
			DB_URL = configFile.getProperty("DB_URL");
			DB_USERNAME = configFile.getProperty("DB_USERNAME");
			DB_PASSWORD = configFile.getProperty("DB_PASSWORD");
		}
	}
	
	public boolean hasError(){
		return error;
	}

}
