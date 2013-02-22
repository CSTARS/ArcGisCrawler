package gov.ca.ceres.arcgis.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapService {
	private String currentVersion = "";
	private String serviceDescription = "";
	private String mapName = "";
	private String description = "";
	private String copyrightText = "";
	private boolean singleFusedMapCache = false;
	private Extent initialExtent = null;
	private Extent fullExtent = null;
	private String units = "";
	private String supportedImageFormatTypes = "";
	private String capabilities = "";
	private DocumentInfo documentInfo = null;
	
	public String getCurrentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	public String getServiceDescription() {
		return serviceDescription;
	}
	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}
	public String getMapName() {
		return mapName;
	}
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCopyrightText() {
		return copyrightText;
	}
	public void setCopyrightText(String copyrightText) {
		this.copyrightText = copyrightText;
	}
	public boolean isSingleFusedMapCache() {
		return singleFusedMapCache;
	}
	public void setSingleFusedMapCache(boolean singleFusedMapCache) {
		this.singleFusedMapCache = singleFusedMapCache;
	}
	public Extent getInitialExtent() {
		return initialExtent;
	}
	public void setInitialExtent(Extent initialExtent) {
		this.initialExtent = initialExtent;
	}
	public Extent getFullExtent() {
		return fullExtent;
	}
	public void setFullExtent(Extent fullExtent) {
		this.fullExtent = fullExtent;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getSupportedImageFormatTypes() {
		return supportedImageFormatTypes;
	}
	public void setSupportedImageFormatTypes(String supportedImageFormatTypes) {
		this.supportedImageFormatTypes = supportedImageFormatTypes;
	}
	public String getCapabilities() {
		return capabilities;
	}
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	public void setDocumentInfo(DocumentInfo documentInfo) {
		this.documentInfo = documentInfo;
	}
	public DocumentInfo getDocumentInfo() {
		return documentInfo;
	}
}
