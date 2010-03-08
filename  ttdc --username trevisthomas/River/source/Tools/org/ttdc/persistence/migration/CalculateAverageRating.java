package org.ttdc.persistence.migration;

import java.util.Date;
import java.util.List;

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

import static org.ttdc.persistence.Persistence.*;

public class CalculateAverageRating {
	private final static Logger log = Logger.getLogger(TagDao.class);
	private final String personId = "3D9871D7-4889-41D1-9E7C-69351C8D022E";//admin
	private final String tagIdMovie = "E9ECF7AF-6406-4BDE-A396-145CE256ABD2";

	Session session;
	public static void main(String[] args) {
		CalculateAverageRating avgrate = new CalculateAverageRating();
		avgrate.go();
	}
	
	public void go(){
		beginSession();
		try{
			start();
			Person creator = PersonDao.loadPerson(personId);
			
			List<AssociationPostTag> asses = session().createCriteria(AssociationPostTag.class)
				.add(Restrictions.eq("tag.tagId",tagIdMovie)).list();
			
			for(AssociationPostTag ass : asses){
				String avg = ass.getPost().getAverageRating();
				
				
				TagDao tagDao = new TagDao();
				tagDao.setDescription("");
				tagDao.setType(Tag.TYPE_AVERAGE_RATING);
				tagDao.setValue(avg);
				Tag t = tagDao.createOrLoad();
				
				AssociationPostTagDao assDao = new AssociationPostTagDao();
				assDao.setCreator(creator);
				assDao.setPost(ass.getPost());
				assDao.setTag(t);
				assDao.create();
				
				session().flush();
				
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
