package org.ttdc.gwt.server.command.executors.utils;

import java.util.List;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.util.Median;

public class Cloudizer {

	/**
	 * Uses the median calculator to distribute posts into lists of relative age and then assign a string to denote it.
	 * This is used to show a gradient using css to present the relative age of new posts.
	 * 
	 * @param posts
	 * @param latest - a list of the latest posts so i know which ones to age.
	 */
	public static void assignPostRelativeAges(List<GTag> latest){
		Cloudizer util = new Cloudizer();
		Median<GTag> calculator =  new Median<GTag>();
		List<List<GTag>> lists = calculator.medianDistribution(3, latest, new TagSourceValueReader());
		
		List<GTag> list;
		list = lists.get(0);
		util.assignRelativeAge(list,0);
		list = lists.get(1);
		util.assignRelativeAge(list,1);
		list = lists.get(2);
		util.assignRelativeAge(list,2);
		list = lists.get(3);
		util.assignRelativeAge(list,3);
		
		list = lists.get(4);
		util.assignRelativeAge(list,4);
		list = lists.get(5);
		util.assignRelativeAge(list,5);
		list = lists.get(6);
		util.assignRelativeAge(list,6);
		list = lists.get(7);
		util.assignRelativeAge(list,7);
		
	}
	
	private void assignRelativeAge(List<GTag> list, int rank){
		for(GTag t : list){
			t.setCloudRank(rank);
		}
	}
	
	private static class TagSourceValueReader implements Median.SourceValueReader<GTag>{
		public long readSourceValue(GTag target) {
			return target.getMass();
		}
	}
}
