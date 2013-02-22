package gov.ca.ceres.arcgis.jackson;

import gov.ca.ceres.arcgis.pojo.MapService;
import gov.ca.ceres.arcgis.pojo.ServiceDirectory;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonObjectMapper {
	
	private static ObjectMapper mapper = new ObjectMapper();
	{
		//mapper.getJsonFactory().setParserFeature(Feature.ALL, state)
	}
	
	public static ServiceDirectory mapToServiceDirectory(String json) {
		
		try {
			return mapper.readValue(json, ServiceDirectory.class);
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		return null;
	}
	
	public static MapService mapToMapService(String json) {
		try {
			return mapper.readValue(json.replaceAll("NaN", "0.0"), MapService.class);
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		return null;
	}

}
