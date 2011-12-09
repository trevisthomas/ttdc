package com.ttdc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class ImageUploadServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ImageUploadServlet.class.getName());
	
	private static final String GENERIC_ERROR = "{error: 'File upload failed. Shrug'}";
	private static final String SUCCESS_MESSAGE = "{success: 'File upload succeeded', guid: '%s'}";

	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String json = null;
		json = getFileItem(request);
		
		response.setContentType("text/html");
		response.getWriter().write(json);
	}

	private String getFileItem(HttpServletRequest request) throws IOException{
		String json = GENERIC_ERROR;
		ServletFileUpload upload = new ServletFileUpload();
		Map<String, String> params = new HashMap<String,String>();
		try {
			FileItemIterator itr = upload.getItemIterator(request);
			FileItemStream fileItem = null;
			Image image = null;
			while(itr.hasNext()){
				FileItemStream item = itr.next();
				if (!item.isFormField() && "uploadFormElement".equals(item.getFieldName())) {
					fileItem = item;
					image = ImageTool.createImage(item.openStream());
				}
				else if(item.isFormField()){
					String content = toStringBuilder(new InputStreamReader(item.openStream())).toString();
					params.put(item.getFieldName(), content);
				}
			}
			json = saveFile(image, params);
		} catch (FileUploadException e) {
			log.warning(e.toString());
			return "{error: '"+e.getMessage()+"'}"; 
		}
		return json;
	}
	
	private static StringBuilder toStringBuilder(Readable r)
    throws IOException
	{
	    StringBuilder sb = new StringBuilder();
	    copy(r, sb);
	    return sb;
	}
	
	public static long copy(Readable from, Appendable to)
    throws IOException
	{
	    CharBuffer buf = CharBuffer.allocate(2048);
	    long total = 0L;
	    do
	    {
	        int r = from.read(buf);
	        if(r != -1)
	        {
	            buf.flip();
	            to.append(buf, 0, r);
	            total += r;
	        } else
	        {
	            return total;
	        }
	    } while(true);
	}
	
	private String saveFile(Image image, Map<String, String> params) {
		try{
			UserService userService = UserServiceFactory.getUserService();
	        User user = userService.getCurrentUser();
			
			
			ImageTool.saveOrUpdate(user, params.get("id"),  params.get("order"), 
					 params.get("title"),  params.get("url"), image,  params.get("description"),  params.get("dateTaken"), params.get("purchaseUrl"));
			
			
			log.info("ImageUploadServlet created " + params.get("title") +" sucessfully.");
			return String.format(SUCCESS_MESSAGE, image.getKey().getId());
		}
		catch(Throwable e){
			return "{error: '"+e.getMessage()+"'}"; 
		}
	}
}
