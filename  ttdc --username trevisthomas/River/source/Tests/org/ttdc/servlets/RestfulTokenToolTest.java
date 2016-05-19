package org.ttdc.servlets;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class RestfulTokenToolTest {
	@Test
	public void tokenTest() {
		RestfulToken token = new RestfulToken("testname");

		String t;
		try {
			t = RestfulTokenTool.toTokenString(token);

			RestfulToken t2 = RestfulTokenTool.fromTokenString(t);

			assertEquals(token.getPersonId(), t2.getPersonId());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
