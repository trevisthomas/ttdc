package org.ttdc.gwt.server.command.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.PaginatedResultConverters;
import org.ttdc.gwt.server.dao.MovieDao;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;

public class PersonListCommandExecutor extends CommandExecutor<PersonListCommandResult>{
	@Override
	protected CommandResult execute() {
		
		PersonListCommand cmd = (PersonListCommand)getCommand();
		PersonListCommandResult results = null;
		try{
			Persistence.beginSession();
			switch(cmd.getType()){
				case MOVIE_REVIEWERS:
					results = loadMovieReviewers();
					break;
				case ALL:
					results = loadUsers(false, cmd);
					break;
				case ACTIVE:
					results = loadUsers(true, cmd);
					break;
				default:
					throw new RuntimeException("PersonListCommandExecutor has no idea what to do");					
			}
		}
		finally{
			Persistence.commit();
		}
		
		return results;
	}
	
	private PersonListCommandResult loadUsers(boolean activeOnly, PersonListCommand cmd) {
		PersonDao dao = new PersonDao();
		dao.setActiveOnly(activeOnly);
		dao.setCurrentPage(cmd.getCurrentPage());
		if(cmd.getPageSize() > 0)
			dao.setPageSize(cmd.getPageSize());
		dao.setSortDirection(cmd.getSortDirection());
		dao.setSortBy(cmd.getSortOrder());
		PaginatedList<Person> results = dao.load();
		PaginatedList<GPerson> gResults;
		if(cmd.isLoadFullDetails()){
			gResults = PaginatedResultConverters.convertPersonListWithFullDetails(results);
		}
		else{
			gResults = PaginatedResultConverters.convertPersonListWithPriviledges(results);
		}
		return new PersonListCommandResult(gResults);
	}
	
		
	private PersonListCommandResult loadMovieReviewers() {
		PersonListCommandResult results;
		List<GPerson> gPersonList = new ArrayList<GPerson>();
		MovieDao movieDao = new MovieDao();
		Map<String,Long> map = movieDao.loadMovieRaters();
		
		List<Person> people = PersonDao.loadPeople(map.keySet());
		GPerson gPerson;
		for(Person p : people){
			gPerson = FastPostBeanConverter.convertPerson(p);
			gPerson.setValue(map.get(p.getPersonId()).toString());
			gPersonList.add(gPerson);
		}
		results = new PersonListCommandResult(gPersonList);
		return results;
	}
}
