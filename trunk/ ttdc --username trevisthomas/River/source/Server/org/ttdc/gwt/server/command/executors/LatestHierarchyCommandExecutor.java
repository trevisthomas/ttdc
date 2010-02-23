package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.Inflatinator;
import org.ttdc.gwt.server.dao.LatestPostsHierarchyDao;
import org.ttdc.gwt.shared.commands.results.PostListCommandResult;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;

public class LatestHierarchyCommandExecutor extends CommandExecutor<PostListCommandResult> {
	private final static Logger log = Logger.getLogger(LatestHierarchyCommandExecutor.class);
	@Override
	public CommandResult execute() {
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		log.debug("execute()");
		
		try{
			Person person = getPerson();
			beginSession();
			LatestPostsHierarchyDao dao = new LatestPostsHierarchyDao();
			
			dao.setFilteredTagIdList(person.getFrontPageFilteredTagIds());
			//dao.setThreadCount(20);//TODO: Make a new value for this trevis!
			List<Post> list = dao.load();
			
			Inflatinator inf = new Inflatinator(list);
			
			ArrayList<GPost> gPostList = (ArrayList<GPost>)inf.extractPostHierarchy();
			
			commit();
			return new PostListCommandResult(gPostList);
		}
		catch(Exception e){
			rollback();
			throw new RuntimeException(e); 
		}
		finally{
			stopwatch.stop();
			log.debug("execute() completed in "+stopwatch);
		}
	}
}

