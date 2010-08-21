package org.ttdc.gwt.server.beanconverters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversionUtils {
	private final static String markerOpen = "START123"+System.currentTimeMillis()+"";
	private final static String markerEnd = "END123"+System.currentTimeMillis()+"";
	
	public static String preparePostSummaryForDisplay(final String value) {
		if(value == null)
			return null;
			
		//Removing any html from the summary.
		String summary;
		if(value.length() > 60){
			summary = value.substring(0, 60);
		}
		else{
			summary = value;
		}
		
		Pattern p = Pattern.compile("<div class=shackTag_q>.*?\\>");
		summary = summary + "</div>";
		Matcher m = p.matcher(summary);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			String subSection = summary.substring(m.start(),m.end());
			subSection = subSection.replaceAll("\\<.*?\\>", "");
			m.appendReplacement(sb, markerOpen+subSection+markerEnd);
		}
		m.appendTail(sb);
		
		String temp = sb.toString().replaceAll("\\<.*?\\>", " ");
		
		temp = temp.replaceAll(markerOpen, "<i>\"").replaceAll(markerEnd, "\"</i>");
		
		return temp;
	}
}
