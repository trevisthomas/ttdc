package org.ttdc.gwt.server.beanconverters;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.ttdc.gwt.shared.util.StringTools;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;

public class ConversionUtils {
	private final static String markerOpen = "START123"+System.currentTimeMillis()+"";
	private final static String markerEnd = "END123"+System.currentTimeMillis()+"";
	
	public static String preparePostSummaryForDisplay(final String value) {
		if(value == null)
			return null;

		//Removing any html from the summary.
		String summary = StringTools.escapeRexExSpecialCharacters(value);
	
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
		
		//Sigh, replace escaped characters for rendering. 
		temp = StringTools.unescapeRexExSpecialCharacters(temp);
		
		if(value.length() > 100){
			summary = summary.substring(0, 100) + "...";
		}	
		
		return temp;
	}
	
	/**
	 *	Replace s=document.getElementById("tggle_1280081345898"); with newHotness so that old embeds work 
	 * in v7 
	 */
	private final static String newEmbedHotness = "s=document.getElementById(\"11E63E28-0961-45B4-91B7-61DB70944ADE\");";
	public static String fixV6Embed_(final String body){
		if(body.indexOf("document.getElementById(") < 0){
			return body;
		}
		
		Pattern p = Pattern.compile("s=document\\.getElementById\\(\".*?\"\\);");
		Matcher m = p.matcher(body);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			m.appendReplacement(sb, newEmbedHotness);
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	//tggle_embed7(embedHtml)
	public static String fixV6Embed(final String body){
		if(body.indexOf("document.getElementById(") < 0){
			return body;
		}
		
//		Pattern p = Pattern.compile("s=document\\.getElementById\\(\".*?\"\\);");
//		Matcher m = p.matcher(body);
//		StringBuffer sb = new StringBuffer();
//		while(m.find()){
//			m.appendReplacement(sb, newEmbedHotness);
//		}
		
		Pattern p1 = Pattern.compile("<script language=\\'JavaScript\\'> function tggle_.*?>embedded</span></a>");
		//Pattern p1 = Pattern.compile( StringTools.escapeRexExSpecialCharacters("<script language=")+".*?"+ StringTools.escapeRexExSpecialCharacters("embedded</span>"));
		Matcher m1 = p1.matcher(body);
		StringBuffer sb1 = new StringBuffer();
		
		while(m1.find()){
			//m1.appendReplacement(sb1, newEmbedHotness);
			String v7embed = "";
			String subSection = body.substring(m1.start(),m1.end());
			//subSection = subSection.replaceAll("\\<.*?\\>", "");
			
			Pattern p = Pattern.compile("s\\.innerHTML=\\'.*?\\'; else s\\.innerHTML");
			Matcher m = p.matcher(subSection);
			StringBuffer sb = new StringBuffer();
			if(m.find()){
				//m.appendReplacement(sb, newEmbedHotness);
				String sub2 = subSection.substring(m.start(),m.end());
				String embed = sub2.substring(sub2.indexOf('\'')+1, sub2.lastIndexOf('\''));
				System.out.println(embed);
				
				embed = embed.replaceAll("\"", "");
				
				v7embed = "<a href=\"javascript:tggle_embed7('"+embed+"');\">[view]</a>";
				
				m1.appendReplacement(sb1, v7embed);
			}
		}
		
		
		
//		//Get the embeded script
//		Pattern p = Pattern.compile("s\\.innerHTML=\\'.*?\"\\'; else s\\.innerHTML");
//		Matcher m = p.matcher(body);
//		StringBuffer sb = new StringBuffer();
//		while(m.find()){
//			m.appendReplacement(sb, newEmbedHotness);
//		}
		
		//replace the javascript call
		m1.appendTail(sb1);
		return sb1.toString();
	}
}



