package org.ttdc.gwt.client.beans;

public class PersonBeanMother {
	public static GPerson createPerson1(){
		GPerson p = new GPerson();
		p.setAnonymous(false);
		p.setLogin("trevis");
		p.setName("trevis thomas");
		p.setEmail("trevisthomas@gmail.com");
		p.setPersonId("123456");
		return p;
	}
}
