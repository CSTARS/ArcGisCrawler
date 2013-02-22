package gov.ca.ceres.arcgis.db;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

import java.util.Properties;

public class DatabaseConnection {

	private boolean debug = true;
	
	private Connection con = null;
	private PreparedStatement ps = null;
	private ResultSet currentResult = null;
	
	private ServerProperties properties;
	
	public DatabaseConnection(String propFile){
		properties = new ServerProperties(propFile);
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException cnfe) {
			if( debug ) System.out.println("Error in DataBaseconnection(): "+cnfe.getMessage());
		}
	}
	
	public boolean connect(){
		Properties props = new Properties();
		props.setProperty("user",properties.DB_USERNAME);
		props.setProperty("password",properties.DB_PASSWORD);
		try {
			con = DriverManager.getConnection(properties.DB_URL, props);
		} catch( SQLException e ){
			if( debug ) System.out.println("Error in DataBaseconnection.connect(): "+e.getMessage()
					+ "\n --Username: "+properties.DB_USERNAME +
					"\n --Url: "+properties.DB_URL);
			return false;
		}
		return true;
	}
	
	public boolean disconnect(){
		try {
			con.close();
		} catch( SQLException e ){
			if( debug ) System.out.println("Error in DataBaseConnection.disconnect(): "+e.getMessage());
			return false;
		}
		return true;
	}
	
	public ResultSet getQueryResult(){
		return currentResult;
	}
	
	public boolean query(String query){
		// TODO: all calls here should call a closeQuery() which closes ps and currentResult
		try {
			ps = con.prepareStatement(query);
			currentResult = ps.executeQuery();
			return true;
		} catch( SQLException e ){
			if( debug ) System.out.println("Error in DataBaseConnection.query(): "+e.getMessage());
		}
		return false;
	}
	
	public void closeQuery(){
		try {
			currentResult.close();
		} catch (SQLException e){
			if( debug ) System.out.println("Error in DataBaseConnection.closeQuery(): "+e.getMessage());
		}
		try {
			ps.close();
		} catch (SQLException e){
			if( debug ) System.out.println("Error in DataBaseConnection.closeQuery(): "+e.getMessage());
		}
	}
	
	public PreparedStatement getPreparedStatement() {
		return ps;
	}
	
	// general prepare stmt
	public void prepare(String stmt){
		try {
			ps = con.prepareStatement(stmt);
		} catch (SQLException e){
			if( debug ) {
				System.out.println("Error in DBComm.prepare(): "+e.getMessage());
				System.out.println(" |-->"+stmt);
			}
		}
	}
	
	// general close
	public void close(){
		try {
			ps.close();
		} catch (SQLException e){
			if( debug ) System.out.println("Error in DBComm.close(): "+e.getMessage());
		}
	}
	

	
}
