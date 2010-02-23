package org.ttdc.gwt.client.presenters.users;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonListType;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class UserListPresenter extends BasePagePresenter<UserListPresenter.View>{
	public interface View extends BasePageView{
		void addRow(UserRowPresenter row);
		HasWidgets paginatorPanel();
		 
		HasClickHandlers loginHeaderClickHandlers();
		HasClickHandlers hitCountHeaderClickHandlers();
		HasClickHandlers memberSinceHeaderClickHandlers();
		HasClickHandlers emailHeaderClickHandlers();
		HasClickHandlers nameHeaderClickHandlers();
	}
	
	@Inject
	public UserListPresenter(Injector injector) {
		super(injector,injector.getUserListView());
	}

	@Override
	public void show(HistoryToken token) {
		

		view.show();
		
		final String sort = token.getParameter(HistoryConstants.SORT_KEY, HistoryConstants.USERS_SORT_BY_LOGIN);
		
		PersonListCommand cmd = new PersonListCommand(PersonListType.ACTIVE);
		cmd.setCurrentPage(token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY,1));
		if(token.getParameter(HistoryConstants.SORT_DIRECTION_KEY,HistoryConstants.SORT_ASC).equals(HistoryConstants.SORT_DESC))
			cmd.setSortDirection(SortDirection.DESC);
		else
			cmd.setSortDirection(SortDirection.ASC);
		
		if(HistoryConstants.USERS_SORT_BY_LOGIN.equals(sort))
			cmd.setSortOrder(SortBy.BY_LOGIN);
		else if(HistoryConstants.USERS_SORT_BY_EMAIL.equals(sort))
			cmd.setSortOrder(SortBy.BY_EMAIL);
		else if(HistoryConstants.USERS_SORT_BY_HITS.equals(sort))
			cmd.setSortOrder(SortBy.BY_HITS);
		else if(HistoryConstants.USERS_SORT_BY_LAST_ACCESSED.equals(sort))
			cmd.setSortOrder(SortBy.BY_LAST_ACCESSED);
		else if(HistoryConstants.USERS_SORT_BY_NAME.equals(sort))
			cmd.setSortOrder(SortBy.BY_NAME);
		else
			throw new RuntimeException("Bad sort field");
		
		setupClickHandlers(token);
		
		injector.getService().execute(cmd, buildCallback(token));
	}

	private void setupClickHandlers(final HistoryToken token) {
		view.loginHeaderClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.USERS_SORT_BY_LOGIN, token);
//				if(!isSortBy(token,HistoryConstants.USERS_SORT_BY_LOGIN)){
//					newToken.removeParameter(HistoryConstants.SORT_DIRECTION_KEY);
//					//newToken.setParameter(HistoryConstants.SORT_DIRECTION_KEY,HistoryConstants.SORT_DESC);
//					
//				}
				
				EventBus.fireHistoryToken(newToken);
			}
		});
		view.hitCountHeaderClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.USERS_SORT_BY_HITS, token);
//				if(!isSortBy(token,HistoryConstants.USERS_SORT_BY_HITS)){
//					newToken.removeParameter(HistoryConstants.SORT_DIRECTION_KEY);
//					//newToken.setParameter(HistoryConstants.SORT_DIRECTION_KEY,HistoryConstants.SORT_DESC);
//				}
				EventBus.fireHistoryToken(newToken);
			}
		});
		view.memberSinceHeaderClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.USERS_SORT_BY_LAST_ACCESSED, token);
//				if(!isSortBy(token,HistoryConstants.USERS_SORT_BY_LAST_ACCESSED)){
//					newToken.removeParameter(HistoryConstants.SORT_DIRECTION_KEY);
//					//newToken.setParameter(HistoryConstants.SORT_DIRECTION_KEY,HistoryConstants.SORT_DESC);
//				}
				EventBus.fireHistoryToken(newToken);
			}
		});
		view.emailHeaderClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.USERS_SORT_BY_EMAIL, token);
//				if(!isSortBy(token,HistoryConstants.USERS_SORT_BY_EMAIL)){
//					newToken.removeParameter(HistoryConstants.SORT_DIRECTION_KEY);
//					//newToken.setParameter(HistoryConstants.SORT_DIRECTION_KEY,HistoryConstants.SORT_DESC);
//				}
				EventBus.fireHistoryToken(newToken);
			}
		});
		view.nameHeaderClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.USERS_SORT_BY_NAME, token);
//				if(!isSortBy(token,HistoryConstants.USERS_SORT_BY_NAME)){
//					newToken.removeParameter(HistoryConstants.SORT_DIRECTION_KEY);
//					//newToken.setParameter(HistoryConstants.SORT_DIRECTION_KEY,HistoryConstants.SORT_DESC);
//				}
				EventBus.fireHistoryToken(newToken);
			}
		});
	}

//	private boolean isSortBy(final HistoryToken token, String sortBy) {
//		return token.getParameter(HistoryConstants.SORT_KEY,HistoryConstants.USERS_SORT_BY_LOGIN).equals(sortBy);
//	}

	
	private CommandResultCallback<PersonListCommandResult> buildCallback(final HistoryToken token) {
		CommandResultCallback<PersonListCommandResult> replyListCallback = new CommandResultCallback<PersonListCommandResult>(){
			@Override
			public void onSuccess(PersonListCommandResult result) {
				for(GPerson person : result.getResults().getList()){
					UserRowPresenter userRowPresenter = injector.getUserRowPresenter();
					userRowPresenter.init(person);
					view.addRow(userRowPresenter);
				}
				
				PaginationPresenter paginator = injector.getPaginationPresenter();
				paginator.initialize(token, result.getResults());
				view.paginatorPanel().add(paginator.getWidget());
			}
		};
		return replyListCallback;
	}

	
	
}
