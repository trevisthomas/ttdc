package org.ttdc.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Shacktag;

/**
 * Tags content, shack style.
 * 
 * Singleton
 * 
 * This singleton is implemented as an enum. (Jan 3 2010, i changed this back to a class to debug a weird jsp error?)
 * 
 * @author Trevis
 *
 */
public class ShackTagger {
	private static ShackTagger INSTANCE;  
	
	public enum Style {CONVERT_CARRAIGE_RETURNS,REMOVE_CARRIGE_RETURNS}
	
	private static Logger log = Logger.getLogger(ShackTagger.class);
	private final List<Shacktag> shacktags = new CopyOnWriteArrayList<Shacktag>();
	
	
	private ShackTagger() {
		Session session = Persistence.beginSession();
		try{
			Query query = session.getNamedQuery("shacktag.getAll");
			@SuppressWarnings("unchecked") List<Shacktag> l = query.list(); 
			shacktags.addAll(l);
		}
		catch(Exception e){
			//log.error(e);
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static ShackTagger getInstance(){
		if(INSTANCE == null)
			INSTANCE = new ShackTagger();
		return INSTANCE;
	}
	
	public String shackTagThis(String str){
		return shackTagThis(str,Style.CONVERT_CARRAIGE_RETURNS);
	}
	public String shackTagThis(String str, Style style) {
		try {
			if (str == null)
				return null;
			//Because they cant be trusted.
			//str = htmlEncode(str);//Once again. >:-|

			//URL's
			str = fixURL(str, " https://", " ");
			str = fixURL(str, " http://", " ");
			//str = fixURL(str, " www", " ");//These dont work right anyway.  they go to http://www.trevisthomas.com/www.yourlink.com
			str = fixURL(str, " link:", " ");

			//I am god of all things cool.
			
			for(Shacktag tag : shacktags){
				str =
					replace(
						str,
						tag.getOpenKey(),
						tag.getOpenTag(),
						tag.getCloseKey(),
						tag.getCloseTag());
			}
			
			//Carriage Returns.
			if(style.equals(Style.CONVERT_CARRAIGE_RETURNS))
				str = replace(str, "\n", " <br>");
			else if(style.equals(Style.REMOVE_CARRIGE_RETURNS))
				str = replace(str, "\r", "");
			else 
				throw new UnsupportedOperationException();

			//SlayerTalesTags
			str = replace(str, "~~~~~", "<p align=\"center\">~~~~~</p>");

			//Trev's custom crazy image linker
			str = replace(str, "1|", "<a href=\"");
			str = replace(str, "|2|", "\"><img ");
			str =
				replace(
					str,
					"|3|",
					" border=3 class=thumbOut onmouseout=\"this.className='thumbOut';\" onmouseover=\"this.className='thumbOver';\" src=\"");
			str = replace(str, "|4", "\"></a>");
			//Wack!
			str =
				replace(
					str,
					"|2",
					"\"><img  border=3 class=thumbOut onmouseout=\"this.className='thumbOut';\" onmouseover=\"this.className='thumbOver';\" src=\"");
		}
		
		catch (RuntimeException e) {
			log.error("Caught some sort of error that caused the tagging parser to die!. \n \""+str+ "\"\n" +e.getMessage());
		}

		return str;
	}
	
	private String makeLink(String urlStr) {
		urlStr.trim();
		int urlndx;
		int end;
		int start;
		String dispUrlStr;
		int splitndx; //For the explicit url

		if (urlStr.length() > 0) {
			splitndx = urlStr.indexOf("|");
			if (splitndx == -1) {
				urlndx = urlStr.indexOf("/", 8); //Truncate url;
				if (urlndx != -1)
					dispUrlStr = urlStr.substring(0, urlndx + 1) + "...";
				else
					dispUrlStr = urlStr;
			}
			else {

				if (urlStr.charAt(splitndx + 1) == '"') {
					//Find the closing quote.
					start = splitndx + 1;
					end = urlStr.indexOf('"', splitndx + 2);
					//No closing quote, Carriage return
					if (end == -1)
						end = urlStr.indexOf('\r', splitndx + 2);
					//No return? Find a space.
					if (end == -1)
						end = urlStr.indexOf(" ", splitndx + 2);
					//No space, just use it all.
					if (end == -1)
						end = urlStr.length();

					dispUrlStr = urlStr.substring(start + 1, end);
				}
				else {
					start = splitndx + 1;
					end = urlStr.indexOf('\r', splitndx + 1);
					//No return? Find a space.
					if (end == -1)
						end = urlStr.indexOf(" ", splitndx + 1);
					//No space, just use it all.
					if (end == -1)
						end = urlStr.length();
					dispUrlStr = urlStr.substring(start, end);
				}

				//urlStr = urlStr.substring(urlStr.indexOf(":") + 1, splitndx);
				urlStr = urlStr.substring(0, splitndx);
			}

			if (dispUrlStr.length() <= 0) {
				dispUrlStr = urlStr;
			}
			urlStr = urlStr.trim();
			dispUrlStr = dispUrlStr.trim();
			if (dispUrlStr.length() > 50)
				dispUrlStr = dispUrlStr.substring(0, 50) + "...";
			String str = " <a href=\"" + urlStr + "\">" + dispUrlStr + "</a>";
			return str;
		}
		return "";
	}
	
	private String fixURL(String text, String startTag, String endTag) {
		StringBuilder buffer = new StringBuilder();
		int start;
		int end;
		int end2;
		int ndx = 0;
		String url;

		if (text.indexOf(startTag) != -1)
			while (true) {
				start = text.indexOf(startTag, ndx);
				if (start == -1) {
					buffer.append(text.substring(ndx));
					break;
				}
				buffer.append(text.substring(ndx, start));
				//Check for carrage return first!
				end2 = text.indexOf("\r", start + startTag.length());
				end = text.indexOf(endTag, start + startTag.length());

				if ((end2 < end && end2 > -1) || end == -1)
					end = end2;

				//Look between start and end for a | if their is one
				//look for a quote following, if that is also there, search for the next quote
				//if that also exists, use it as the new end. if not use the old one.
				if (end > 0) //Test is un necessary if we're at the end already
					{
					String substr = text.substring(start, end);
					int quote = substr.indexOf("|\"");
					int endquote;
					if (quote > 0) {

						endquote = text.indexOf('"', quote + start + startTag.length());
						if (endquote > 0)
							end = endquote + 1;
					}
				}

				if (end == -1) {
					url = text.substring(start);
					ndx = text.length();
				}
				else {
					url = text.substring(start, end);
					ndx = end;
				}

				url = makeLink(url);

				buffer.append(url);

			}

		if (buffer.length() == 0) {
			return text;
		}
		else {
			return buffer.toString();
		}
	}
	
	/**
	 * Does a string replace.  
	 * 
	 * I tried to replace this with String.replaceAll but that went very badly.  App runs out of memory? Shrug.
	 * 
	 * @param text
	 * @param oldS
	 * @param newS
	 * @return
	 */
	public String replace(String text, String oldS, String newS) {
		StringBuilder buffer = new StringBuilder();
		int ndx = 0;
		int start;
		int oldLength = oldS.length();
		while (true) {
			start = text.indexOf(oldS, ndx);
			if (start == -1) {
				buffer.append(text.substring(ndx));
				break;
			}
			buffer.append(text.substring(ndx, start));
			buffer.append(newS);
			ndx = start + oldLength;
		}
		return buffer.toString();
	}
	
	/**
	 * Replaces pairs of strings within a string, this is designed specifically for replacing 
	 * beginning and ending tags in a way that safely handles missing tags. 
	 *
	 * Old description from v5:
	 *  
	 * Ok this sexy little function is designed to replace user addable enclosing tags.
	 * it tracks the opening requests and if a different number of closing tags
	 * are added then this compensates for it.
	 *
	 * This replace will replace oldS with newS and oldN with newN and if there are
	 * different numbers of occurances of oldN and newN an appriate number of newN's will
	 * be tagged onto the end. this version is to compensate for broken shacktags!
	 * 
	 * @param text string containing values to be replaced
	 * @param oldS 
	 * @param newS
	 * @param oldN
	 * @param newN
	 * @return returns the string with the values properly replaced
	 */
	private String replace(String text, String oldS, String newS, String oldN, String newN) {
		int replacements = 0;
		StringBuilder buffer = new StringBuilder();
		int ndx = 0;
		int start;
		int oldLength = oldS.length();
		while (true) {
			start = text.indexOf(oldS, ndx);
			if (start == -1) {
				buffer.append(text.substring(ndx));
				break;
			}
			buffer.append(text.substring(ndx, start));
			buffer.append(newS);
			replacements++;
			ndx = start + oldLength;
		}
		text = buffer.toString();
		//Ok now do the closing tags
		buffer = new StringBuilder();
		ndx = 0;
		oldLength = oldN.length();
		while (true) {
			start = text.indexOf(oldN, ndx);
			if (start == -1) {
				buffer.append(text.substring(ndx));
				break;
			}
			buffer.append(text.substring(ndx, start));
			buffer.append(newN);
			replacements--;
			ndx = start + oldLength;
		}
		//If the tag was accurately used, replacements should be 0, if not
		//put that number of close tags on the end.
		while (replacements > 0) {
			replacements--;
			buffer.append(newN);
		}
		return buffer.toString();
	}

	/**
	 * Gets the list of shackTags.  I added this so that i can show them in the shackTagHelper js function
	 * @return
	 */
	public List<Shacktag> getShacktags() {
		return shacktags;
	}
}
