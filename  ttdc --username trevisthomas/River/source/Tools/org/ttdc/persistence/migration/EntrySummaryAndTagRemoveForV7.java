package org.ttdc.persistence.migration;

import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.ttdc.biz.network.services.helpers.PostFormatter;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Entry;

public class EntrySummaryAndTagRemoveForV7 {
	private static Logger log = Logger.getLogger(SortValueOnTag.class);
	private StopWatch stopwatch = new StopWatch();
	
	private void doit(){
		try{
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
	}
	
	
	public static void main(String[] args) { 
		EntrySummaryAndTagRemoveForV7 thing = new EntrySummaryAndTagRemoveForV7();
		thing.stopwatch.start();
		thing.doit();
		thing.stopwatch.stop();
		
		log.info("Done. "+ thing.stopwatch.toString());
		System.exit(0);
	}
}
