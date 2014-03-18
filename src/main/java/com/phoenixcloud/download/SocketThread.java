package com.phoenixcloud.download;


/**
 * override the function of run,and accept and deal with client's requests 
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.phoenixcloud.common.PhoenixProperties;
import com.phoenixcloud.common.Constants;
class SocketThread extends Thread {
	private ServerSocket serverSocket;

	public SocketThread() {
		if (serverSocket == null) {
			try {
				int port = PhoenixProperties.getInstance().getIntProperty("hfs_port");
				if (0 == port) {
					port = Constants.PORT_FOR_PROVIDING_DOWNLOAD;
				}
				this.serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		// loop
		while (!this.isInterrupted()) {
			try {
				Socket socket = serverSocket.accept();
				if (socket != null)
					new ProcessSocketData(socket).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void closeServerSocket() {
		try {
			if (serverSocket != null && !serverSocket.isClosed())
				serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
