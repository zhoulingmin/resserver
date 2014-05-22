package com.phoenixcloud.resources;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;

import com.phoenixcloud.common.PhoenixProperties;

@Path("/manageFiles")
public class ManageFiles {
	private PhoenixProperties prop = PhoenixProperties.getInstance();
	
	@POST
	@Path("/changeFolder")
	//@Consumes("text/plain")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public String changeFolder(@QueryParam("oldFolder") String oldFolder
			, @QueryParam("newFolder") String newFolder) {
		JSONObject retObj = new JSONObject();
		try {
			oldFolder = URLDecoder.decode(oldFolder, "utf-8");
			newFolder = URLDecoder.decode(newFolder, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			retObj.put("ret", 1);
			retObj.put("error", e.toString());
		}
		
		String bookDir = prop.getProperty("book_file_folder");
		String path = bookDir + File.separator + oldFolder;
		
		do {
			File oldBookFolder = new File(path);
			if (!oldBookFolder.exists()) {
				retObj.put("ret", 404);
				retObj.put("error", "目录:" + path + " 不存在！");
				break;
			}
			
			path = bookDir + File.separator + newFolder;
			File newBookFolder = new File(path);
			if (newBookFolder.exists()) {
				retObj.put("ret", 409);
				retObj.put("error", "目标目录:" + path + " 已存在！");
				break;
			}
			
			path = bookDir + File.separator + oldFolder + File.separator + oldFolder + ".pkg";
			File oldBook = new File(path);
			if (!oldBook.exists()) {
				retObj.put("ret", 404);
				retObj.put("error", "文件:" + path + " 不存在！");
				break;
			}
			
			path = bookDir + File.separator + oldFolder + File.separator + newFolder + ".pkg";
			File newBook = new File(path);
			if (newBook.exists() && !newBook.delete()) {
				retObj.put("ret", 409);
				retObj.put("error", "文件:" + path + " 已存在！");
				break;
			}
			
			String resDir = prop.getProperty("book_res_folder");
			path = resDir + File.separator + oldFolder;
			File oldResFolder = new File(path);
			if (oldResFolder.exists()) {
				path = resDir + File.separator + newFolder;
				File newResFolder = new File(path);
				if (newResFolder.exists()) {
					retObj.put("ret", 409);
					retObj.put("error", "目标资源:" + path + " 已存在！");
					break;
				}
				// 1.change resource folder
				try {
					FileUtils.moveDirectory(oldResFolder, newResFolder);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					retObj.put("ret", 1);
					retObj.put("error", e.toString());
					break;
				}
			}
			
			// 2.change book file
			try {
				FileUtils.moveFile(oldBook, newBook);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				retObj.put("ret", 1);
				retObj.put("error", e1.toString());
				break;
			}
			
			// 3.change book folder
			try {
				FileUtils.moveDirectory(oldBookFolder, newBookFolder);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				retObj.put("ret", 1);
				retObj.put("error", e1.toString());
				break;
			}
			
			retObj.put("ret", 0);
			
		} while(false);
		
		return retObj.toString();
	}
}
