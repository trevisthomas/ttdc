package org.ttdc.persistence.msql.experimental;

import static org.ttdc.persistence.Persistence.session;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.ImageFull;

public class TryMysql {
	private static final Logger log = Logger.getLogger(TryMysql.class);
	public static void main(String[] args) {
		Session mysql = Persistence2.beginSession();
		Session sql = Persistence.beginSession();
		
		try{
			@SuppressWarnings("unchecked")
			List<ImageFull> list = sql.createQuery("FROM ImageFull").list();///i where i.imageId='07725998-F0FF-49FA-B226-00981624A392'
			for(ImageFull src : list){
				ImageExperiment dest = new ImageExperiment();
				dest.setImage(src.getImage());
				dest.setImageId(src.getImageId());
				dest.setName(src.getName());
				mysql.save(dest);
				mysql.flush();
				log.info("Name: "+src.getName());
			}
			
			Persistence2.commit();
		}
		catch(Exception e){
			Persistence2.rollback();
		}
	}
}
