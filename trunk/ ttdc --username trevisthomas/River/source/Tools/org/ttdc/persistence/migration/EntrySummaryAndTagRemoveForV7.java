package org.ttdc.persistence.migration;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.ttdc.gwt.server.util.PostFormatter;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Post;

public class EntrySummaryAndTagRemoveForV7 {
	private static Logger log = Logger.getLogger(SortValueOnTag.class);
	private StopWatch stopwatch = new StopWatch();
	
	
	// Disabling this migration step on Oct 25.  I dont want to shacktag in the DB anymore.  
	// V7 has lost rich text so we're back to pre v7 formatting
	// also, summaries are no longer needed.
	
	public void doit(){
		try{
			start();
			log.info("");
			Persistence.beginSession();
			int count = 1;
			List<Entry> entries = Persistence.session().createQuery("From Entry").list();
			for (Entry e : entries) {
				e.setBody(PostFormatter.getInstance().format(e.getBody()));
				e.setSummary(PostFormatter.getInstance().formatSummary(e.getBody()));
			    Persistence.session().save(e);
			    
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
		EntrySummaryAndTagRemoveForV7 thing = new EntrySummaryAndTagRemoveForV7();
		thing.stopwatch.start();
		thing.doit();
		thing.stopwatch.stop();
		
		log.info("Done. "+ thing.stopwatch.toString());
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
