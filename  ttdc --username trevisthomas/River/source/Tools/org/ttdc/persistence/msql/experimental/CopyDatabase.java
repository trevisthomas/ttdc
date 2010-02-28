package org.ttdc.persistence.msql.experimental;

import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Post;

public class CopyDatabase {
	
	private static final Logger log = Logger.getLogger(CopyDatabase.class);
	
	
	public CopyDatabase() {
		
	}
	
	
	
//	private void copyAssociationPersonPrivilege(){
//		
//		@SuppressWarnings("unchecked")
//		List<ImageFull> list = Persistence.session().createQuery("FROM ImageFull").list();///i where i.imageId='07725998-F0FF-49FA-B226-00981624A392'
//		for(ImageFull src : list){
//			ImageExperiment dest = new ImageExperiment();
//			dest.setImage(src.getImage());
//			dest.setImageId(src.getImageId());
//			dest.setName(src.getName());
//			PersistenceMySql.session().save(dest);
//			PersistenceMySql.session().flush();
//			log.info("Name: "+src.getName());
//			
//			
//			mysql.save();
//			mysql.flush();
//		}
//		
//		log.info("copyAssociationPersonPrivilege: complete");
//		log.info(stopwatch.toSplitString());
//		
//	}
	
	private void copyAssociationPostTag(){
		PersistenceMySql.beginSession();
		Persistence.beginSession();
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		log.info("Starting associationPostTag copy");
		@SuppressWarnings("unchecked")
		List<AssociationPostTag> list = Persistence.session().createQuery("FROM AssociationPostTag").list();///i where i.imageId='07725998-F0FF-49FA-B226-00981624A392'
		int count = 0;
		for(AssociationPostTag src : list){
			
//			log.info("Name: "+src.getName());
			PersistenceMySql.session().save(src);
			if((count % 20000) == 0){
				log.info("flushing at count "+count);
			   	Persistence.session().flush();
			}
		}
		PersistenceMySql.commit();
		stopwatch.stop();
		log.info("copyAssociationPersonPrivilege: complete");
		log.info(stopwatch.toString());
		
	}
	
	private void copyPost(){
		PersistenceMySql.beginSession();
		Persistence.beginSession();
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		log.info("Starting associationPostTag copy");
		@SuppressWarnings("unchecked")
		//List<AssociationPostTag> list = Persistence.session().createQuery("FROM Post").list();
		List<Post> list = Persistence.session().createQuery("FROM Post p WHERE p.postId='919AEE3F-BFFD-47CD-9D02-FBAF4941507E'").list();
		int count = 0;
		for(Post src : list){
			PersistenceMySql.session().save(src);
			if((count % 20000) == 0){
				log.info("flushing at count "+count);
			   	Persistence.session().flush();
			}
		}
		PersistenceMySql.commit();
		stopwatch.stop();
		log.info("copyPost: complete");
		log.info(stopwatch.toString());
	}
	
	private void copyEntity(String query){
		PersistenceMySql.beginSession();
		Persistence.beginSession();
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		log.info("Starting associationPostTag copy");
		@SuppressWarnings("unchecked")
		//List<AssociationPostTag> list = Persistence.session().createQuery("FROM Post").list();
		//List<Post> list = Persistence.session().createQuery("FROM Post p WHERE p.postId='919AEE3F-BFFD-47CD-9D02-FBAF4941507E'").list();
		List<Object> list = Persistence.session().createQuery(query).list();
		int count = 0;
		for(Object src : list){
			PersistenceMySql.session().save(src);
			if((count % 2000) == 0){
				log.info("flushing at count "+count);
			   	Persistence.session().flush();
			}
		}
		PersistenceMySql.commit();
		stopwatch.stop();
		log.info("copyPost: complete");
		log.info(stopwatch.toString());
	}
	
	
	public static void main(String[] args) {
		CopyDatabase copier;
		StopWatch stopwatch = new StopWatch();
		try{
			stopwatch.start();
			copier = new CopyDatabase();
			//copier.copyAssociationPostTag();
			copier.copyEntity("FROM Post p WHERE p.postId='919AEE3F-BFFD-47CD-9D02-FBAF4941507E'");

		}
		catch(Exception e){
			log.error(e);
			PersistenceMySql.rollback();
		}
		finally{
			log.info("Done. Total time: "+stopwatch.toString());
		}
	}
}
