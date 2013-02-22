package gov.ca.ceres.arcgis;

import gov.ca.ceres.arcgis.jackson.JsonObjectMapper;
import gov.ca.ceres.arcgis.pojo.ServiceDirectory;
import gov.ca.ceres.arcgis.pojo.ServiceDirectoryServices;
import gov.ca.ceres.arcgis.pojo.ServiceInfo;

public class DirectoryCrawler {
	private String host = "";
	private String path = "";
	private String serverId = "";
	private int threadId = -1;
	
	ServiceRequest request = null;
	
	public DirectoryCrawler(String url, String serverId, String path, int threadId) {
		this.host = url;
		this.path = path;
		this.serverId = serverId;
		this.threadId = threadId;
	}
	
	public void run() {
		
		request = new ServiceRequest(host+"/"+path+"?"+StaticValues.JSON_GET_VAR, threadId);
		request.run();
		
		ServiceDirectory sd = JsonObjectMapper.mapToServiceDirectory(request.getContent());
		if( sd == null ) return;
		
		for( String dir: sd.getFolders() ) {
			DirectoryCrawler dirCrawler = new DirectoryCrawler(host, serverId, dir, threadId);
			dirCrawler.run();
		}
		
		for( ServiceDirectoryServices service: sd.getServices() ) {
			Crawler.SERVICES.add(new ServiceInfo(service.getName(), service.getType(), host, serverId));
		}
	}
	
}
