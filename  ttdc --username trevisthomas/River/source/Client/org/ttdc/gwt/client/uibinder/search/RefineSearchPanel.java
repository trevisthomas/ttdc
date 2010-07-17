package org.ttdc.gwt.client.uibinder.search;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.SEARCH_CREATOR_ID_KEY;

import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.util.DateRangeLite;
import org.ttdc.gwt.client.presenters.util.MyListBox;
import org.ttdc.gwt.client.services.BatchCommandTool;
import org.ttdc.gwt.client.uibinder.calendar.InteractiveCalendarPanel;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonListType;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class RefineSearchPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, RefineSearchPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    //@UiField(provided = true) Grid tableElement =  new Grid(24,2);
        
    @UiField(provided = true) ListBox userListBoxElement;

    @UiField SimplePanel toCalendarElement;
    @UiField SimplePanel fromCalendarElement;
    private InteractiveCalendarPanel startCalendarPresenter;
	private InteractiveCalendarPanel endCalendarPresenter;
	private Day startDay;
	private Day endDay;
	private DateRangeLite dateRange;

    
    protected DateRangeLite getDateRange() {
		return dateRange;
	}

	@Inject
    public RefineSearchPanel(Injector injector) { 
    	this.injector = injector;
    	
    	userListBoxElement = new MyListBox(false);
    	
    	initWidget(binder.createAndBindUi(this));
    	
    
	}
    
    @Override
    public Widget getWidget() {
    	return this;
    }
    
    public void init(Date startDate, Date endDate) {
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.SEARCH_START_DATE, startDate.getTime());
		token.setParameter(HistoryConstants.SEARCH_END_DATE, endDate.getTime());
		init(token);
	}
	
	public void init(HistoryToken token){
		BatchCommandTool batcher = new BatchCommandTool();
		
		dateRange = new DateRangeLite(token);
		
		startCalendarPresenter = injector.createInteractiveCalendarPanel();
		startCalendarPresenter.init(InteractiveCalendarPanel.Mode.DATE_PICKER_MODE ,dateRange.getStartDate());

		endCalendarPresenter = injector.createInteractiveCalendarPanel();
		endCalendarPresenter.init(InteractiveCalendarPanel.Mode.DATE_PICKER_MODE, dateRange.getEndDate());
		
		fromCalendarElement.clear();
		fromCalendarElement.add(startCalendarPresenter.getWidget());
		toCalendarElement.clear();
		toCalendarElement.add(endCalendarPresenter.getWidget());
		
		String creatorId = token.getParameter(SEARCH_CREATOR_ID_KEY);
		
		PersonListCommand personListCmd = new PersonListCommand(PersonListType.ACTIVE);
		personListCmd.setSortOrder(SortBy.BY_HITS);
		personListCmd.setSortDirection(SortDirection.ASC);
		
		CommandResultCallback<PersonListCommandResult> personListCallback = buildPersonListCallback(creatorId);
		
		batcher.add(personListCmd, personListCallback);
		
		injector.getService().execute(batcher.getActionList(), batcher);
	}
	
	/**
	 * After setting rootId, threadId's and Filters call init to prep the search box.
	 * 
	 */
	public void init(){
		init(new HistoryToken());
	}
	
	
	private CommandResultCallback<PersonListCommandResult> buildPersonListCallback(final String creatorId) {
		CommandResultCallback<PersonListCommandResult> replyListCallback = new CommandResultCallback<PersonListCommandResult>(){
			@Override
			public void onSuccess(PersonListCommandResult result) {
				addPerson("", "");
				setSelectedCreatorId("");
				for(GPerson person : result.getResults().getList()){
					addPerson(person.getPersonId(), person.getLogin());
				}
				
				if(creatorId != null)
					setSelectedCreatorId(creatorId);
			}
		};
		return replyListCallback;
	}
	
	
	private void addPerson(String personId, String login) {
		userListBoxElement.addItem(login,personId);
		
	}
	
	private void setSelectedCreatorId(String personId) {
		((MyListBox)userListBoxElement).setSelectedValue(personId);
	}

	public String getSelectedCreatorId(){
		return ((MyListBox)userListBoxElement).getSelectedValue();
	}
	
	
	public void addSelectedDateRangeToToken(HistoryToken token) {
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

}