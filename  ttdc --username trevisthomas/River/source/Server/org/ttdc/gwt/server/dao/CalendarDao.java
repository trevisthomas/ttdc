package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.server.util.CalendarBuilder;
import org.ttdc.gwt.shared.calender.CalendarThreadSummary;
import org.ttdc.gwt.shared.calender.CalendarPost;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.DayProxy;
import org.ttdc.gwt.shared.calender.Hour;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Week;
import org.ttdc.gwt.shared.calender.Year;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.DaySummaryEntity;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.SimplePostEntity;
import org.ttdc.persistence.objects.ThreadSummaryEntity;

public class CalendarDao extends FilteredPostPaginatedDaoBase{
	private final static Logger log = Logger.getLogger(CalendarDao.class);
	private int yearNumber;
	private int weekOfYear;
	private int monthOfYear;
	private int dayOfMonth;
	
	public Year buildYear(){
		log.debug("Building year calendar.");
		Queue<Day> data = buildDayDataForYear(yearNumber);
		CalendarBuilder cb = new CalendarBuilder(new MyDayProxy(data));
		Year year = cb.buildYear(yearNumber);
		return year;
	}
	
	
	public Week buildWeek(){
		log.debug("Building week calendar.");
		Queue<Day> data = buildDayDataForWeek(yearNumber,weekOfYear);
		CalendarBuilder cb = new CalendarBuilder(new MyDayProxy(data));
		Week week = cb.buildWeek(yearNumber,weekOfYear);
		return week;
	}
	
	public Month buildMonth(){
		log.debug("Building month calendar.");
		Queue<Day> data = buildDayDataForMonth(yearNumber,monthOfYear);
		CalendarBuilder cb = new CalendarBuilder(new MyDayProxy(data));
		Month month = cb.buildMonth(yearNumber,monthOfYear);
		return month;
	}
	
	public Month buildSimpleMonth(){
		log.debug("Building simple month calendar.");
		Queue<Day> data = buildDayDataForMonthSimple(yearNumber,monthOfYear);
		CalendarBuilder cb = new CalendarBuilder(new MyDayProxy(data));
		Month month = cb.buildMonth(yearNumber,monthOfYear);
		return month;
	}
	
	public Day buildDay() {
		log.debug("Building day calendar.");
		Queue<Day> data = buildDayDataForDay(yearNumber,monthOfYear,dayOfMonth);
		CalendarBuilder cb = new CalendarBuilder(new MyDayProxy(data));
		Day day = cb.buildDay(yearNumber,monthOfYear,dayOfMonth);
		return day;
	}
	
	
	private Queue<Day> buildDayDataForDay(int yr, int mo, int dd){
		Date startDate = DaoUtils.createDateStart(yr,mo,dd);
		Date endDate = DaoUtils.createDateEnd(yr,mo,dd);
		log.debug(startDate);
    	log.debug(endDate);
		
		@SuppressWarnings("unchecked")
		List<SimplePostEntity> list = session().getNamedQuery("CalendarDao.fetchHourlyWithSummary")
			.setParameter("filterMask", buildFilterMask(getFilterFlags()))
			.setTimestamp("startDate",startDate)
			.setTimestamp("endDate", endDate).list();
		
		Queue<Day> data = convertSimplePostEntityListToDayQueue(list);
		
		return data;
	}
	
	private Queue<Day> buildDayDataForWeek(int yearNumber,int weekOfYear){
		Date startDate = DaoUtils.getDateBeginningOfWeek(yearNumber, weekOfYear);
    	Date endDate = DaoUtils.getDateEndOfWeek(yearNumber, weekOfYear);
    	
    	log.debug(startDate);
    	log.debug(endDate);
    	
    	@SuppressWarnings("unchecked")
    	List<SimplePostEntity> list = session().getNamedQuery("CalendarDao.fetchHourly")
    		.setParameter("filterMask", buildFilterMask(getFilterFlags()))
			.setTimestamp("startDate",startDate)
			.setTimestamp("endDate", endDate).list();
    	
    	Queue<Day> data = convertSimplePostEntityListToDayQueue(list);
    	return data;
	}
	
	
	/* 
	 * 	The version below uses the date range query from search, it was an attempt to make
	 * mySQL faster, still not great.
	 * 
	 */
//	private Queue<Day> buildDayDataForWeek(int yearNumber,int weekOfYear){
//		Date startDate = DaoUtils.getDateBeginningOfWeek(yearNumber, weekOfYear);
//    	Date endDate = DaoUtils.getDateEndOfWeek(yearNumber, weekOfYear);
//    	
//    	log.debug("Start: "+startDate);
//    	log.debug("End: "+endDate);
//		
//		DateRange dateRange = new DateRange(startDate, endDate);
//		
//		PostSearchDao searchDao = new PostSearchDao();
//		searchDao.setPageSize(-1);
//		searchDao.setDateRange(dateRange);
//		PaginatedList<Post> result = searchDao.search();
//		
//		Queue<Day> data = new LinkedList<Day>();
//		
//		Inflatinator inflatinator = new Inflatinator(result.getList());
//		//InflatinatorLite inflatinator = new InflatinatorLite(result.getList());
//		
//		Day d = null;
//		Hour h = null;
//    	for(GPost post : inflatinator.extractPosts()){
//	    //for(Post post : result.getList()){
//			Day currDay = new Day(post.getDate());
//			
//			if(d == null || !currDay.equals(d)){
//				d = currDay;
//	        	d.initHourly();
//	        	data.add(d);
//	        	h = d.getHour(0);
//    		}
//    		if(h.getHourOfDay() != getHourOfDayFromDate(post.getDate()))
//    			h = d.getHour(getHourOfDayFromDate(post.getDate()));
//    		
//    		CalendarPost calendarPost = buildCalendarPostFromRegularPostEntity(post);
//    		
//    		h.add(calendarPost);
//    	}
//		return data;
//	}
	
	private int getHourOfDayFromDate(Date date){
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal.get(GregorianCalendar.HOUR_OF_DAY);
	}
	
	private Queue<Day> buildDayDataForMonth(int yearNumber,int monthOfYear){
		@SuppressWarnings("unchecked")
    	List<ThreadSummaryEntity> list = session().getNamedQuery("CalendarDao.fetchMonth")
    		.setParameter("filterMask", buildFilterMask(getFilterFlags()))
			.setInteger("year", yearNumber)
			.setInteger("month", monthOfYear).list();
		
		Queue<Day> data = new LinkedList<Day>();
		CalendarThreadSummary summary = null; 
		Day d = null;
		for(ThreadSummaryEntity ts : list){
			if(!compare(ts,d)){
				d = new Day();
				d.setDay(ts.getDay());
	        	d.setYear(ts.getYear());
	        	d.setMonth(ts.getMonth());
	        	d.initSummary();
	        	data.add(d);
			}
			summary = new CalendarThreadSummary();
			summary.setCount(ts.getCount());
			summary.setDay(ts.getDay());
			summary.setMonth(ts.getMonth());
			summary.setRootId(ts.getRootId());
			summary.setTitle(ts.getTitle());
			summary.setYear(ts.getYear());
			//log.debug(summary);
			d.addThread(summary);
		}
		return data;
	}
	
	
//	Map<String,CalendarThreadSummary> summaryMap = new HashMap<String,CalendarThreadSummary>();
//	private Queue<Day> buildDayDataForMonth(int yearNumber,int monthOfYear){
//		Calendar start = new GregorianCalendar();
//		Calendar end = new GregorianCalendar();
//		start.set(yearNumber, monthOfYear-1, 1,0,0,0);   
//		end.set(yearNumber, monthOfYear-1, 1,0,0,0);
//		end.add(GregorianCalendar.MONTH, 1);
//		end.add(GregorianCalendar.DAY_OF_MONTH, -1);
//		
//		log.debug("start: "+start.getTime());
//		log.debug("end: "+end.getTime());
//		
//		DateRange dateRange = new DateRange(start.getTime(), end.getTime());
//		
//		PostSearchDao searchDao = new PostSearchDao();
//		searchDao.setPageSize(-1);
//		searchDao.setDateRange(dateRange);
//		PaginatedList<Post> result = searchDao.search();
//		
//		Queue<Day> data = new LinkedList<Day>();
//		CalendarThreadSummary summary = null; 
//		Day d = null;
//		for(Post post : result.getList()){
//			Day currDay = new Day(post.getDate());
//			if(d == null || !currDay.equals(d)){
//				summaryMap.clear(); //Start reset summary map for the day.
//				d = currDay;
//	        	d.initSummary();
//	        	data.add(d);
//			}
//			if(!summaryMap.containsKey(post.getRoot().getPostId())){
//				summary = new CalendarThreadSummary();
//				summaryMap.put(post.getRoot().getPostId(), summary);
//				d.addThread(summary);
//			}
//			else{
//				summary = summaryMap.get(post.getRoot().getPostId());
//			}
//			summary.setCount(summary.getCount()+1);
//			summary.setDay(d.getDay());
//			summary.setMonth(d.getMonth()+1);
//			summary.setYear(d.getYear());
//			summary.setRootId(post.getRoot().getPostId());
//			summary.setTitle(post.getTitle());
//			//log.debug(summary);
//			
//		}
//		return data;
//	}
	
	private Queue<Day> buildDayDataForMonthSimple(int yearNumber, int monthOfYear){
		Queue<Day> data = new LinkedList<Day>();
		
		@SuppressWarnings("unchecked")
    	List<DaySummaryEntity> list = session().getNamedQuery("CalendarDao.fetchSimpleMonth")
			.setInteger("year", yearNumber)
			.setInteger("month", monthOfYear).list();
		
		for(DaySummaryEntity dse : list){
			Day d = new Day();
			d.setDay(dse.getDay());
			d.setYear(dse.getYear());
        	d.setMonth(dse.getMonth());
        	data.add(d);
		}
		
		return data;
	}
	
	private Queue<Day> buildDayDataForYear(int yearNumber){
		Queue<Day> data = new LinkedList<Day>();
		
		@SuppressWarnings("unchecked")
    	List<DaySummaryEntity> list = session().getNamedQuery("CalendarDao.fetchYear")
			.setInteger("year", yearNumber).list();
		
		for(DaySummaryEntity dse : list){
			Day d = new Day();
			d.setDay(dse.getDay());
			d.setYear(dse.getYear());
        	d.setMonth(dse.getMonth());
        	data.add(d);
		}
		
		return data;
	}
	

	private Queue<Day> convertSimplePostEntityListToDayQueue(List<SimplePostEntity> list) {
		Queue<Day> data = new LinkedList<Day>();
    	Day d = null;
    	Hour h = null;
    	for(SimplePostEntity sp : list){
    		//log.debug(sp);
    		if(!compare(sp,d)){
    			d = new Day();
	    		d.setDay(sp.getDay());
	        	d.setYear(sp.getYear());
	        	d.setMonth(sp.getMonth());
	        	d.initHourly();
	        	data.add(d);
	        	h = d.getHour(0);
    		}
    		if(h.getHourOfDay() != sp.getHour())
    			h = d.getHour(sp.getHour());
    		
    		CalendarPost calendarPost = buildCalendarPostFromSimplePostEntity(sp);
    		
    		h.addCalendarPost(calendarPost);
    	}
		return data;
	}


	private CalendarPost buildCalendarPostFromSimplePostEntity(SimplePostEntity sp) {
		CalendarPost calendarPost = new CalendarPost();
		calendarPost.setCreatorId(sp.getCreatorId());
		calendarPost.setCreatorLogin(sp.getCreatorLogin());
		calendarPost.setDate(sp.getDate());
		calendarPost.setHour(sp.getHour());
		calendarPost.setMonth(sp.getMonth());
		calendarPost.setPostId(sp.getPostId());
		calendarPost.setRootId(sp.getRootId());
		calendarPost.setSummary(sp.getSummary());
		calendarPost.setTitle(sp.getTitle());
		calendarPost.setYear(sp.getYear());
		return calendarPost;
	}
	
	private CalendarPost buildCalendarPostFromRegularPostEntity(GPost p) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(p.getDate());
		
		CalendarPost calendarPost = new CalendarPost();
		calendarPost.setCreatorId(p.getCreator().getPersonId());
		calendarPost.setCreatorLogin(p.getCreator().getLogin());
		calendarPost.setDate(p.getDate());
		calendarPost.setHour(cal.get(Calendar.HOUR_OF_DAY));
		calendarPost.setMonth(cal.get(Calendar.MONTH)+1);
		calendarPost.setPostId(p.getPostId());
		calendarPost.setRootId(p.getRoot().getPostId());
		//calendarPost.setSummary(p.getEntry());
		calendarPost.setTitle(p.getTitle());
		calendarPost.setYear(cal.get(Calendar.YEAR));
		return calendarPost;
	}
	
	private CalendarPost buildCalendarPostFromRegularPostEntity(Post p) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(p.getDate());
		
		CalendarPost calendarPost = new CalendarPost();
		calendarPost.setCreatorId("123dontclickid");
		calendarPost.setCreatorLogin("Todo: login");
		calendarPost.setDate(p.getDate());
		calendarPost.setHour(cal.get(Calendar.HOUR_OF_DAY));
		calendarPost.setMonth(cal.get(Calendar.MONTH)+1);
		calendarPost.setPostId(p.getPostId());
		calendarPost.setRootId(p.getRoot().getPostId());
		calendarPost.setSummary("TODO, add summary");
		calendarPost.setTitle("Sigh, todo add title");
		calendarPost.setYear(cal.get(Calendar.YEAR));
		return calendarPost;
	}
	
	private boolean compare(SimplePostEntity spe, Day d){
		if(d == null) return false;
		return spe.getDay() == d.getDay() && spe.getMonth() == d.getMonth()
				&& spe.getYear() == d.getYear();
	}
	private boolean compare(ThreadSummaryEntity tse, Day d){
		if(d == null) return false;
		return tse.getDay() == d.getDay() && tse.getMonth() == d.getMonth()
				&& tse.getYear() == d.getYear();
	}
	
	public int getYearNumber() {
		return yearNumber;
	}

	public void setYearNumber(int yearNumber) {
		this.yearNumber = yearNumber;
	}
	
	public int getWeekOfYear() {
		return weekOfYear;
	}

	public void setWeekOfYear(int weekOfYear) {
		this.weekOfYear = weekOfYear;
	}

	public int getMonthOfYear() {
		return monthOfYear;
	}

	public void setMonthOfYear(int monthOfYear) {
		this.monthOfYear = monthOfYear;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}


	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	
	/**
	 * 
	 * 
	 *
	 */
	private class MyDayProxy implements DayProxy{
		private Queue<Day> dataForPeriod;
		private Day latest;
		MyDayProxy(Queue<Day> dataForPeriod){
			this.dataForPeriod = dataForPeriod;
			latest = dataForPeriod.poll();
		}
		 
		@Override
		public void fill(Day day) {
			if(latest == null) return;
			if(day.equals(latest)){
				day.setContent(true);
				//load it up
				if(latest.isHourly()){
					day.setHours(latest.getHours());
				}
				else if(latest.isSummary()){
					day.setThreads(latest.getThreads());
				}
				else{
					/*Nothing to do*/
				}
				latest = dataForPeriod.poll();
			}
			else{
				if(latest.isHourly())
					day.setHours(new ArrayList<Hour>());
				day.setContent(false);
			}
		}
	}



	

}
