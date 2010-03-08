package org.ttdc.persistence.migration;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;
import static org.ttdc.persistence.Persistence.session;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.ttdc.gwt.server.dao.AssociationPostTagDao;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class GenerateWeekOfYearTags {
	private final static Logger log = Logger.getLogger(GenerateWeekOfYearTags.class);
	private final String personId = "3D9871D7-4889-41D1-9E7C-69351C8D022E";//admin
	
	Session session;
	public static void main(String[] args) {
		GenerateWeekOfYearTags avgrate = new GenerateWeekOfYearTags();
		avgrate.go();
	}
	
	public void go(){
		throw new RuntimeException("I dont use week of year tags anymore");
//		beginSession();
//		try{
//			start();
//			beginSession();
//			Person creator = PersonDao.loadPerson(personId);
//			
//			List<Post> posts = session().createCriteria(Post.class).list();
//			
//			for(Post post : posts){
//				Date d = post.getDate();
//				
//				Calendar cal = GregorianCalendar.getInstance();
//				cal.setTime(d);
//				
//				int moy = cal.get(Calendar.WEEK_OF_YEAR);
//				Tag t = tagmap.get(moy);
//				
//				if(t == null ){
//					TagDao tagDao = new TagDao();
//					tagDao.setCreator(creator);
//					tagDao.setDescription("");
//					tagDao.setType(Tag.TYPE_WEEK_OF_YEAR);
//					tagDao.setValue(""+moy);
//					 t = tagDao.createOrLoad();
//					 tagmap.put(moy, t);
//				}
//				
//				AssociationPostTag ass = new AssociationPostTag();
//				ass.setCreator(creator);
//				ass.setTag(t);
//				ass.setPost(post);
//				session().save(ass);
//				
//			}
//			commit();
//		}
//		catch (Exception e) {
//			log.error(e);
//			rollback();
//		}
//		finally{
//			end();
//		}
		
	}
	
	private Map<Integer,Tag> tagmap = new HashMap<Integer,Tag>();
//	private Tag[] tags = new Tag[54];
//	private void makeAllTags(){
//		Person creator = PersonDao.loadPerson(personId);
//		
//		for(int i = 0;i<53;i++){
//			TagDao tagDao = new TagDao();
//			tagDao.setCreator(creator);
//			tagDao.setDescription("");
//			tagDao.setType(Tag.TYPE_WEEK_OF_YEAR);
//			tagDao.setValue(""+i);
//			Tag t = tagDao.createOrLoad();
//			tags[i] = t;
//		
//		}
//	}	
	
	StopWatch stopwatch;

	public void start(){
		stopwatch = new StopWatch();
		stopwatch.start();
	}
	public void end(){
		
		stopwatch.stop();
		log.info("Time taken: "+stopwatch.toString());
		log.info("ass count: "+AssociationPostTag.iCount);
		log.info("post count: "+Post.iCount);
	}
}
