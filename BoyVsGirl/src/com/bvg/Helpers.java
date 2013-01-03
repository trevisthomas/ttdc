package com.bvg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Helpers {
	private final static SimpleDateFormat formatZen = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	private final static SimpleDateFormat formatRss = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	
	
	public static Date getAsDate(Map<String, Object> source, String name){
		if(source == null){
			return null;
		}
		Map<String, Object> map = (Map<String, Object>)source.get(name);
		if(map == null){
			return null;
		}
		String time = (String)map.get("Value");
		Date date = null;
		try {
			date = formatZen.parse(time + " GMT");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getDateAsRssString(Map<String, Object> source, String name){
		Date d = getAsDate(source, name);
		if(d != null){
			return formatRss.format(d);
		}
		else {
			return "";
		}
	}
	
	public static String encodeHTML(String s)
	{
	    StringBuffer out = new StringBuffer();
	    for(int i=0; i<s.length(); i++)
	    {
	        char c = s.charAt(i);
	        if(c > 127 || c=='"' || c=='<' || c=='>')
	        {
	           out.append("&#"+(int)c+";");
	        }
	        else
	        {
	            out.append(c);
	        }
	    }
	    return out.toString();
}
}
