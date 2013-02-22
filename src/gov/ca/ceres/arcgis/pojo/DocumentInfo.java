package gov.ca.ceres.arcgis.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentInfo {
	private String Title = "";
	private String Author = "";

	public void setTitle(String Title) {
		this.Title = Title;
	}
	public String getTitle() {
		return Title;
	}
	public void setAuthor(String Author) {
		this.Author = Author;
	}
	public String getAuthor() {
		return Author;
	}
	
}
