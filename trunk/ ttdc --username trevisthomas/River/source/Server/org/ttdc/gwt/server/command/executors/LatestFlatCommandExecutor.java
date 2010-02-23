package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.Inflatinator;
import org.ttdc.gwt.server.dao.LatestPostsFlatDao;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.shared.commands.results.PostListCommandResult;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;

public class LatestFlatCommandExecutor extends CommandExecutor<PostListCommandResult> {
	public CommandResult execute() {
		try{
			beginSession();
			Person person = PersonDao.loadPerson(getPerson().getPersonId());
			//Person person = getPerson();
			LatestPostsFlatDao dao = new LatestPostsFlatDao();
			
			dao.setFilteredTagIdList(person.getFrontPageFilteredTagIds());
			dao.setPageSize(person.getNumCommentsFrontpage());
			
			List<Post> list = dao.load();
			
			Inflatinator inf = new Inflatinator(list);
			
			ArrayList<GPost> gPostList = (ArrayList<GPost>)inf.extractPosts();
			
			commit();
			return new PostListCommandResult(gPostList);
		}
		catch(Exception e){
			rollback();
			throw new RuntimeException(e); 
		}
	}
}
