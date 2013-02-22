package gov.ca.ceres.arcgis.db;

import gov.ca.ceres.arcgis.pojo.ServiceInfo;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.LinkedList;

public class Database {
	
	public static Database INSTANCE = new Database();
	public static String SCHEMA = "esri";
	
	private DatabaseConnection conn = new DatabaseConnection("/etc/arcgiscrawler/setup.properties");
	
	public void connect() {
		conn.connect();
	}
	
	public void disconnect() {
		conn.disconnect();
	}
	
	public LinkedList<String[]> getServers() {
		LinkedList<String[]> servers = new LinkedList<String[]>();
		
		conn.query("SELECT * from "+SCHEMA+".servers");
		ResultSet rs = conn.getQueryResult();
		
		try {
			while( rs.next() ) {
				servers.add(new String[] {
					rs.getString("url"),
					String.valueOf(rs.getInt("server_id"))
				});
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		conn.closeQuery();

		return servers;
	}
	
	/**
	 * This will add or update the service
	 * 
	 * @param id - server id
	 * @param path - service path (from root)
	 * @param type - service type
	 * @param title - service title
	 * @param description - description of service
	 * 
	 * return int
	 */
	public synchronized int updateService(int id, String path, String type, String title, String description) {
		int serviceId = -1;
		
		boolean found = false;
		conn.query("select * from "+SCHEMA+".services where server_id = '"+id+"' and path = '"+path+"' and servicetype = '"+type+"'");
		ResultSet rs = conn.getQueryResult();
		try {
			if( rs.next() ) {
				found = true;
				serviceId = rs.getInt("service_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if( found ) {

			conn.prepare("UPDATE "+SCHEMA+".services SET serviceType=?, title=?, description=? where service_id = ?");
			PreparedStatement stmt = conn.getPreparedStatement();
			try {
				stmt.setString(1, type);
				stmt.setString(2, title);
				stmt.setString(3, description);
				stmt.setInt(4, serviceId);
				stmt.execute();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else {
			
			conn.prepare("INSERT INTO "+SCHEMA+".services (server_id, path, serviceType, title, description, inserted) VALUES (?,?,?,?,?, now())");
			PreparedStatement stmt = conn.getPreparedStatement();
			try {
				stmt.setInt(1, id);
				stmt.setString(2, path);
				stmt.setString(3, type);
				stmt.setString(4, title);
				stmt.setString(5, description);
				stmt.execute();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			conn.query("select * from "+SCHEMA+".services where server_id = '"+id+"' and path = '"+path+"' and servicetype = '"+type+"'");
			rs = conn.getQueryResult();
			try {
				if( rs.next() ) {
					serviceId = rs.getInt("service_id");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			
		}

		
		return serviceId;
	}
	
	public synchronized void addTestResult(int serviceId, boolean status) {
		conn.prepare("INSERT INTO "+SCHEMA+".sweeps (service_id, status) values (?,?)");
		PreparedStatement stmt = conn.getPreparedStatement();
		try {
			stmt.setInt(1, serviceId);
			stmt.setBoolean(2, status);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*public void createTmpTable() {
		conn.prepare("CREATE TABLE "+SCHEMA+".sweep ("+
				"service_id integer primary key,"+
				"hs hstore);"
		);
		PreparedStatement stmt = conn.getPreparedStatement();
		try {
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		conn.prepare("grant all on "+SCHEMA+".sweep to datacagov;");
		stmt = conn.getPreparedStatement();
		try {
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
	
	public void addDownServices() {
		LinkedList<Integer> list = new LinkedList<Integer>();
		//conn.query("select service_id from esri.services si where si.service_id not in " +
		//		"(select service_id from esri.service_date_uptime where date_trunc('day', localtimestamp) = time);");
		
		conn.query("select service_id from esri.service_date_uptime where date_trunc('day', localtimestamp) = runtime;");
		ResultSet rs = conn.getQueryResult();
		try {
			while( rs.next() ) {
				list.add(rs.getInt("service_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for( int i: list ) addTestResult(i, false);
	}
	
	@SuppressWarnings("deprecation")
	public void addCbaseItem(ServiceInfo info) {
		if( info.getServiceId() == -1 ) return;
		
		// first create status array
		conn.query("SELECT * FROM "+SCHEMA+".service_date_uptime where service_id = "+info.getServiceId());
		ResultSet rs = conn.getQueryResult();
		String uptimeDate = "";
		String uptimeStatus = "";
		try {
			while( rs.next() ) {
				uptimeDate += toNiceDate(rs.getDate("runtime"))+",";
				uptimeStatus += String.valueOf(rs.getBoolean("status"))+",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if( !uptimeDate.isEmpty() ) {
			uptimeDate = uptimeDate.substring(0, uptimeDate.length()-1);
			uptimeStatus = uptimeStatus.substring(0, uptimeStatus.length()-1);
		}
		conn.closeQuery();

		String inserted = "";
		conn.query("SELECT inserted FROM "+SCHEMA+".services where service_id = "+info.getServiceId());
		rs = conn.getQueryResult();
		try {
			if( rs.next() ) {
				Timestamp ts = rs.getTimestamp("inserted");
				inserted = (ts.getYear()+1900)+"-"+(ts.getMonth()+1)+"-"+ts.getDate();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		conn.closeQuery();
		
		
		conn.prepare("INSERT INTO "+SCHEMA+".sweep (service_id, hs) VALUES"+
				"(?, hstore(ARRAY['Link(text)','Resource(text)','Description(text)','Title(text)', '"+info.getType()+"(text)', " +
				"'Uptime Date(text)@,', 'Uptime Status(bool)@,', 'Date Entered(date)'],ARRAY[?,?,?,?,?,?,?,?]))"
		);
		String url = info.getHost()+"/"+info.getPath()+"/"+info.getType();
		PreparedStatement stmt = conn.getPreparedStatement();
		
		try {
			stmt.setInt(1, info.getServiceId());
			stmt.setString(2, url);
			stmt.setString(3, info.getType());
			stmt.setString(4, info.getDescription());
			stmt.setString(5, info.getTitle());
			stmt.setString(6, url);
			stmt.setString(7, uptimeDate);
			stmt.setString(8, uptimeStatus);
			stmt.setString(9, inserted);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void truncSweepTable() {
		conn.prepare("TRUNCATE "+SCHEMA+".sweep");
		PreparedStatement stmt = conn.getPreparedStatement();
		try {
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void cleanOldSweeps() {
		conn.prepare("set search_path=esri,public");
		PreparedStatement stmt = conn.getPreparedStatement();
		try {
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		/*conn.prepare("select "+SCHEMA+".clean_sweeps()");
		stmt = conn.getPreparedStatement();
		try {
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		conn.prepare("select "+SCHEMA+".delete_old_services()");
		stmt = conn.getPreparedStatement();
		try {
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public String toNiceDate(Date d) {
		return (d.getYear()+1900)+"-"+(d.getMonth()+1)+"-"+d.getDate();
	}

	
	
}
