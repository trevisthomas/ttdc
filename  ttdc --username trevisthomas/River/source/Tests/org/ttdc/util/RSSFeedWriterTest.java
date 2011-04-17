package org.ttdc.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.ttdc.util.rss.Feed;
import org.ttdc.util.rss.FeedMessage;
import org.ttdc.util.rss.RSSFeedWriter;

public class RSSFeedWriterTest {

	@Test
	public void testFeed() {
		// Create the rss feed
		String copyright = "Copyright hold by Lars Vogel";
		String title = "Eclipse and Java Information";
		String description = "Eclipse and Java Information";
		String language = "en";
		String link = "http://www.vogella.de";
		Calendar cal = new GregorianCalendar();
		Date creationDate = cal.getTime();
		
		Feed rssFeeder = new Feed(title, link, description, language,
				copyright, creationDate);

		// Now add one example entry
		FeedMessage feed = new FeedMessage();
		feed.setTitle("RSSFeed");
		feed.setDescription("This is a description");
		feed.setAuthor("nonsense@somewhere.de (Lars Vogel)");
		feed.setGuid("http://www.vogella.de/articles/RSSFeed/article.html");
		feed.setLink("http://www.vogella.de/articles/RSSFeed/article.html");
		rssFeeder.getMessages().add(feed);

		// Now write the file
		try {
			RSSFeedWriter writer = new RSSFeedWriter(rssFeeder, "test_articles.rss");
			writer.write();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
