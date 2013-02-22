package gov.ca.ceres.arcgis.pojo;

/**
 * This object represents an arcgis service directory
 * 
 * @author jrmerz
 */
public class ServiceDirectory {

	private float currentVersion = 0;
	private String[] folders = null;
	private ServiceDirectoryServices[] services = null;
	
	public float getCurrentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(float currentVersion) {
		this.currentVersion = currentVersion;
	}
	public String[] getFolders() {
		return folders;
	}
	public void setFolders(String[] folders) {
		this.folders = folders;
	}
	public ServiceDirectoryServices[] getServices() {
		return services;
	}
	public void setServices(ServiceDirectoryServices[] services) {
		this.services = services;
	}
	
}
