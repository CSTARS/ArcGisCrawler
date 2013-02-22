package gov.ca.ceres.arcgis;

import java.util.LinkedList;

import gov.ca.ceres.arcgis.db.Database;
import gov.ca.ceres.arcgis.pojo.ServiceInfo;

/**
 * MAKE SURE YOU SETUP THE TUNNEL!!!
 * ssh -f -L 3001:localhost:5432 jrmerz@ec2-50-18-14-244.us-west-1.compute.amazonaws.com -N
 * http://ec2-184-169-252-209.us-west-1.compute.amazonaws.com/
 * 
 * 
 * 
 * @author jrmerz
 *
 */
public class Crawler {

	private static LinkedList<String[]> SERVERS = null;
	
	private static final int MAX_THREADS = 6;
	private static int count = 0;
	private static long startTime = 0;
	private static int runningThreads = 0;
	
	public static int SERVICE_COUNT = 0;
	
	public static LinkedList<ServiceInfo> SERVICES = new LinkedList<ServiceInfo>();
	private static LinkedList<ServiceInfo> CHECK_SERVICES = new LinkedList<ServiceInfo>();
	
	public static void main(String[] args) {
		startTime = System.currentTimeMillis();
		
		Database.INSTANCE.connect();
		SERVERS = Database.INSTANCE.getServers();
		
		 for( int i = 0; i < MAX_THREADS; i++ ) {
			String[] info = SERVERS.get(i);
			ServerCrawler serverCrawler = new ServerCrawler(info[0], info[1], handler, i);

			updateCount();
			runningThreads++;
			serverCrawler.start();
		}
	}
	
	public synchronized static void updateCount() {
		count++;
	}
	
	private static CompleteHandler handler = new CompleteHandler() {
		@Override
		public synchronized void onComplete(int id) {
			if( count < SERVERS.size() ) {
				String[] info = SERVERS.get(count);
				ServerCrawler serverCrawler = new ServerCrawler(info[0], info[1], handler, id);
				serverCrawler.start();
				updateCount();
				return;
			}
			
			runningThreads--;
			if( runningThreads == 0 ) {
				System.out.println("\n***Services found: "+SERVICES.size());
				parseServices();
			}
		}
	};
	
	public static void parseServices() {
		// now start parsing services
		for( int i = 0; i < MAX_THREADS; i++ ) {		
			ServiceInfo info = SERVICES.pop();
			ServiceCrawler sc = new ServiceCrawler(info, info.getHost(), info.getServerId(), serviceHandler, i);
			sc.start();
			CHECK_SERVICES.add(info);
			runningThreads++;
		}
	}
	
	public static CompleteHandler serviceHandler = new CompleteHandler() {
		@Override
		public synchronized void onComplete(int id) {
			if( SERVICES.size() > 0 ) {
				ServiceInfo info = SERVICES.pop();
				ServiceCrawler sc = new ServiceCrawler(info, info.getHost(), info.getServerId(), serviceHandler, id);
				sc.start();
				CHECK_SERVICES.add(info);
			} else {
				runningThreads--;
				if( runningThreads == 0 ) {
					
					System.out.println("\n***Runtime: "+(System.currentTimeMillis()-startTime));
					System.out.println("\n***Services checked: "+SERVICE_COUNT);
					updateItems();
				}
			}
		}
	};
	
	// update the items table;
	private static void updateItems() {
		System.out.println("\n***Updating Cbase Records");
		
		Database.INSTANCE.addDownServices();
		
		Database.INSTANCE.truncSweepTable();
		
		for( ServiceInfo info: CHECK_SERVICES ) {
			Database.INSTANCE.addCbaseItem(info);
		}
		
		Database.INSTANCE.cleanOldSweeps();
		
		Database.INSTANCE.disconnect();
		System.out.println("***Done.");
	}
	

	
	

}
