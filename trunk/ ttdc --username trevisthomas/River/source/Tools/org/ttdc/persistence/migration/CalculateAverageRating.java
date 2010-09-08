package org.ttdc.persistence.migration;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;
import static org.ttdc.persistence.Persistence.session;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;
import org.ttdc.util.RatingUtility;

public class CalculateAverageRating {
	private final static Logger log = Logger.getLogger(CalculateAverageRating.class);
	
	Session session;
	public static void main(String[] args) {
		CalculateAverageRating avgrate = new CalculateAverageRating();
		avgrate.go();
	}
	
	public void go(){
		beginSession();
		try{
			start();
			List<Post> posts = session().createQuery("Select p FROM Post p WHERE bitwise_and(metaMask , 16) = 16").list();
			
			for(Post post : posts){
				RatingUtility.updateAverageRating(post);
			}
			commit();

		}
		catch (Exception e) {
			log.error(e);
			rollback();
		}
		finally{
			end();
		}
		
	}

	
	
	Date start;
	Date end;

	public void start(){
		start = new Date();
	}
	public void end(){
		end = new Date();
		log.info("Time taken: "+(end.getTime() - start.getTime())/1000.0);
		log.info("ass count: "+AssociationPostTag.iCount);
		log.info("post count: "+Post.iCount);
	}
}
