package org.ttdc.biz.network.services.helpers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Shacktag;
import org.ttdc.util.ShackTagger;


public class PostFormatter {
	private static Logger log = Logger.getLogger(PostFormatter.class);
	private static final List<Shacktag> shacktags = new ArrayList<Shacktag>();
	static{
		Session session = Persistence.beginSession();
		try{
			Query query = session.getNamedQuery("shacktag.getAll");
			@SuppressWarnings("unchecked") List<Shacktag> l = query.list(); 
			shacktags.addAll(l);
		}
		catch(Exception e){
			log.error(e);
			throw new ExceptionInInitializerError(e);
		}
	}
	
	private static PostFormatter me  = null;
	
	
	public static PostFormatter getInstance(){
		if(me == null)
			me = new PostFormatter();
		return me;
	}
	
	public String htmlEncode(final String s){
		StringBuilder sbuf = new StringBuilder();
	    int len = s.length();
	    for (int i = 0; i < len; i++) {
	      int ch = s.charAt(i);
	      if(ch > 131){
	    	  sbuf.append("&#");
	    	  sbuf.append((int)ch);
	    	  sbuf.append(";");
	      }
	      else{
	    	  sbuf.append((char)ch);
	      }
	    }
	    return sbuf.toString();
	}
	
	private PostFormatter() {
		log.info("PostFormatter!! ");
		
		
			
	}
	
	public String format(final String msg){
		/*
		String retmsg;
		retmsg = msg.replaceAll("\n", "<br>");
		//retmsg = retmsg.replaceAll("\n", "<br>");
		return htmlEncode(retmsg);
		
		//return msg;
		  
		*/
		return htmlEncode(ShackTagger.getInstance().shackTagThis(msg));
	}
	public String formatSummary(final String msg){
		int length = 100;
		String summary = msg.substring(0, length<=msg.length()? length : msg.length());
		
		return htmlEncode(ShackTagger.getInstance().shackTagThis(summary,ShackTagger.Style.REMOVE_CARRIGE_RETURNS));
		
		//return msg;
	}
}
