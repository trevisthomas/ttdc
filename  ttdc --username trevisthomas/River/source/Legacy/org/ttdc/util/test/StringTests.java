package org.ttdc.util.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.ttdc.util.ShackTagger;

public class StringTests {
	
	@Test public void shackTagTester(){
		String msg = "Trevir{s}r loves the http://google.com|\"google\" doesn't he?";
		
		msg = ShackTagger.getInstance().shackTagThis(msg);
		assertTrue(msg.length() > 0); //Lame i know but...
		
	}
}
