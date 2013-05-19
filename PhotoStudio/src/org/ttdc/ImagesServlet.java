package org.ttdc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ImagesServlet extends HttpServlet {
	
	private static final Map<String, FolderMonitor> monitorMap = new HashMap<String, FolderMonitor>();
	
	static{
		monitorMap.put("trevis", new FolderMonitor("C:/PhotoStudioDemo/trevis"));
		monitorMap.put("chrissy", new FolderMonitor("C:/PhotoStudioDemo/chrissy"));
	}
	
	public static FolderMonitor getFolderMonitor(String key){
		return monitorMap.get(key);
	}
	
	public static Set<String> getMonitoredNames(){
		return monitorMap.keySet();
	}
	
	public ImagesServlet() {
		System.err.println("Servlet constructed.");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if ("/latestImage".equals(req.getServletPath())) {
			System.err.println("Getting the latest image");

		} else if ("/imagesSince".equals(req.getServletPath())) {
			
//			String lastImage = req.getParameter("last");
//			String who = req.getParameter("who");
//			
//			System.err.println("Getting the images by: "+who+" since: "+lastImage);
//			
//			FolderMonitor fm = monitorMap.get(who);
//			List<String> newImages = fm.getAllFilesSince(lastImage);
//			
//			if(newImages.size() > 0){
//				req.setAttribute("listOfImages", newImages);
//				javax.servlet.RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/listOfImages.jsp");
//				rd.forward(req, resp);
//			}
//
			
			//JSON
			String lastImage = req.getParameter("last");
			String who = req.getParameter("who");
			
			System.err.println("Getting the images by: "+who+" since: "+lastImage);
			
			FolderMonitor fm = monitorMap.get(who);
			List<String> newImages = fm.getAllFilesSince(lastImage);
			
			if(newImages.size() > 0){
				req.setAttribute("listOfImages", newImages);
				javax.servlet.RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/listOfImagesJSON.jsp");
				rd.forward(req, resp);
			}
			
		}
		else{
			System.err.println("Servlet doesnt know what to do.");
		}
	}
}
