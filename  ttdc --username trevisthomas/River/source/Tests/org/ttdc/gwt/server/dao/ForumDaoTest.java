package org.ttdc.gwt.server.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ttdc.gwt.server.dao.Helpers.assertTagged;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Forum;
import org.ttdc.persistence.objects.Post;

//GenericListCommandResult

public class ForumDaoTest {
	private final static Logger log = Logger.getLogger(ForumDaoTest.class);
	@Test
	public void getForumTagList() {
		try {
			Persistence.beginSession();

			ForumDao dao = new ForumDao();
			List<Forum> forums = dao.loadForums();

			assertTrue(forums.size() > 10);

		} 
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Persistence.rollback();
		}
	}
	
	@Test
	public void getPostsInForum(){
		try{
			beginSession();
			ForumDao dao = new ForumDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			
			List<Forum> forums = dao.loadForums();
			Forum forum = forums.get(0);
			
			dao.setForumId(forum.getTagId());
			
			PaginatedList<Post> results = dao.loadTopics();
			
			Helpers.printResults(results,log);
			
			
			//assertTagged(results.getList(), tagTrevis);
			assertTagged(results.getList(), forum.getTagId());
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
}
