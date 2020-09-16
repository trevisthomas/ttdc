package org.ttdc.gwt.client.uibinder.users;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.users.UserRowPresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.PaginationPanel;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.client.uibinder.shared.UiHelpers;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonListType;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.gwt.shared.commands.types.SortDirection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserListPanel extends BasePageComposite{
	interface MyUiBinder extends UiBinder<Widget, UserListPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField(provided = true) Widget pageHeaderElement;
	@UiField FlexTable tableElement;
	@UiField SimplePanel paginatorElement;
	@UiField (provided = true) Widget pageFooterElement;
	
	private final StandardPageHeaderPanel pageHeaderPanel;
	private HistoryToken token;
	
	private final FocusPanel loginHeader;
	private final FocusPanel hitCountHeader;
	private final FocusPanel memberSinceHeader;
	private final FocusPanel emailHeader;
	private final FocusPanel nameHeader;
	
	private int row = 0;
	@Inject
	public UserListPanel(Injector injector) {
		this.injector = injector;
		
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	
		loginHeader = UiHelpers.createTableHeaderPanel("Login");
		hitCountHeader = UiHelpers.createTableHeaderPanel("Hit Count");
		memberSinceHeader = UiHelpers.createTableHeaderPanel("Last Accessed");
		emailHeader = UiHelpers.createTableHeaderPanel("Email");
		nameHeader = UiHelpers.createTableHeaderPanel("Name");
		pageFooterElement = injector.createStandardFooter().getWidget();
    	
		initWidget(binder.createAndBindUi(this));
		
		initHeader();
	}
	
	

	@Override
	protected void onShow(HistoryToken token) {
	
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
		
		pageHeaderPanel.init("Users", "list of active user accounts");
		pageHeaderPanel.getSearchBoxPresenter().init();
	}

	public void addRow(UserRowPresenter rowPresenter) {
		row++;
		
		tableElement.getRowFormatter().setStyleName(row, "tt-userlistrow");
		
		tableElement.setWidget(row, 0, rowPresenter.getLoginWidget());
		tableElement.setWidget(row, 1, rowPresenter.getNameWidget());
		if (isPrivateAccessUser()) {
			tableElement.setWidget(row, 2, rowPresenter.getHitsWidget());
			tableElement.setWidget(row, 3, rowPresenter.getMemberSinceWidget());
			tableElement.setWidget(row, 4, rowPresenter.getEmailWidget());
		}
	}

	private void initHeader() {
		row = 0;
		tableElement.clear();
		tableElement.getColumnFormatter().addStyleName(0, "tt-userlistcol-login");
		tableElement.getColumnFormatter().addStyleName(1, "tt-userlistcol-name");
		if (isPrivateAccessUser()) {
			tableElement.getColumnFormatter().addStyleName(2, "tt-userlistcol-hits");
			tableElement.getColumnFormatter().addStyleName(3, "tt-userlistcol-date");
			tableElement.getColumnFormatter().addStyleName(4, "tt-userlistcol-email");
		}
		tableElement.getRowFormatter().addStyleName(0, "tt-userlistrow-header");
		

		tableElement.setWidget(0, 0, loginHeader);
		tableElement.setWidget(0, 1, nameHeader);
		if (isPrivateAccessUser()) {
			tableElement.setWidget(0, 2, hitCountHeader);
			tableElement.setWidget(0, 3, memberSinceHeader);
			tableElement.setWidget(0, 4, emailHeader);
		}
	}

	private boolean isPrivateAccessUser() {
		return ConnectionId.getInstance().getCurrentUser().isPrivateAccessAccount();
	}
	private void setupClickHandlers(final HistoryToken token) {
		loginHeader.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.USERS_SORT_BY_LOGIN, token);
				EventBus.fireHistoryToken(newToken);
			}
		});
		hitCountHeader.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.USERS_SORT_BY_HITS, token);
				EventBus.fireHistoryToken(newToken);
			}
		});
		memberSinceHeader.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.USERS_SORT_BY_LAST_ACCESSED, token);
				EventBus.fireHistoryToken(newToken);
			}
		});
		emailHeader.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.USERS_SORT_BY_EMAIL, token);
				EventBus.fireHistoryToken(newToken);
			}
		});
		nameHeader.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken newToken = PresenterHelpers.cloneTokenForSort(HistoryConstants.USERS_SORT_BY_NAME, token);
				EventBus.fireHistoryToken(newToken);
			}
		});
	}

	private CommandResultCallback<PersonListCommandResult> buildCallback(final HistoryToken token) {
		CommandResultCallback<PersonListCommandResult> replyListCallback = new CommandResultCallback<PersonListCommandResult>(){
			@Override
			public void onSuccess(PersonListCommandResult result) {
				for(GPerson person : result.getResults().getList()){
					UserRowPresenter userRowPresenter = injector.getUserRowPresenter();
					userRowPresenter.init(person);
					addRow(userRowPresenter);
				}
				
				PaginationPanel paginationPanel = injector.createPaginationPanel();
				paginationPanel.initialize(token, result.getResults());
				paginatorElement.clear();
				paginatorElement.add(paginationPanel);
			}
		};
		return replyListCallback;
	}

}
