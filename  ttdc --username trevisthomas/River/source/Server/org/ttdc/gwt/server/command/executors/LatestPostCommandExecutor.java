package org.ttdc.gwt.server.command.executors;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.ExecutorHelpers;
import org.ttdc.gwt.server.command.executors.utils.PaginatedResultConverters;
import org.ttdc.gwt.server.dao.AccountDao;
import org.ttdc.gwt.server.dao.EarmarkedPostDao;
import org.ttdc.gwt.server.dao.InboxDao;
import org.ttdc.gwt.server.dao.LatestPostsDao;
import org.ttdc.gwt.server.dao.FastLatestPostsDao;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.PostFlag;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class LatestPostCommandExecutor extends CommandExecutor<PaginatedListCommandResult<GPost>>{
	
	@Override
	protected CommandResult execute() {
		LatestPostsCommand cmd = (LatestPostsCommand)getCommand();
		PaginatedListCommandResult<GPost> result;
		
		try{
			Persistence.beginSession();
			switch (cmd.getAction()) {
			case LATEST_CONVERSATIONS:
				result = loadConversations(cmd);
				break;
			case LATEST_FLAT:
				result = loadFlatFuckHibernate(cmd);
				break;
			case LATEST_NESTED:
				result = loadNested(cmd);
				break;
			case LATEST_THREADS:
				result = loadThreads(cmd);
				break;
			case LATEST_EARMARKS:
				result = loadEarmarks(cmd);
				break;
			case LATEST_GROUPED:
				result = loadGroupedFuckHibernate(cmd);	
				break;
			default:
				throw new RuntimeException("LatestPostCommandExecutor doesnt understand that action type");
			}
			incrementUserHitCount();
			Persistence.commit();
		}
		catch (RuntimeException e) {
			Persistence.rollback();
			throw e;
		}
		return result;
	}

	
	/**
	 * This execute method is being added so that my new restful webservices can perform this command.
	 * @param cmd
	 * @return
	 */
	public PaginatedListCommandResult<GPost> execute(LatestPostsCommand cmd){
		PaginatedListCommandResult<GPost> result;
		
		try{
			Persistence.beginSession();
			switch (cmd.getAction()) {
			case LATEST_CONVERSATIONS:
				result = loadConversations(cmd);
				break;
			case LATEST_FLAT:
				result = loadFlatFuckHibernate(cmd);
				break;
			case LATEST_NESTED:
				result = loadNested(cmd);
				break;
			case LATEST_THREADS:
				result = loadThreads(cmd);
				break;
			case LATEST_EARMARKS:
				result = loadEarmarks(cmd);
				break;
			case LATEST_GROUPED:
				result = loadGroupedFuckHibernate(cmd);	
				break;
			default:
				throw new RuntimeException("LatestPostCommandExecutor doesnt understand that action type");
			}
			incrementUserHitCount();
			Persistence.commit();
		}
		catch (RuntimeException e) {
			Persistence.rollback();
			throw e;
		}
		return result;
	}

	private PaginatedListCommandResult<GPost> loadThreads(LatestPostsCommand cmd) {
		LatestPostsDao dao = getLatestPostDaoWithPersonalFilter(cmd);
		PaginatedList<Post> results = dao.loadThreads();
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
		return new PaginatedListCommandResult<GPost>(gResults);
	}

	private LatestPostsDao getLatestPostDaoWithPersonalFilter(LatestPostsCommand cmd) {
		LatestPostsDao dao = new LatestPostsDao();
		dao.setCurrentPage(cmd.getPageNumber());
		Person p = getPerson();
		dao.setFilterFlags(ExecutorHelpers.createFlagFilterListForPerson(p));
		dao.addFilterThreadIds(p.getFrontPageFilteredThreadIds());
		return dao;
	}
	
	private FastLatestPostsDao getLatestPostDaoWithPersonalFilterFuckHibernate(LatestPostsCommand cmd) {
		FastLatestPostsDao dao = new FastLatestPostsDao();
		dao.setCurrentPage(cmd.getPageNumber());
		Person p = getPerson();
		InboxDao inboxDao = new InboxDao(p);
		dao.setFilterFlags(ExecutorHelpers.createFlagFilterListForPerson(p));
		dao.addFilterThreadIds(p.getFrontPageFilteredThreadIds());
		dao.setInboxDao(inboxDao);
		return dao;
	}

	private PaginatedListCommandResult<GPost> loadEarmarks(LatestPostsCommand cmd) {
		Person person = getPerson();
		if(person.isAnonymous())
			throw new RuntimeException("Anonymous users don't have ear marks.");
		
		EarmarkedPostDao dao = new EarmarkedPostDao();
		dao.setCurrentPage(cmd.getPageNumber());
		dao.setPersonId(person.getPersonId());
		
		TagDao tagDao = new TagDao();
		tagDao.setValue(person.getPersonId());
		tagDao.setType(org.ttdc.gwt.client.constants.TagConstants.TYPE_EARMARK);
		Tag tag = tagDao.load();
		
		PaginatedList<GPost> gResults = null;
		if(tag == null){
			gResults = new PaginatedList<GPost>();
		}
		else{
			dao.setTagId(tag.getTagId());
			PaginatedList<Post> results = dao.loadEarmarkedPosts();
			gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
		}
		return new PaginatedListCommandResult<GPost>(gResults);
	}
	
	private PaginatedListCommandResult<GPost> loadNested(LatestPostsCommand cmd) {
		LatestPostsDao dao = getLatestPostDaoWithPersonalFilter(cmd);
		PaginatedList<Post> results = dao.loadNested();
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResultsNested(results, getPerson());
		return new PaginatedListCommandResult<GPost>(gResults);
	}
	
	private PaginatedListCommandResult<GPost> loadGrouped(LatestPostsCommand cmd) {
		LatestPostsDao dao = getLatestPostDaoWithPersonalFilter(cmd);
		
		PaginatedList<Post> results = dao.loadGrouped();
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResultsNested(results, getPerson());
		return new PaginatedListCommandResult<GPost>(gResults);
	}
	
	private PaginatedListCommandResult<GPost> loadGroupedFuckHibernate(LatestPostsCommand cmd) {
//		LatestPostsDao dao = getLatestPostDaoWithPersonalFilter(cmd);
//		
//		PaginatedList<Post> results = dao.loadGrouped();
//		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResultsNested(results, getPerson());
//		return new PaginatedListCommandResult<GPost>(gResults);
		
		FastLatestPostsDao dao = getLatestPostDaoWithPersonalFilterFuckHibernate(cmd);
		PaginatedList<GPost> gResults = dao.loadGrouped();
		return new PaginatedListCommandResult<GPost>(gResults);
	}
	
	private PaginatedListCommandResult<GPost> loadFlat(LatestPostsCommand cmd) {
		LatestPostsDao dao = getLatestPostDaoWithPersonalFilter(cmd);
		PaginatedList<Post> results = dao.loadFlat();
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
		return new PaginatedListCommandResult<GPost>(gResults);
	}
	
	private PaginatedListCommandResult<GPost> loadFlatFuckHibernate(LatestPostsCommand cmd) {
		FastLatestPostsDao dao = getLatestPostDaoWithPersonalFilterFuckHibernate(cmd);
		PaginatedList<GPost> gResults = dao.loadFlat();
		return new PaginatedListCommandResult<GPost>(gResults);
	}

	private PaginatedListCommandResult<GPost> loadConversations(LatestPostsCommand cmd) {
		LatestPostsDao dao = getLatestPostDaoWithPersonalFilter(cmd);
		PaginatedList<Post> results = dao.loadConversations();
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResultsNested(results, getPerson());
		return new PaginatedListCommandResult<GPost>(gResults);
	}
	
}
