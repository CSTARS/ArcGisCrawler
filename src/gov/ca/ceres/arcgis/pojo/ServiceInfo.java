package gov.ca.ceres.arcgis.pojo;

public class ServiceInfo {

	private String host = "";
	private String serverId = "";
	private int serviceId = -1;
	private String title = "";
	private String type = "";
	private String description = "";
	private String path = "";
	
	public ServiceInfo(String path, String type, String host, String serverId) {
		this.setHost(host);
		this.setServerId(serverId);
		this.path = path;
		this.type = type;
	}
	
	public void setServiceId(int id) {
		serviceId = id;
	}
	
	public int getServiceId() {
		return serviceId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getServerId() {
		return serverId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
