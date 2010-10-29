package org.ttdc.gwt.server.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class PostFormatterTest {
	@Test
	public void testFormatForLink(){
		
		String s1 = "Anyone here use http://link.com|\"QR Code\" after";
		String s1a = "Anyone here use <a target=\"_blank\" href=\"http://link.com\">QR Code</a> after";
		String s2 = "https://link/|\"show\"";
		String s2a = "<a target=\"_blank\" href=\"https://link/\">show</a>";
		
		String s3 = "https://link/";
		String s3a = "<a target=\"_blank\" href=\"https://link/\">https://link/...</a>";
		
		String s5 = "https://link";
		String s5a = "<a target=\"_blank\" href=\"https://link\">https://link</a>";
		
		String s4 = "<a href=\"http://link/\">cust</a>";
		String s4a = "<a href=\"http://link/\">cust</a>";
		
		
		
		String s1q = PostFormatter.getInstance().format(s1);
		String s2q = PostFormatter.getInstance().format(s2);
		
		assertEquals(s4a, PostFormatter.getInstance().format(s4));
		
		assertEquals(s1a, s1q);
		assertEquals(s2a, s2q);
		assertEquals(s3a, PostFormatter.getInstance().format(s3));
		
		assertEquals(s5a, PostFormatter.getInstance().format(s5));
		
		String s6 = "<embed src=\"http://vid\">";
		String s6a = "<embed src=\"http://vid\">";
		assertEquals(s6a, PostFormatter.getInstance().format(s6));
		
	}
	
	@Test
	public void testFormatTagMisMatching(){
		
		String s1 = "Anyone q[here]q";
		String s1a = "Anyone <div class=shackTag_q>here</div>";
		String s2 = "q[Anyone]s here";
		String s2a = "<div class=shackTag_q>Anyone]s here</div>";
			
		
		
		assertEquals(s1a, PostFormatter.getInstance().format(s1));
		assertEquals(s2a, PostFormatter.getInstance().format(s2));
		
	}
}


