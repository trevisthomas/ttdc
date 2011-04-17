package org.ttdc.util.rss;

import java.text.ParseException;
import java.util.Date;

/*
 * Represents one RSS message
 * 
 * http://www.vogella.de/articles/RSSFeed/article.html
 */
public class FeedMessage {

	private String title;
	private String description;
	private String link; 
    private String author;
    private String guid;
    private Date pubDate;
    
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public Date getPubDate() {
		return pubDate;
	}
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
	public void setPubDate(String pubDateString) throws ParseException {
		setPubDate(Feed.rssDateFormatter.parse(pubDateString));
	}
	public String getPubDateString() {
		return Feed.rssDateFormatter.format(pubDate);
	}
	public String toString() {
		return "Feed [title=" + title + ", description=" + description + ", link=" + link 
				+ ", author=" + author + ", link=" + link + ", pubDate="
				+ getPubDateString() + ", guid=" + guid + "]";
	}
		
}