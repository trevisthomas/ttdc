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

import org.ttdc.util.StringTools;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;

public class ConversionUtils {
	private final static String markerOpen = "START123"+System.currentTimeMillis()+"";
	private final static String markerEnd = "END123"+System.currentTimeMillis()+"";
	
//	public static String preparePostSummaryForDisplay(final String value) {
//		Reader r;
//		try{
//			r = new StringReader(value);
//			HTMLEditorKit.Parser parser;
//		    System.out.println("About to parse " + value);
//		    parser = new ParserDelegator();
//		    parser.parse(r, new HTMLParseLister(), true);
//		    r.close();
//		    String s = r.toString();
//		    return s;
//		    
//		}
//		catch(Exception e){
//			return "error";
//		}
//	}
	
	
	
	public static String preparePostSummaryForDisplay(final String value) {
		if(value == null)
			return null;

		//Removing any html from the summary.
		String summary = StringTools.escapeRexExSpecialCharacters(value);
		if(value.length() > 1000){
			summary = summary.substring(0, 1000);
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
		
		//Sigh, replace escaped characters for rendering. 
		temp = StringTools.unescapeRexExSpecialCharacters(temp);
		
		return temp;
	}
	
	/**
	 *	Replace s=document.getElementById("tggle_1280081345898"); with newHotness so that old embeds work 
	 * in v7 
	 */
	private final static String newEmbedHotness = "s=document.getElementById(\"11E63E28-0961-45B4-91B7-61DB70944ADE\");";
	public static String fixV6Embed(final String body){
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
}

/**
 * HTML parsing proceeds by calling a callback for
 * each and every piece of the HTML do*****ent.  This
 * simple callback class simply prints an indented
 * structural listing of the HTML data.
 */
class HTMLParseLister extends HTMLEditorKit.ParserCallback
{
    int indentSize = 0;

    protected void indent() { 
        indentSize += 3;
    }
    protected void unIndent() {
        indentSize -= 3; if (indentSize < 0) indentSize = 0;
    }

    protected void pIndent() {
        for(int i = 0; i < indentSize; i++) System.out.print(" ");
    }

    public void handleText(char[] data, int pos) {
        pIndent();
        System.out.println("Text(" + data.length + " chars)");
    }

    public void handleComment(char[] data, int pos) {
        pIndent();
        System.out.println("Comment(" + data.length + " chars)");
    }

    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        pIndent();
        System.out.println("Tag start(<" + t.toString() + ">, " +
                           a.getAttributeCount() + " attrs)");
        indent();
    }

    public void handleEndTag(HTML.Tag t, int pos) {
        unIndent();
        pIndent();
        System.out.println("Tag end(</" + t.toString() + ">)");
    }

    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        pIndent();
        System.out.println("Tag(<" + t.toString() + ">, " +
                           a.getAttributeCount() + " attrs)");
    }

    public void handleError(String errorMsg, int pos){
        System.out.println("Parsing error: " + errorMsg + " at " + pos);
    }
}    

