package org.ttdc.util.rss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/*
 * Stores an RSS feed
 * http://www.vogella.de/articles/RSSFeed/article.html
 */
public class Feed {
	
	private final String title;
	private final String link;
	private final String description;
	private final String language;
	private final String copyright;
	private final Date pubDate;
	private final List<FeedMessage> entries = new ArrayList<FeedMessage>();
	
	public final static SimpleDateFormat rssDateFormatter = new SimpleDateFormat(
			"EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
	
	public Feed(String title, String link, String description,
			String language, String copyright, Date pubDate) {
				this.title = title;
				this.link = link;
				this.description = description;
				this.language = language;
				this.copyright = copyright;
				this.pubDate = pubDate;
	}
	
	public Feed(String title, String link, String description,
			String language, String copyright, String pubDateString) throws ParseException {
		this(title, link, description, language, copyright, (StringUtils.isEmpty(pubDateString) ? null : rssDateFormatter.parse(pubDateString)));
	}

	public List<FeedMessage> getMessages() {
		return entries;
	}
	
	public void addFeedMessage(FeedMessage message){
		entries.add(message);
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getLanguage() {
		return language;
	}

	public String getCopyright() {
		return copyright;
	}

	public String getPubDateString() {
		if(pubDate != null)
			return rssDateFormatter.format(pubDate);
		else
			return "";
	}
	
	public Date getPubDate() {
		return pubDate;
	}

	@Override
	public String toString() {
		return "Feed [copyright=" + copyright + ", description=" + description
				+ ", language=" + language + ", link=" + link + ", pubDate="
				+ getPubDateString() + ", title=" + title + "]";
	}
	
	

}