package org.ttdc.gwt.server.command.executors;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;

import java.util.Collections;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.SearchResultConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.executors.utils.Cloudizer;
import org.ttdc.gwt.server.dao.TagSearchDao;
import org.ttdc.gwt.shared.commands.BrowsePopularTagsCommand;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Tag;

public class BrowsePopularTagsCommandExecutor  extends CommandExecutor<SearchTagsCommandResult>{
	@Override
	protected CommandResult execute() {
		try {
			BrowsePopularTagsCommand command = (BrowsePopularTagsCommand)getCommand();
			
			beginSession();
			
			TagSearchDao dao = new TagSearchDao();
			dao.setPageSize(command.getMaxTags());
			dao.addFilterForTagType(Tag.TYPE_TOPIC);
//			dao.addFilterForTagType(Tag.TYPE_CREATOR);
//			dao.addFilterForTagType(Tag.TYPE_DATE_YEAR);
//			dao.addFilterForTagType(Tag.TYPE_DATE_MONTH);
//			dao.addFilterForTagType(Tag.TYPE_RATING);
//			dao.addFilterForTagType(Tag.TYPE_DISPLAY);
			
			
			PaginatedList<Tag> results = dao.search();
			
			if(command.isSortAlphabetical()){
				Collections.sort(results.getList(),new Tag.AlphebeticalByValueComparator());
			}
			
			PaginatedList<GTag> gResults = SearchResultConverter.convertSearchResults(results);
			
			Cloudizer.assignPostRelativeAges(gResults.getList());
			
			return new SearchTagsCommandResult(gResults);
			
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		finally{
			commit();
		}
		
	}
	
	

}
