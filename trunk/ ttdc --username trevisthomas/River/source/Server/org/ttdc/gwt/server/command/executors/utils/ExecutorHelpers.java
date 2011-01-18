package org.ttdc.gwt.server.command.executors.utils;

import java.util.HashSet;
import java.util.Set;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.shared.util.PostFlag;
import org.ttdc.persistence.objects.Person;

public class ExecutorHelpers {
	public static Set<PostFlag> createFlagFilterListForPerson(Person person) {
		Set<PostFlag> flagList = new HashSet<PostFlag>();
		if(!person.isNwsEnabled()){
			flagList.add(PostFlag.NWS);
		}
		if(!person.isPrivateAccessAccount()){
			flagList.add(PostFlag.PRIVATE);
		}
		if(!person.isAdministrator()){
			flagList.add(PostFlag.DELETED);
		}
		return flagList;
	}
	
	public static void broadcastMarkSiteRead(Person person, String connectionId) {
		GPerson gPerson = FastPostBeanConverter.convertPerson(person);
		PersonEvent event = new PersonEvent(PersonEventType.USER_MARK_SITE_READ, gPerson);
		ServerEventBroadcaster.getInstance().broadcastEvent(event, connectionId);
	}
}
