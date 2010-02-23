package org.ttdc.persistence.migration;

import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.util.StringTools;

/**
 * 
 * Populates the sort value on all tag objects.
 *
 */
public class SortValueOnTag {
	private static Logger log = Logger.getLogger(SortValueOnTag.class);
	private StopWatch stopwatch = new StopWatch();
	
	private void doit(){
		try{
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
	}
	
	public static void main(String[] args) { 
		SortValueOnTag thing = new SortValueOnTag();
		thing.stopwatch.start();
		thing.doit();
		thing.stopwatch.stop();
		
		log.info("Done."+thing.stopwatch.getTime());
		System.exit(0);
	}
}
