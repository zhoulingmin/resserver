package com.phoenixcloud.download;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServerSocketListener implements ServletContextListener {
	private SocketThread socketThread;
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		if(socketThread!=null&&socketThread.isInterrupted()){
			socketThread.closeServerSocket();
			socketThread.interrupt();
		}
	}

	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		if(socketThread==null){
			socketThread = new SocketThread();
			// start listener thread when the app start
			socketThread.start();
		}
	}
}
