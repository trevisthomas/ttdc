package org.ttdc.struts.network.common;

import org.ttdc.persistence.objects.Person;

public interface SecurityAware {
	public void setPerson(Person user);
}
