package org.ttdc.util;

import org.apache.log4j.Logger;
import org.junit.Test;
import junit.framework.TestCase;


public class SendEmailTest extends TestCase {
	private static Logger log = Logger.getLogger(SendEmailTest.class);
	@Test
	public final void testSendMail() {
		String html = "<html><body><table border=2><tr><td>Test</td><td>test2</td></tr></table></body></html>";
		String url = "http://www.google.com";
		String text = "just some text";
		
		try {
			SendGmail.sendMail(html, SendGmail.ContentType.HTML, "This is some HTML", "ttdc.trevisthomas.com",
					"trevisthomas@gmail.com");
			assertTrue("Send HTML worked",true);
		} catch (BizException e) {
			log.error(e);
			assertFalse(e.getMessage(),true);
		}
		try {
			SendGmail.sendMail(url, SendGmail.ContentType.URL, "This is a URL", "ttdc.trevisthomas.com",
					"trevisthomas@gmail.com");
			assertTrue("Send URL: "+url+" worked",true);
		} catch (BizException e) {
			assertFalse(e.getMessage(),true);
		}

		try {
			SendGmail.sendMail(text, SendGmail.ContentType.TEXT, "This is a some text", "ttdc.trevisthomas.com",
					"trevisthomas@gmail.com");
			assertTrue("Send TEXT worked",true);
		} catch (BizException e) {
			assertFalse(e.getMessage(),true);
		}

	}
}
