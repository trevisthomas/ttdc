package org.ttdc.gwt.server.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class LuceneUtils {
	public final static SimpleDateFormat luceneDateFormater = new SimpleDateFormat("yyyyMMddHHmm");
	public synchronized static String addDateRangeToLuceneQuery(String original, DateRange dateRange){
		String phrase = original;
		
		if(StringUtils.isEmpty(phrase)){
			if(dateRange != null && dateRange.isValidDateRange()){
				phrase = String.format("+date:[%s TO %s]",luceneDateFormater.format(dateRange.getStartDate()),luceneDateFormater.format(dateRange.getEndDate()));
			}
			else{
				phrase = String.format("+date:[%s TO %s]",luceneDateFormater.format(new Date(0)),luceneDateFormater.format(new Date()));
			}
		}
		
		return phrase;
	}
	
	public synchronized static String addDateRangeToLuceneQuery2(String original, DateRange dateRange){
		String phrase = "";
		if(StringUtils.isEmpty(original))
			phrase = "";
		else
			phrase = "+"+original;
		//if(StringUtils.isEmpty(phrase)){
			if(dateRange != null && dateRange.isValidDateRange()){
				phrase = String.format("%s +date:[%s TO %s]",phrase,luceneDateFormater.format(dateRange.getStartDate()),luceneDateFormater.format(dateRange.getEndDate()));
			}
			else if(StringUtils.isEmpty(phrase)){
				phrase = String.format("%s +date:[%s TO %s]",phrase,luceneDateFormater.format(new Date(0)),luceneDateFormater.format(new Date()));
			}
		//}
		
		return phrase;
	}
}
