package org.ttdc.servlets;

import org.junit.Test;

public class RssFeedGeneratorTest {
	@Test
	public void testLatestRss(){
		RssFeedGenerator.buildFeed(System.out, "");	
	}
	
	@Test
	public void testTopicRss(){
		RssFeedGenerator.buildFeed(System.out, "rss=topic&postId=F534A2E2-F782-4740-ACDE-F785ECF1B4F9");
	}
	
}
