package org.ttdc.persistence.migration;

import java.util.Date;

import org.apache.log4j.Logger;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class Version7AggrogatedMigrator {
	private final static Logger log = Logger.getLogger(Version7AggrogatedMigrator.class);
	public static void main(String[] args) {
		Version7AggrogatedMigrator runner = new Version7AggrogatedMigrator();
		try{
			runner.start();
			CalculateAverageRating avgrate = new CalculateAverageRating();
			avgrate.go();
			
			Thread.sleep(4000);
			MaterializedPathConversion converter = new MaterializedPathConversion();
			converter.go();
			
			Thread.sleep(4000);
			SortValueOnTag thing = new SortValueOnTag();
			thing.doit();
			
//          I dont think that this post formatting is needed with v7 anymore. Summaries are history i think and so is rich text editing.
//			Thread.sleep(4000);
//			EntrySummaryAndTagRemoveForV7 entryThing = new EntrySummaryAndTagRemoveForV7();
//			entryThing.doit();
			
			Thread.sleep(4000);
			EntryBodyConvertEmbedLinks embedds = new EntryBodyConvertEmbedLinks();
			embedds.doit();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			
		}
		finally{
			runner.end();
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
