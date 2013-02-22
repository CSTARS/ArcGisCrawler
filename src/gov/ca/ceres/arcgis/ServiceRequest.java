package gov.ca.ceres.arcgis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class ServiceRequest {

	private String url = "";
	private int threadId = -1;
	
	private long headTime = 0;
	private int headStatus = -1;
	
	private long getTime = 0;
	private int getStatus = -1;
	
	private String content = "";
	
	public ServiceRequest(String url, int threadId) {
		this.url = url;
		this.threadId = threadId;
	}
	
	public void run() {
		getTest();
		sleep();
		headTest();
		sleep();
		System.out.println(threadId+" HEAD: "+headStatus+" "+headTime+"ms GET: "+getStatus+" "+getTime+"ms - "+url);
	}
	
	private void sleep() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void headTest() {
		try {
			URL url = new URL(this.url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.setConnectTimeout(45000);
			conn.setReadTimeout(45000);
        
			Date d = new Date();
			conn.connect();

			headTime = new Date().getTime() - d.getTime();
			headStatus = conn.getResponseCode();
	        //if( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
	        //        headTest = true;
	        //}
		} catch (IOException e) {}
	}
	
	public void getTest() {
		try {
			URL url = new URL(this.url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(45000);
			conn.setReadTimeout(45000);
        
			Date d = new Date();
			conn.connect();

			getStatus = conn.getResponseCode();
			
			BufferedReader rd  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        StringBuilder sb = new StringBuilder();
	        
	        String line;
	        while ((line = rd.readLine()) != null) {
	              sb.append(line + '\n');
	        }
	        
	        content = sb.toString();
			getTime = new Date().getTime() - d.getTime();
		} catch (IOException e) {}
	}
	
	public long getHeadTime() {
		return headTime;
	}
	
	public int getHeadStatus() {
		return headStatus;
	}
	
	public long getGETTime() {
		return getTime;
	}
	
	public int getGETStatus() {
		return getStatus;
	}
	
	public String getContent() {
		return content;
	}
	
}
