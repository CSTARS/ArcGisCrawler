package gov.ca.ceres.arcgis;

import gov.ca.ceres.arcgis.db.Database;
import gov.ca.ceres.arcgis.jackson.JsonObjectMapper;
import gov.ca.ceres.arcgis.pojo.MapService;
import gov.ca.ceres.arcgis.pojo.ServiceInfo;

public class ServiceCrawler extends Thread {
	
	private ServiceInfo service = null;
	private String host = "";
	private String serverId = "";
	private CompleteHandler handler = null;
	private int threadId = -1;
	
	public ServiceCrawler(ServiceInfo service, String host, String serverId, CompleteHandler handler, int threadId) {
		this.host = host;
		this.service = service;
		this.serverId = serverId;
		this.handler = handler;
		this.threadId = threadId;
	}
	
	@Override
	public void run() {
		
		ServiceRequest request = new ServiceRequest(host+"/"+service.getPath()+"/"+service.getType()+"?"+StaticValues.JSON_GET_VAR, threadId);
		request.run();
			
		int serviceId = -1;
		if( service.getType().contentEquals("MapServer") ) {
			MapService mapService = JsonObjectMapper.mapToMapService(request.getContent());
			if( mapService != null ) {
				
				
				String mapName = mapService.getMapName();
				String docInfoName = "";
				if( mapService.getDocumentInfo() != null ) {
					docInfoName = mapService.getDocumentInfo().getTitle();
					if( docInfoName == null ) docInfoName = "";
					
					// hack!!! Jackson doesn't seem to be parsing this 'Title' :( but so important
					if( docInfoName.isEmpty() ) {
						docInfoName = request.getContent().replaceAll(".*\"Title\":\"", "").replaceAll("\".*", "").replaceAll("\n", "");
					}
				}
				
				if( mapName.isEmpty() || mapName.contentEquals("Layers") || mapName.length() < docInfoName.length() ) {
					mapName = docInfoName;
				}
				if( mapName.isEmpty() ) {
					mapName = service.getPath().replaceAll(".*/", "").replaceAll("_", " ");
				}
				service.setTitle(mapName);
				
				String description = mapService.getDescription();
				if( description.isEmpty() ) description = mapService.getServiceDescription();
				service.setDescription(description);
				
				serviceId = Database.INSTANCE.updateService(Integer.parseInt(serverId), service.getPath(), 
						service.getType(), mapName, description);
				
			}
		} else {
			serviceId = Database.INSTANCE.updateService(Integer.parseInt(serverId), service.getPath(), 
					service.getType(), "", "");
		}
		service.setServiceId(serviceId);
			
		boolean status = false;
		if( request.getGETStatus() == 200 && request.getHeadStatus() == 200 ) {
			status = true;
		}
		
		Database.INSTANCE.addTestResult(serviceId, status);
		
		Crawler.SERVICE_COUNT++;
		
		handler.onComplete(threadId);
	}

}
