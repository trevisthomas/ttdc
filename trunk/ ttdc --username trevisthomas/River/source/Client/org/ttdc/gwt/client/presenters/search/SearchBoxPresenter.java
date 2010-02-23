package org.ttdc.gwt.client.presenters.search;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_PHRASE_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_TAG_ID_KEY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.home.InteractiveCalendarPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.util.DateRangeLite;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.TagCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.commands.results.TagCommandResult;
import org.ttdc.gwt.shared.commands.types.TagActionType;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/*
 * To replace SiteSearchPresenter,SearchWithinSubsetPresenter and SearchWithinTaggedSubsetPresenter 
 * with one, far more awesome search box.
 */
public class SearchBoxPresenter extends BasePresenter<SearchBoxPresenter.View> /*implements DayClickHandler implements CalendarEventListener*/{
	public interface View extends BaseView{
		HasClickHandlers searchClickHandler();
				
		void addPerson(String personId, String login);
		void setSelectedCreator(String personId);
		void setDefaultMessage(String message);
		void setSearchPhrase(String phrase);
		HasWidgets calendarPanel();
		HasWidgets startDatePanel();
		HasWidgets endDatePanel();
		void setFromDate(Day day);
		void setToDate(Day day);
		String prsonIdFilter();
		String getSearchPhrase();
		String getSelectedCreatorTagId();
		HasText searchBox();
	}

	private String rootId;
	private String threadId;
	private String postId;
	private String phrase;
	private List<String> tagIdList = new ArrayList<String>();
	private List<GTag> tagList = new ArrayList<GTag>();
	
	private String threadTitle;
	private String tagTitles;
	
	private Day startDay;
	private Day endDay;
	private DateRangeLite dateRange;
	
	
	private InteractiveCalendarPresenter startCalendarPresenter;
	private InteractiveCalendarPresenter endCalendarPresenter;

	@Inject
	protected SearchBoxPresenter(Injector injector) {
		super(injector, injector.getSearchBoxView());
		
		view.setDefaultMessage("Enter phrase to perform search");
		view.searchClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				performSearch();
			}
		});
	}
	
	public void init(HistoryToken token){
		BatchCommandTool batcher = new BatchCommandTool();
		
	
		dateRange = new DateRangeLite(token);
		
		startCalendarPresenter = injector.getInteractiveCalendarPresenter();
		startCalendarPresenter.init(dateRange.getStartDate());

		endCalendarPresenter = injector.getInteractiveCalendarPresenter();
		endCalendarPresenter.init(dateRange.getEndDate());
		
		view.calendarPanel().clear();
		view.calendarPanel().add(startCalendarPresenter.getWidget());
		view.calendarPanel().add(endCalendarPresenter.getWidget());
		
		
		TagCommand personListCmd = new TagCommand(TagActionType.LOAD_CREATORS);
		CommandResultCallback<TagCommandResult> personListCallback = buildPersonListCallback();
		
		batcher.add(personListCmd, personListCallback);
		threadTitle = "";
		tagTitles = "";
		phrase = token.getParameter(SEARCH_PHRASE_KEY);
		
		PostCrudCommand postCmd = new PostCrudCommand();
		if(postId != null){
			postCmd.setPostId(postId);
			batcher.add(postCmd,buildPostCallback());
		}
		
		tagIdList.addAll(token.getParameterList(SEARCH_TAG_ID_KEY));
		
		if(tagIdList.size() > 0){
			SearchTagsCommand command = new SearchTagsCommand(tagIdList);
			batcher.add(command,buildTagListCallback());
		}
		if(tagList.size() > 0){
			extractTagTitles(tagList);
		}
		
		setDefaultMessage();//Will be over ridden if there is more info to show
		
		injector.getService().execute(batcher.getActionList(), batcher);
	}
	
	/**
	 * After setting rootId, threadId's and Filters call init to prep the search box.
	 * 
	 */
	public void init(){
		init(new HistoryToken());
	}
	
	private CommandResultCallback<SearchTagsCommandResult> buildTagListCallback(){
		CommandResultCallback<SearchTagsCommandResult> callback = new CommandResultCallback<SearchTagsCommandResult>(){
			@Override
			public void onSuccess(SearchTagsCommandResult result) {
				List<GTag> list = result.getResults().getList();
				extractTagTitles(list);
				setDefaultMessage();
				tagIdList = new ArrayList<String>();
				for(GTag tag : list){
					if(tag.getType().equals(TagConstants.TYPE_CREATOR)){
						view.setSelectedCreator(tag.getTagId());
					}
					else{
						tagIdList.add(tag.getTagId());
					}
				}
			}
		};
		return callback;
	}
	
	private void extractTagTitles(List<GTag> list) {
		StringBuilder sb = new StringBuilder();
		for(GTag tag : list){
			if(sb.length() > 0)
				sb.append(",");
			sb.append(tag.getValue());
		}
		tagTitles = sb.toString();
	}

	private CommandResultCallback<PostCommandResult> buildPostCallback() {
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				GPost post = result.getPost();
				threadTitle = post.getTitle();		
				
				if(post.isRootPost()){
					//Nothing to do
					rootId = post.getPostId();
				}
				else if(post.isThreadPost()){
					threadId = post.getPostId();
				}
				else{
					threadId = post.getThread().getPostId();
				}
				
				setDefaultMessage();
			}
		};
		return rootPostCallback;
	}
	
	private CommandResultCallback<TagCommandResult> buildPersonListCallback() {
		CommandResultCallback<TagCommandResult> replyListCallback = new CommandResultCallback<TagCommandResult>(){
			@Override
			public void onSuccess(TagCommandResult result) {
				for(GTag tag : result.getTagList()){
					view.addPerson(tag.getTagId(),tag.getCreator().getLogin());
				}
			}
		};
		return replyListCallback;
	}
	
	@Override
	public Widget getWidget() {
		
		
		return super.getWidget();
	}

	private void setDefaultMessage() {
		String msg = "";
		
		if(StringUtil.notEmpty(phrase)){
			msg += phrase;
		}
		
		if(tagTitles.length() > 0){
			if(threadTitle.length() > 0)
				msg += "Search in "+threadTitle+" for posts tagged "+tagTitles;
			else
				msg += "Search in posts tagged "+tagTitles;
		}
		else if(StringUtil.notEmpty(threadTitle)){
			msg += "Search in "+threadTitle+".";
		}
		
		msg += dateRange.toString();
		
		if(StringUtil.notEmpty(msg)){
			view.setDefaultMessage(msg);
		}
		
		
	}
	
	private void performSearch(){
		String phrase = view.getSearchPhrase();
		HistoryToken token = new HistoryToken();
		
		String creatorId = view.getSelectedCreatorTagId();
		
		addSelectedDateRangeToToken(token);
	
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_RESULTS);
		if(tagIdList.size() > 0 || StringUtil.notEmpty(creatorId)){
			for(String tagId : tagIdList){
				token.addParameter(SEARCH_TAG_ID_KEY, tagId);
			}
			if(StringUtil.notEmpty(creatorId)){
				token.addParameter(SEARCH_TAG_ID_KEY, creatorId);
			}
		}
		if(StringUtil.notEmpty(rootId)){
			token.addParameter(HistoryConstants.ROOT_ID_KEY, rootId);
			token.addParameter(HistoryConstants.SEARCH_MODE_KEY, HistoryConstants.SEARCH_MODE_IN_ROOT);
		}
		else if(StringUtil.notEmpty(threadId)){
			token.addParameter(HistoryConstants.THREAD_ID_KEY, threadId);
			token.addParameter(HistoryConstants.SEARCH_MODE_KEY, HistoryConstants.SEARCH_MODE_IN_THREAD);
		}
		else{
			token.addParameter(HistoryConstants.SEARCH_MODE_KEY, HistoryConstants.SEARCH_MODE_VALUE_TOPICS);
		}
		
		if(StringUtil.notEmpty(phrase)){
			token.setParameter(SEARCH_PHRASE_KEY, phrase);
		}
				
		EventBus.fireHistoryToken(token);
		
	}

	private void addSelectedDateRangeToToken(HistoryToken token) {
		startDay = startCalendarPresenter.getSelectedDay();
		endDay = endCalendarPresenter.getSelectedDay();
		
		if(startDay == null && endDay == null ){
			return;	
		}
		
		if(startDay != null){
			Date startDate = startDay.toDate();
			token.addParameter(HistoryConstants.SEARCH_START_DATE, ""+startDate.getTime());
		}
		if(endDay != null){
			Date endDate = endDay.toDate();
			token.addParameter(HistoryConstants.SEARCH_END_DATE, ""+endDate.getTime());
		}
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		view.setSearchPhrase(phrase);
		this.phrase = phrase;
	}

	public List<String> getTagIdList() {
		return tagIdList;
	}
	
	public void setTagIdList(List<String> tagIdList) {
		if(tagIdList != null)
			this.tagIdList = tagIdList;
	}
	
	public void addTagIdFilter(String tagId){
		tagIdList.add(tagId);
	}
	
	public void removeTagIdFilter(String tagId){
		tagIdList.remove(tagId);
	}
	
	public List<GTag> getTagList() {
		return tagList;
	}

	public void setTagList(List<GTag> tagList) {
		if(tagList != null)
			this.tagList = tagList;
	}
}
