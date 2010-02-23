package org.ttdc.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.ttdc.persistence.objects.ImageFull;

public class ImageUploadServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ImageServer.class);
	
	private static final long MAX_SIZE = 1024*1024*2;
	//private static final String ACCEPTABLE_CONTENT_TYPE = "image/jpeg";
	private String [] contentTypes = {"image/jpeg","image/jpg","image/png"};
	private static final String CONTENT_TYPE_UNACCEPTABLE = "{error: 'File upload failed. "
			+ " Only images can be uploaded'}";
	
	private static final String GENERIC_ERROR = "{error: 'File upload failed. Shrug'}";

	private static final String SIZE_UNACCEPTABLE = "{error: 'File upload failed. File size must be " + MAX_SIZE
			+ " bytes or less'}";

	private static final String SUCCESS_MESSAGE = "{success: 'File upload succeeded', guid: '%s'}";
	

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String json = null;
		json = getFileItem(request);
		
		response.setContentType("text/html");
		response.getWriter().write(json);
	}

	private String getFileItem(HttpServletRequest request) {
		String json = GENERIC_ERROR;
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem> items = upload.parseRequest(request);
			FileItem fileItem = null;
			String saveAs = null;
			
			for(FileItem item : items){
				if (!item.isFormField() && "uploadFormElement".equals(item.getFieldName())) {
					fileItem = item;
					saveAs = fixFileName(item.getName());
				}
				else if(item.isFormField() && "saveAs".equals(item.getFieldName())){
					saveAs = item.getString();
				}
			}
			json = processFile(request, fileItem, saveAs);
		} catch (FileUploadException e) {
			log.error(e);
			return "{error: '"+e.getMessage()+"'}"; 
		}
		return json;
	}
	
	/**
	 * IE8 passed in the full file path!
	 * 
	 * @param name
	 * @return
	 */
	private String fixFileName(String name) {
		if(name.indexOf('\\') > 0){
			return name.substring(name.lastIndexOf('\\')+1);
		}else if(name.indexOf('/') > 0){
			return name.substring(name.lastIndexOf('/')+1);
		}
		else 
			return name;
		
	}

	private String processFile(HttpServletRequest request, FileItem item, String saveAs) {
		if (!isContentTypeAcceptable(item))
			return CONTENT_TYPE_UNACCEPTABLE;

		if (!isSizeAcceptable(item))
			return SIZE_UNACCEPTABLE;
	
		String message = null;
		try {
			message = saveFile(request, item.getInputStream(), saveAs);
		} catch (Exception e) {
			log.error(e);
			return "{error: '"+e.getMessage()+"'}"; 
		}
		
		return message;
	}
	
	private String saveFile(HttpServletRequest request, InputStream stream, String saveAs) {
		try{
			SessionProxy sp = new SessionProxy(request);
			String personId = sp.getPersonIdFromSession();
			ImageProxy ip = new ImageProxy(personId);
			ImageFull image = ip.saveImageFile(stream, saveAs);
			log.info("ImageUploadServlet created " + image.getName() +" sucessfully.");
			return String.format(SUCCESS_MESSAGE, image.getImageId());
		}
		catch(Throwable e){
			return "{error: '"+e.getMessage()+"'}"; 
		}
	}
	
	private boolean isSizeAcceptable(FileItem item) {
		return item.getSize() <= MAX_SIZE;
	}

	private boolean isContentTypeAcceptable(FileItem item) {
		List<String> list = Arrays.asList(contentTypes);
		return list.contains(item.getContentType());
	}

}
