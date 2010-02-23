package org.ttdc.gwt.client.presenters.util;

import java.util.Date;

import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

/**
 * I have another DateRange object on the server that has more logic, this one
 * just translates the two dates into the right order mainily because GWT doesnt have
 * a GregorianCalendar
 * 
 */
public class DateRangeLite {
	private Date startDate;
	private Date endDate;
	
	public DateRangeLite(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		
	}
	
	public DateRangeLite(HistoryToken token){
		long start = token.getParameterAsLong(HistoryConstants.SEARCH_START_DATE, 0);
		long end = token.getParameterAsLong(HistoryConstants.SEARCH_END_DATE, 0);
		
		if(start != 0){
			startDate = new Date(start);
		}
		if(end != 0){
			endDate = new Date(end);
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	
	
	
	@Override
	public String toString() {
		String msg = "";
		if(startDate != null){
			if(endDate != null){
				msg = " "+DateFormatUtil.formatLongDay(startDate) +" to "+ DateFormatUtil.formatLongDay(endDate);
			}
			else{
				msg = " after " +DateFormatUtil.formatLongDay(startDate);
			}
		}else if(endDate != null){
			msg = " before " +DateFormatUtil.formatLongDay(endDate);
		}
		return msg;
	}
}

