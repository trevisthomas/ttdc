package org.ttdc.gwt.client.uibinder.post;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class MagicNestedSpacerTest {
	@Test
	public void testMagic(){
		MagicNestedSpacer magic = new MagicNestedSpacer();
		List<String> results = magic.decisionEngine(new int[]{3,4,1,5}, new int[]{2,4,0});
		
		assertEquals(MagicNestedSpacer.CONTINUE, results.get(0));
		assertEquals(MagicNestedSpacer.BLANK, results.get(1));
		assertEquals(MagicNestedSpacer.NODE, results.get(2));
				
	}
	
	@Test
	public void testMagic2(){
		MagicNestedSpacer magic = new MagicNestedSpacer();
		List<String> results = magic.decisionEngine(new int[]{3,4,1,5}, new int[]{3,4,0});
		
		assertEquals(MagicNestedSpacer.BLANK, results.get(0));
		assertEquals(MagicNestedSpacer.BLANK, results.get(1));
		assertEquals(MagicNestedSpacer.NODE, results.get(2));
				
	}
	
}
