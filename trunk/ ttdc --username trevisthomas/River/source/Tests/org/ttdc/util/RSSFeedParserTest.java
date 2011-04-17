package org.ttdc.util;

import org.junit.Test;
import org.ttdc.util.rss.Feed;
import org.ttdc.util.rss.FeedMessage;
import org.ttdc.util.rss.RSSFeedParser;

public class RSSFeedParserTest {
	@Test
	public void parseFoxNewsTest(){
		RSSFeedParser parser = new RSSFeedParser("http://feeds.foxnews.com/foxnews/latest");
		Feed feed = parser.readFeed();
		System.out.println(feed);
		for (FeedMessage message : feed.getMessages()) {
			System.out.println(message);
			
		}
	}
}
