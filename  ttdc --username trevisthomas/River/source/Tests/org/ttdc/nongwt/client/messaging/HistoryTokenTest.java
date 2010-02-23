package org.ttdc.nongwt.client.messaging;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

public class HistoryTokenTest {
	private final String name = "personId";
	private final String value = "3325CE14-A37E-4236-875C-F1D97F006682";
	private final String name2 = "test";
	private final String value2 = "123345";
	
	@Test
	public void basicTest(){
		HistoryToken token = new HistoryToken();
		
		token.setParameter(name, value);
		
		assertEquals(value, token.getParameter(name));
		assertEquals("personId=3325CE14-A37E-4236-875C-F1D97F006682",token.toString());
	}
	@Test
	public void withTwoParametersTest(){
		HistoryToken token = new HistoryToken();
		
		token.setParameter(name, value);
		token.setParameter(name2, value2);
		
		assertEquals(value, token.getParameter(name));
		assertEquals(value2, token.getParameter(name2));
		assertEquals(name+"="+value+"&"+name2+"="+value2,token.toString());
	}
	
	
	/**
	 * You may want to mod the guy to carry a list. This functionality is a bit weird 
	 * but i did it as comma separated just to get another test passing
	 */
	@Test
	public void withDuplicateKeysParametersTest(){
		HistoryToken token = new HistoryToken();
		
		token.addParameter(name, value);
		token.addParameter(name, value2);
		
		assertEquals(value+","+value2, token.getParameter(name));
		assertEquals(name+"="+value+","+value2,token.toString());
		List<String> list = token.getParameterList(name);
		assertEquals(2, list.size());
	}
	

	
	@Test
	public void fromStringTest(){
		String str = name+"="+value+"&"+name2+"="+value2;
		HistoryToken token = new HistoryToken(str);
		
		assertEquals(value, token.getParameter(name));
		assertEquals(value2, token.getParameter(name2));
	}
	
	@Test
	public void blankQueryStringTest(){
		String str = "";
		HistoryToken token = new HistoryToken(str);
		
		assertNotNull(token);
	}
	@Test
	public void blankInvalidQueryString(){
		try
		{
			String str = "name_no_val";
			HistoryToken token = new HistoryToken(str);
			
			assertNotNull(token);
			fail("Should have thrown an exception");
		}
		catch(Exception e){
			assertEquals("Invalid Query String", e.getMessage());
		}
	}
	
	@Test
	public void hasToken(){
		HistoryToken token = new HistoryToken();
		
		token.setParameter(name, value);
		token.setParameter(name, value2);
		
		assertTrue(token.hasParameter(name));
		assertFalse(token.hasParameter("not there"));
	}
	
	@Test
	public void removeToken(){
		HistoryToken token = new HistoryToken();
		
		token.setParameter(name, value);
		token.setParameter(name, value2);
		
		token.removeParameter(name);
		
		assertFalse(token.hasParameter(name));
		
	}
	
	@Test
	public void checkParameter(){
		HistoryToken token = new HistoryToken();
		token.setParameter(name, value);
		assertTrue(token.isParameterEq(name,value));
		assertFalse(token.isParameterEq(name,value2));
	}
	
}
