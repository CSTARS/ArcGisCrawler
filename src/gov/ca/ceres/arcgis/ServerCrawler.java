package gov.ca.ceres.arcgis;

public class ServerCrawler extends Thread {
	
	private DirectoryCrawler rootDir = null;
	private CompleteHandler handler = null;
	private int threadId = -1;
	
	public ServerCrawler(String url, String serverId, CompleteHandler handler, int threadId) {
		this.handler = handler;
		this.threadId = threadId;
		
		rootDir = new DirectoryCrawler(url, serverId, "", threadId);
	}

	@Override
	public void run() {
		rootDir.run();
		handler.onComplete(threadId);
	}

}
