package org.ttdc.util.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringUtils;

public class RSSFeedParser {
	static final String TITLE = "title";
	static final String DESCRIPTION = "description";
	static final String CHANNEL = "channel";
	static final String LANGUAGE = "language";
	static final String COPYRIGHT = "copyright";
	static final String LINK = "link";
	static final String AUTHOR = "author";
	static final String ITEM = "item";
	static final String PUB_DATE = "pubDate";
	static final String GUID = "guid";

	final URL url;

	public RSSFeedParser(String feedUrl) {
		try {
			this.url = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public Feed readFeed() {
		Feed feed=null;
		try {
		
			boolean isFeedHeader = true;
			// Set header values intial to the empty string
			String description="";
			String title="";
			String link="";
			String language="";
			String copyright ="";
			String author = "";
			String pubdate ="";
			String guid ="";

			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = read();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document
			while (eventReader.hasNext()) {

				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					final String nodeName = event.asStartElement().getName().getLocalPart();
					if (nodeName == (ITEM)) {
						if (isFeedHeader) {
							isFeedHeader = false;
							feed = new Feed(title, link, description,
									language, copyright, pubdate);
						}
						event = eventReader.nextEvent();
						continue;
					}

					if (nodeName == (TITLE)) {
						event = eventReader.nextEvent();
						title = event.asCharacters().getData(); 
						continue;
					}
					if (nodeName == (DESCRIPTION)) {
						event = eventReader.nextEvent();
						description= event.asCharacters().getData(); 
						continue;
					}

					if (nodeName == (LINK)) {
						event = eventReader.nextEvent();
						if(event.isCharacters()){
							link = event.asCharacters().getData();
						}
						else{
							//This element is not what you think it should be
						}
						 
						continue;
					}

					if (nodeName == (GUID)) {
						event = eventReader.nextEvent();
						guid= event.asCharacters().getData(); 
						continue;
					}
					if (nodeName == (LANGUAGE)) {
						event = eventReader.nextEvent();
						language= event.asCharacters().getData(); 
						continue;
					}
					if (nodeName == (AUTHOR)) {
						event = eventReader.nextEvent();
						author= event.asCharacters().getData(); 
						continue;
					}
					if (nodeName == (PUB_DATE)) {
						event = eventReader.nextEvent();
						pubdate= event.asCharacters().getData(); 
						continue;
					}
					if (nodeName == (COPYRIGHT)) {
						event = eventReader.nextEvent();
						copyright= event.asCharacters().getData(); 
						continue;
					}
				} else if (event.isEndElement()) {
					if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
						FeedMessage message = new FeedMessage();
						message.setAuthor(author);
						message.setDescription(description);
						message.setGuid(guid);
						message.setLink(link);
						message.setTitle(title);
						if(!StringUtils.isEmpty(pubdate)){
							message.setPubDate(pubdate);
						}
						feed.getMessages().add(message);
						event = eventReader.nextEvent();
						continue;
					}
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return feed;

	}

	private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}