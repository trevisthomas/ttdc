package org.ttdc.test.utils;

import junit.framework.Assert;

public class ThreadUtils {
	public static void delay() {
		delay(10);
	}
	public static void delay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Assert.assertNotNull(e);
		}
	}
	
}
