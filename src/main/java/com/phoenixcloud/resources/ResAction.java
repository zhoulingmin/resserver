package com.phoenixcloud.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

import com.phoenixcloud.common.PhoenixProperties;
import com.phoenixcloud.util.MiscUtils;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/res")
public class ResAction {
	
	private PhoenixProperties prop = PhoenixProperties.getInstance();
	
	@POST
	@Path("uploadFile/{code}/{cataAddr}/{fileName}")
	//@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public String uploadFile(InputStream fis,
			@PathParam("code") String code,
			@PathParam("cataAddr") String cataAddr,
			@PathParam("fileName") String fileName) {
		
		JSONObject retObj = new JSONObject();
		try {
			code = URLDecoder.decode(code, "utf-8");
			fileName = URLDecoder.decode(fileName, "utf-8");
			cataAddr = URLDecoder.decode(cataAddr, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			retObj.put("ret", 1);
			retObj.put("error", e1.toString());
		}
		String bookDir = prop.getProperty("book_res_folder");
		String folderStr = bookDir + File.separator + code + File.separator + cataAddr;
		File folder = new File(folderStr);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(folderStr + File.separator + fileName));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			retObj.put("ret", 0);
			fis.close();
			fos.close();
		} catch (Exception e) {
			retObj.put("ret", 1);
			retObj.put("error", e.toString());
		}

		return retObj.toString();
	}
	
	@POST
	@Path("uploadFile/{code}/{fileName}")
	//@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public String uploadFile(InputStream fis,
			@PathParam("code") String code,
			@PathParam("fileName") String fileName) {
		
		JSONObject retObj = new JSONObject();
		try {
			code = URLDecoder.decode(code, "utf-8");
			fileName = URLDecoder.decode(fileName, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			retObj.put("ret", 1);
			retObj.put("error", e1.toString());
		}
		String bookDir = prop.getProperty("book_res_folder");
		String folderStr = bookDir + File.separator + code;
		File folder = new File(folderStr);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(folderStr + File.separator + fileName));
			byte[] buffer = new byte[1024 * 16];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			retObj.put("ret", 0);
			fis.close();
			fos.close();
		} catch (Exception e) {
			retObj.put("ret", 1);
			retObj.put("error", e.toString());
		}

		return retObj.toString();
	}
	
	@POST
	@Path("uploadFileAndEncrypted/{code}/{cataAddr}/{fileName}")
	//@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public String uploadFileAndEncrypted(InputStream fis,
			@PathParam("code") String code,
			@PathParam("cataAddr") String cataAddr,
			@PathParam("fileName") String fileName) {
		
		JSONObject retObj = new JSONObject();
		try {
			code = URLDecoder.decode(code, "utf-8");
			fileName = URLDecoder.decode(fileName, "utf-8");
			cataAddr = URLDecoder.decode(cataAddr, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			retObj.put("ret", 1);
			retObj.put("error", e1.toString());
		}
		String bookDir = prop.getProperty("book_res_folder");
		String folderStr = bookDir + File.separator + code + File.separator + cataAddr;
		File folder = new File(folderStr);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(folderStr + File.separator + fileName));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			retObj.put("ret", 0);
			fis.close();
			fos.close();
			
			// compress and encrypt above file
			String osName = System.getProperty("os.name");
			String zipTool = prop.getProperty("zip_tool");
			String sourceFileName = folderStr + File.separator + fileName;;
			String zipFileName = sourceFileName + ".zip";;
			if (osName != null 
					&& (-1 != osName.indexOf("windows") || -1 != osName .indexOf("Windows"))) {
				String[] command = new String[5];
				command[0] = zipTool;
				command[1] = "a";
				command[2] = "-p" + MiscUtils.zipPwd;
				command[3] = zipFileName;
				command[4] = sourceFileName;
				
				MiscUtils.runtimeExec(command, zipFileName);
				
				// remove the original file
				File oldFile = new File(sourceFileName);
				if (oldFile != null) {
					oldFile.delete();
				}
			} else {
				// change directory, can't do that in linux 
				//Runtime.getRuntime().exec(new String[]{"cd", folderStr}).waitFor();

				// execute command
				// zip -j -m -P pwd zipfile sourcefilelist
				zipTool += " -j -m -P " + MiscUtils.zipPwd + " " + zipFileName + " " + sourceFileName;
				MiscUtils.getLogger().info("Creating : " + zipFileName);
				MiscUtils.getLogger().info("Running : " + zipTool);
				String[] command = zipTool.split(" ");
				MiscUtils.runtimeExec(command, zipFileName);
			}
			
		} catch (Exception e) {
			retObj.put("ret", 1);
			retObj.put("error", e.toString());
		}

		return retObj.toString();
	}
	
	@POST
	@Path("test")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String test(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition dispostion,
			@FormDataParam("fileName") String fileName) {
		return "";
	}
	
	@GET
	@Path("downloadFile/{code}/{fileName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(@PathParam("code") String code,
			@PathParam("fileName") String fileName) {	

		String bookDir = prop.getProperty("book_res_folder");
		try {
			fileName = URLDecoder.decode(fileName, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			throw new WebApplicationException(404);
		}
		String folderStr = bookDir + File.separator + code;
		File file = new File(folderStr, fileName);
		if (!file.exists()) {
			throw new WebApplicationException(404);
		}
		String mt = new MimetypesFileTypeMap().getContentType(file);
		String downFileName = fileName;
		try {
			downFileName = new String(fileName.getBytes(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			MiscUtils.getLogger().info(e.toString());
		}
		return Response
				.ok(file, mt)
				.header("Content-disposition","attachment;filename=\"" + downFileName + "\"")
				.header("ragma", "No-cache").header("Cache-Control", "no-cache").build();
	}	
	
	@GET
	@Path("downloadFile/{code}/{cataAddr}/{fileName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(@PathParam("code") String code,
			@PathParam("cataAddr") String cataAddr,
			@PathParam("fileName") String fileName) {	

		String bookDir = prop.getProperty("book_res_folder");
		try {
			fileName = URLDecoder.decode(fileName, "utf-8");
			cataAddr = URLDecoder.decode(cataAddr, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			throw new WebApplicationException(404);
		}
		String folderStr = bookDir + File.separator + code + File.separator + cataAddr;
		File file = new File(folderStr, fileName);
		if (!file.exists()) {
			throw new WebApplicationException(404);
		}
		String mt = new MimetypesFileTypeMap().getContentType(file);
		String downFileName = fileName;
		try {
			downFileName = new String(fileName.getBytes(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			MiscUtils.getLogger().info(e.toString());
		}
		return Response
				.ok(file, mt)
				// attachment: indicates download,  inline: indicates opening file in browser.
				// And add the following statement
				// .header("Content-Type", "application/x-shockwave-flash")
				.header("Content-disposition","attachment;filename=\"" + downFileName + "\"")
				.header("ragma", "No-cache").header("Cache-Control", "no-cache").build();
	}
}
