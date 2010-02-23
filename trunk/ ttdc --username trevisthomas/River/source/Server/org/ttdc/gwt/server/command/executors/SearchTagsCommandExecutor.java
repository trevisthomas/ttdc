package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.GenericBeanConverter;
import org.ttdc.gwt.server.beanconverters.SearchResultConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.Cloudizer;
import org.ttdc.gwt.server.dao.DaoUtils;
import org.ttdc.gwt.server.dao.DateRange;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.server.dao.TagSearchDao;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Tag;

public class SearchTagsCommandExecutor extends CommandExecutor<SearchTagsCommandResult>{
	private final SimpleDateFormat dateFormatWeekBoundary = new SimpleDateFormat("MMM d");
	
	@Override
	protected CommandResult execute() {
		try {
			SearchTagsCommand command = (SearchTagsCommand)getCommand();
			
			beginSession();
			PaginatedList<GTag> gResults;
			if(command.getTagIdList().size() > 0){
				gResults = performLookup(command);
			}
			else{
				gResults = performSearch(command);
			}
			return new SearchTagsCommandResult(gResults);
			
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		finally{
			commit();
		}
		
	}

	private PaginatedList<GTag> performLookup(SearchTagsCommand command) {
		List<GTag> tags = new ArrayList<GTag>();
		
		int year = 0;
		int weekOfYear = 0;
		GTag weekOfYearTag = null;
		
		
		for(String tagId : command.getTagIdList()){
			Tag t = TagDao.loadTag(tagId); //TODO: this is potentially ineffecient.  Maybe make TagDao handle the list lookup natively?
			GTag gTag = GenericBeanConverter.convertTag(t);
			tags.add(gTag);
			if(gTag.getType().equals(Tag.TYPE_WEEK_OF_YEAR)){
				weekOfYearTag = gTag;
				weekOfYear = Integer.parseInt(t.getValue());
			}
			else if(gTag.getType().equals(Tag.TYPE_DATE_YEAR)){
				year = Integer.parseInt(t.getValue());
			}
		}
		
		//Week of year is a strange tag.  It must be within the context of a year to be meaningful
		//so lets check to see if we have that condition and do some magic if we have the info.
		if(weekOfYear != 0 && year != 0 && weekOfYearTag != null){
			//Fix the value of the weekOfYear to be display friendly
			Date beginning = DaoUtils.getDateBeginningOfWeek(year, weekOfYear);
			Date ending = DaoUtils.getDateEndOfWeek(year, weekOfYear);
			String weekStart = dateFormatWeekBoundary.format(beginning);
			String weekEnd = dateFormatWeekBoundary.format(ending);
			
			weekOfYearTag.setValue(weekStart + " to " + weekEnd);
		}
		
		PaginatedList<GTag> gResults = new PaginatedList<GTag>();
		
		gResults.setCurrentPage(-1);
		gResults.setPageSize(-1);
		gResults.setPhrase("");
		gResults.setTotalResults(tags.size());
		gResults.setList(tags);
		return gResults;
	}

	private PaginatedList<GTag> performSearch(SearchTagsCommand command) {
		TagSearchDao dao = new TagSearchDao();
		dao.setPhrase(command.getPhrase());
		dao.setCurrentPage(command.getPageNumber());
		dao.setDateRange(new DateRange(command.getStartDate(), command.getEndDate()));
		dao.setTagIds(command.getTagIdList());
		PaginatedList<GTag> gResults = SearchResultConverter.convertSearchResults(dao.search());
		Cloudizer.assignPostRelativeAges(gResults.getList());
		return gResults;
	}
}
