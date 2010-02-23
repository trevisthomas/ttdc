package org.ttdc.struts.network.actions.history;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.WidgetService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;
import org.ttdc.util.web.Month;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Action to support history
 * 
 * @author Trevis
 *
 */

@Results({
	@Result( name="success", value="tiles.history", type=TilesResult.class)
})
final public class History extends ActionSupport implements SecurityAware {
	private static final long serialVersionUID = -1990378125323407131L;
	private static Logger log = Logger.getLogger(History.class);
	private Person person;
	private List<String> years;
	private String year = "";
	private List<Month> months;
	
	@Override
	public String execute() throws Exception {
		try{
			//Get years with content
			years = CommentService.getInstance().getYearsWithContent();
			if(year.equals("")){
				year = years.get(years.size() - 1);
			}
			months = WidgetService.getInstance().getYearCalendar(Integer.valueOf(year));
			
			//Get content for latest year or selected year
		}
		catch(NumberFormatException e){
			log.info(e);
			addActionError(e.getMessage());
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
		}
		return SUCCESS;
	}
	
	
	public List<String> getYears() {
		return years;
	}
	public void setYears(List<String> years) {
		this.years = years;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public List<Month> getMonths() {
		return months;
	}
	public void setMonths(List<Month> months) {
		this.months = months;
	}
	
}	
