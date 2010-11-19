package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateRange {
	public Date getMaxDate() {
		return new Date();
	}

	
	private Date startDate;
	private Date endDate;
	
	/**
	 * Remember that i will start at the beginning of the start date and at the end of the end date!
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public DateRange(Date startDate, Date endDate) {
		init(startDate,endDate);
		//Below is for a bug fix caused when searches were defaulting to today without being converted to GMT
		//The problem was first noticed in the user profile post pages.
		if(!isValidDateRange()){
			init(new Date(0),new Date());
		}
	}
	
	/*
	 * Googled, and grabbed from here
	 * http://snippets.dzone.com/posts/show/5288
	 * 
	 */
	private static Date cvtToGmt( Date date )
	{
	   TimeZone tz = TimeZone.getDefault();
	   Date ret = new Date( date.getTime() - tz.getRawOffset() );
	   // if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
	   if ( tz.inDaylightTime( ret )){
	      Date dstDate = new Date( ret.getTime() - tz.getDSTSavings() );
	      // check to make sure we have not crossed back into standard time
	      // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
	      if ( tz.inDaylightTime( dstDate )){
	         ret = dstDate;
	      }
	   }
	   return ret;
	}
	
	private void init(Date start, Date end) {
		//If the end date is before the start date, flip them
		if(start != null && end != null){
			if(end.before(start)){
				Date tmp = start;
				start = end;
				end = tmp;
			}
		}
		
		if(start != null){
			startDate = cvtToGmt(start);  
		}
		
		if(end != null){
			Calendar tmp = GregorianCalendar.getInstance();
			tmp.setTime(end);
			tmp.set(Calendar.HOUR_OF_DAY, 23);
			tmp.set(Calendar.MINUTE, 59);
			tmp.set(Calendar.SECOND, 59);
			endDate = cvtToGmt(tmp.getTime());
		}
		
		//If one is missing, set the other to a value that will get us some data.
		if(startDate != null){
			if(endDate == null){
				endDate = getMaxDate();
			}
		}
		if(endDate != null){
			if(startDate == null){
				startDate = new Date(0L);
			}
		}
	}

//	private void init(Date start, Date end) {
//		//If the end date is before the start date, flip them
//		if(start != null && end != null){
//			if(end.before(start)){
//				Date tmp = start;
//				start = end;
//				end = tmp;
//			}
//		}
//		
//		if(start != null){
//			Calendar tmp = GregorianCalendar.getInstance();
//			tmp.setTime(start);
//			//tmp.setTimeZone(TimeZone.getTimeZone("CST"));
//			tmp.set(Calendar.HOUR_OF_DAY, 0);
//			tmp.set(Calendar.MINUTE, 0);
//			tmp.set(Calendar.SECOND, 0);
//			tmp.add(Calendar.HOUR_OF_DAY, +5);  
//			startDate = tmp.getTime();
//		}
//		
//		if(end != null){
//			Calendar tmp = GregorianCalendar.getInstance();
//			tmp.setTime(end);
//			tmp.set(Calendar.HOUR_OF_DAY, 23);
//			tmp.set(Calendar.MINUTE, 59);
//			tmp.set(Calendar.SECOND, 59);
//			//tmp.setTimeZone(TimeZone.getTimeZone("GMT"));
//			tmp.add(Calendar.HOUR_OF_DAY, +5);
//			endDate = tmp.getTime();
//		}
//		
//		//If one is missing, set the other to a value that will get us some data.
//		if(startDate != null){
//			if(endDate == null){
//				endDate = getMaxDate();
//			}
//		}
//		if(endDate != null){
//			if(startDate == null){
//				startDate = new Date(0L);
//			}
//		}
//	}
	
	boolean isValidDateRange(){
		return startDate != null && endDate != null;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
}
