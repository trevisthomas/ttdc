package org.ttdc.persistence.migration;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.ttdc.gwt.shared.util.StringTools;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

/**
 * 
 * Populates the sort value on all tag objects.
 *
 */
public class SortValueOnTag {
	private static Logger log = Logger.getLogger(SortValueOnTag.class);
	private StopWatch stopwatch = new StopWatch();
	
	public void doit(){
		try{
			start();
			log.info("Setting sort value on tags");
			Persistence.beginSession();
			int count = 1;
			List<Tag> tags = Persistence.session().getNamedQuery("tag.getAll").list();
			for (Tag t : tags) {
				String sortValue = StringTools.formatTitleForSort(t.getValue());
			    t.setSortValue(sortValue);
			    Persistence.session().save(t);
			    
			    if((count % 1000) == 0)
			    	Persistence.session().flush();
			}
			Persistence.commit();
		}
		catch(RuntimeException e){
			Persistence.rollback();
			log.error(e);
		}
		finally{
			end();
		}
	}
	
	public static void main(String[] args) { 
		SortValueOnTag thing = new SortValueOnTag();
		thing.stopwatch.start();
		thing.doit();
		thing.stopwatch.stop();
		
		log.info("Done."+thing.stopwatch.getTime());
		System.exit(0);
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
