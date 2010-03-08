package org.ttdc.gwt.server.command.executors.utils;

import java.util.HashSet;
import java.util.Set;

import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.util.PostFlag;

public class ExecutorHelpers {
	public static Set<PostFlag> createFlagFilterListForPerson(Person person) {
		Set<PostFlag> flagList = new HashSet<PostFlag>();
		if(!person.isNwsEnabled()){
			flagList.add(PostFlag.INF);
		}
		if(!person.isPrivateAccessAccount()){
			flagList.add(PostFlag.PRIVATE);
		}
		return flagList;
	}
}
