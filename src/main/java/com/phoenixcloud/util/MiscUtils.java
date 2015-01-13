package com.phoenixcloud.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MiscUtils {
	private static final int PROCESS_COMPLETION_CYCLE_CHECK_PERIOD = 250;
	private static final int MAX_NO_CHANGE_COUNT = 40000 / PROCESS_COMPLETION_CYCLE_CHECK_PERIOD;
	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	private static String baseChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*();':\"[]{},./<>?|\\-=_+";
	public static String zipPwd = "Za1F4f2x!(dfaERu4LC9.po0*2RxEKM3";
	
	public static Logger getLogger() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String caller = ste[2].getClassName();
		return (Logger.getLogger(caller));
	}

	public static String getCurrentTime(String formatter) {
		SimpleDateFormat dateFformatter = new SimpleDateFormat(formatter);
		return dateFformatter.format(new Date());
	}

	public static String getCurrentTime() {
		return formatter.format(new Date());
	}

	public static InputStream getResource(String res) {
		InputStream in = null;
		if (StringUtils.isNotEmpty(res)) {
			in = MiscUtils.class.getResourceAsStream(res);
			if (in == null) {
				in = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(res);
			}
		}
		return in;
	}
	
	public static String getRandomString(int num) {
		if (num < 1) {
			num = 1;
		}
		int len = baseChars.length();
		StringBuffer randomStr = new StringBuffer();
		Random random = new Random();
		for(int i=0; i<num; i++){
			randomStr.append(baseChars.charAt(random.nextInt(len)));
		}
		return randomStr.toString();
	}
	
	public static void runtimeExec(String[] command, String outputFilename) throws IOException {
		
		File f = new File(outputFilename);
		
		Process process = Runtime.getRuntime().exec(command);

		long previousFileSize = 0;
		int noFileSizeChangeCounter = 0;

		try {
			while (true) {
				try {
					Thread.sleep(PROCESS_COMPLETION_CYCLE_CHECK_PERIOD);
				} catch (InterruptedException e) {
					MiscUtils.getLogger().error("Thread interrupted", e);
				}

				try {
					int exitValue = process.exitValue();

					if (exitValue != 0) {
						MiscUtils.getLogger().debug("Error running command : " + command);

						String errorMsg = StringUtils.trimToNull(IOUtils.toString(process.getInputStream()));
						if (errorMsg != null) {
							MiscUtils.getLogger().debug(errorMsg);
						}

						errorMsg = StringUtils.trimToNull(IOUtils.toString(process.getErrorStream()));
						if (errorMsg != null) {
							MiscUtils.getLogger().debug(errorMsg);
						}
					}
					return;
				} catch (IllegalThreadStateException e) {
					long tempSize = f.length();

					MiscUtils.getLogger().debug("Progress output filename=" + outputFilename + ", filesize=" + tempSize);

					if (tempSize != previousFileSize) {
						noFileSizeChangeCounter = 0;
					} else {
						noFileSizeChangeCounter++;
						if (noFileSizeChangeCounter > MAX_NO_CHANGE_COUNT) {
							break;
						}
					}
				}
			}
			MiscUtils.getLogger().error("Error, process appears stalled. command=" + command);
		} finally {
			process.destroy();
		}
	}
}
