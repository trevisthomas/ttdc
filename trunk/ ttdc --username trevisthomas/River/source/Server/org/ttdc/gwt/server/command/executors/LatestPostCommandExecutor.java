package org.ttdc.gwt.server.command.executors;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.PaginatedResultConverters;
import org.ttdc.gwt.server.dao.EarmarkedPostDao;
import org.ttdc.gwt.server.dao.LatestPostsDao;
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
				result = loadConversations();
				break;
			case LATEST_FLAT:
				result = loadFlat();
				break;
			case LATEST_NESTED:
				result = loadNested();
				break;
			case LATEST_THREADS:
				result = loadThreads();
				break;
			case LATEST_EARMARKS:
				result = loadEarmarks();
				break;
			default:
				throw new RuntimeException("LatestPostCommandExecutor doesnt understand that action type");
			}
			Persistence.commit();
		}
		catch (RuntimeException e) {
			Persistence.rollback();
			throw e;
		}
		return result;
	}

	

	private PaginatedListCommandResult<GPost> loadThreads() {
		LatestPostsDao dao = getLatestPostDaoWithPersonalFilter();
		PaginatedList<Post> results = dao.loadThreads();
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
		return new PaginatedListCommandResult<GPost>(gResults);
	}

	private LatestPostsDao getLatestPostDaoWithPersonalFilter() {
		LatestPostsDao dao = new LatestPostsDao();
		Person p = getPerson();
		if(!p.isNwsEnabled()){
			dao.addFlagFilter(PostFlag.NWS);
		}
		
		if(!p.isPrivateAccessAccount()){
			dao.addFlagFilter(PostFlag.PRIVATE);
		}
		
		dao.addFilterThreadIds(p.getFrontPageFilteredThreadIds());
		return dao;
	}

	private PaginatedListCommandResult<GPost> loadEarmarks() {
		Person person = getPerson();
		if(person.isAnonymous())
			throw new RuntimeException("Anonymous users don't have ear marks.");
		
		EarmarkedPostDao dao = new EarmarkedPostDao();
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
	
	private PaginatedListCommandResult<GPost> loadNested() {
		LatestPostsDao dao = getLatestPostDaoWithPersonalFilter();
		PaginatedList<Post> results = dao.loadNested();
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResultsNested(results, getPerson());
		return new PaginatedListCommandResult<GPost>(gResults);
	}

	private PaginatedListCommandResult<GPost> loadFlat() {
		LatestPostsDao dao = getLatestPostDaoWithPersonalFilter();
		PaginatedList<Post> results = dao.loadFlat();
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResults(results, getPerson());
		return new PaginatedListCommandResult<GPost>(gResults);
	}

	private PaginatedListCommandResult<GPost> loadConversations() {
		LatestPostsDao dao = getLatestPostDaoWithPersonalFilter();
		PaginatedList<Post> results = dao.loadConversations();
		PaginatedList<GPost> gResults = PaginatedResultConverters.convertSearchResultsNested(results, getPerson());
		return new PaginatedListCommandResult<GPost>(gResults);
	}
	
}