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

@Path("/book")
public class BookAction {
	
	private PhoenixProperties prop = PhoenixProperties.getInstance();
	
	@POST
	@Path("uploadFile/{code}/{fileName}")
	//@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public String uploadFile(InputStream fis,
			@PathParam("code") String code,
			@PathParam("fileName") String fileName) {
		
		JSONObject retObj = new JSONObject();
		try {
			fileName = URLDecoder.decode(fileName, "utf-8");
			code = URLDecoder.decode(code, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			retObj.put("ret", 1);
			retObj.put("error", e1.toString());
		}
		String bookDir = prop.getProperty("book_file_folder");
		File folder = new File(bookDir + File.separator + code);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(bookDir + File.separator + code + File.separator + fileName));
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
	@Path("uploadFile/{code}/cover/{fileName}")
	//@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public String uploadCover(InputStream fis,
			@PathParam("code") String code,
			@PathParam("fileName") String fileName) {
		
		JSONObject retObj = new JSONObject();
		try {
			fileName = URLDecoder.decode(fileName, "utf-8");
			code = URLDecoder.decode(code, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			retObj.put("ret", 1);
			retObj.put("error", e1.toString());
		}
		String bookDir = prop.getProperty("book_file_folder");
		File folder = new File(bookDir + File.separator + code);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(bookDir + File.separator + code + File.separator + fileName));
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

		String bookDir = prop.getProperty("book_file_folder");
		try {
			fileName = URLDecoder.decode(fileName, "utf-8");
			code = URLDecoder.decode(code, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			throw new WebApplicationException(404);
		}
		File file = new File(bookDir + File.separator + code, fileName);
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
	@Path("downloadFile/{code}/cover/{fileName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadCover(@PathParam("code") String code,
			@PathParam("fileName") String fileName) {	

		String bookDir = prop.getProperty("book_file_folder");
		try {
			fileName = URLDecoder.decode(fileName, "utf-8");
			code = URLDecoder.decode(code, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			throw new WebApplicationException(404);
		}
		File file = new File(bookDir + File.separator + code, fileName);
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
	
	
}
