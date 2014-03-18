package com.phoenixcloud.download;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.phoenixcloud.util.MiscUtils;

public class ProcessSocketData extends Thread {
	private Socket socket;
	public ProcessSocketData() {
		super();
	}

	public ProcessSocketData(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			String instring = null;
			StringBuffer reqBuf = new StringBuffer();
			while ((instring = br.readLine()) != null) {
				if (instring != null) {
					reqBuf.append(instring);
					//reqBuf.append("\n");
				}
			}
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			// 解析URL
			
			// 读取文件
			
			// 写入输出流
			pw.flush();
			pw.close();
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			MiscUtils.getLogger().info("error happened while receiving oru msg!!");
		}
	}

}
