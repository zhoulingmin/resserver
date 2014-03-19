package com.phoenixcloud.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

import com.phoenixcloud.common.PhoenixProperties;
import com.phoenixcloud.util.MiscUtils;

@Path("/book")
public class BookAction {
	
	private PhoenixProperties prop = PhoenixProperties.getInstance();
	
	@POST
	@Path("uploadFile/{code}/{fileName}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public String uploadFile(InputStream fis,
			@PathParam("code") String code,
			@PathParam("fileName") String fileName) {	

		String bookDir = prop.getProperty("book_file_folder");
		File folder = new File(bookDir + File.separator + code);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		JSONObject retObj = new JSONObject();
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(bookDir + File.separator + code + File.separator + fileName));
			byte[] buffer = new byte[1024 * 16];
			while ((fis.read(buffer)) != -1) {
				fos.write(buffer);
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
	
	@GET
	@Path("downloadFile/{code}/{fileName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(@PathParam("code") String code,
			@PathParam("fileName") String fileName) {	

		String bookDir = prop.getProperty("book_file_folder");
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
				.header("Content-disposition","attachment;filename=" + downFileName + ".pdf")
				.header("ragma", "No-cache").header("Cache-Control", "no-cache").build();
	}
	
}
