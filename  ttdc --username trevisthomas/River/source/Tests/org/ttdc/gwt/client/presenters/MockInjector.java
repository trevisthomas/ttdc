package org.ttdc.gwt.client.presenters;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.ttdc.gwt.client.Injector;

import org.ttdc.gwt.client.autocomplete.SugestionOracle;
import org.ttdc.gwt.client.components.widgets.PostPanelWidget;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.admin.AdminToolsPresenter;
import org.ttdc.gwt.client.presenters.admin.ImageManagementPresenter;
import org.ttdc.gwt.client.presenters.admin.ImageRowPresenter;
import org.ttdc.gwt.client.presenters.admin.PersonStatusGadgetPresenter;
import org.ttdc.gwt.client.presenters.admin.StyleManagementPresenter;
import org.ttdc.gwt.client.presenters.admin.StyleRowPresenter;
import org.ttdc.gwt.client.presenters.admin.UserAdministrationPresenter;
import org.ttdc.gwt.client.presenters.admin.UserObjectTemplateEditorPresenter;
import org.ttdc.gwt.client.presenters.admin.UserObjectTemplateRowPresenter;
import org.ttdc.gwt.client.presenters.admin.UserPrivilegeGadgetPresenter;
import org.ttdc.gwt.client.presenters.calendar.CalendarPostPresenter;
import org.ttdc.gwt.client.presenters.calendar.CalendarPresenter;
import org.ttdc.gwt.client.presenters.calendar.DayOfMonthPresenter;
import org.ttdc.gwt.client.presenters.calendar.DayPresenter;
import org.ttdc.gwt.client.presenters.calendar.HourPresenter;
import org.ttdc.gwt.client.presenters.calendar.MonthDetailPresenter;
import org.ttdc.gwt.client.presenters.calendar.MonthPresenter;
import org.ttdc.gwt.client.presenters.calendar.ScaleSelectorPresenter;
import org.ttdc.gwt.client.presenters.calendar.WeekPresenter;
import org.ttdc.gwt.client.presenters.calendar.YearPresenter;
import org.ttdc.gwt.client.presenters.comments.NewCommentPresenter;
import org.ttdc.gwt.client.presenters.comments.RemovableTagPresenter;
import org.ttdc.gwt.client.presenters.dashboard.EditProfilePresenter;
import org.ttdc.gwt.client.presenters.dashboard.ProfilePresenter;
import org.ttdc.gwt.client.presenters.dashboard.RemovableWebLinkPresenter;
import org.ttdc.gwt.client.presenters.dashboard.ResetPasswordPresenter;
import org.ttdc.gwt.client.presenters.dashboard.SettingsPresenter;
import org.ttdc.gwt.client.presenters.dashboard.UserDashboardPresenter;
import org.ttdc.gwt.client.presenters.demo.DemoPresenter;
import org.ttdc.gwt.client.presenters.home.CalendarSelectorModulePresenter;
import org.ttdc.gwt.client.presenters.home.ConversationPresenter;
import org.ttdc.gwt.client.presenters.home.EarmarkedPresenter;
import org.ttdc.gwt.client.presenters.home.FlatPresenter;
import org.ttdc.gwt.client.presenters.home.Home2Presenter;
import org.ttdc.gwt.client.presenters.home.InteractiveCalendarPresenter;
import org.ttdc.gwt.client.presenters.home.MoreLatestPresenter;
import org.ttdc.gwt.client.presenters.home.NestedPresenter;
import org.ttdc.gwt.client.presenters.home.ThreadPresenter;
import org.ttdc.gwt.client.presenters.home.TrafficPersonPresenter;
import org.ttdc.gwt.client.presenters.home.TrafficPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieListPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.post.LikesPresenter;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenter;
import org.ttdc.gwt.client.presenters.post.PostSummaryPresenter;
import org.ttdc.gwt.client.presenters.post.SearchPresenter;
import org.ttdc.gwt.client.presenters.post.SearchResultsPresenter;
import org.ttdc.gwt.client.presenters.post.SearchTagResultsPresenter;
import org.ttdc.gwt.client.presenters.post.SearchWithinSubsetPresenter;
import org.ttdc.gwt.client.presenters.post.SearchWithinTaggedSubsetPresenter;
import org.ttdc.gwt.client.presenters.post.SiteSearchPresenter;
import org.ttdc.gwt.client.presenters.post.TagCloudPresenter;
import org.ttdc.gwt.client.presenters.post.TagRemovePresenter;
import org.ttdc.gwt.client.presenters.search.SearchBoxDatePresenter;
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.shared.ButtonPresenter;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.GenericTabularFlowPresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.shared.ImageUploadPresenter;
import org.ttdc.gwt.client.presenters.shared.MovieCoverWithRatingPresenter;
import org.ttdc.gwt.client.presenters.shared.PageMessagesPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationView;
import org.ttdc.gwt.client.presenters.shared.PopupCalendarDatePresenter;
import org.ttdc.gwt.client.presenters.shared.TextPresenter;
import org.ttdc.gwt.client.presenters.shared.UserIdentityPresenter;
import org.ttdc.gwt.client.presenters.shared.WaitPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter.View;
import org.ttdc.gwt.client.presenters.tag.PostTagListPresenter;
import org.ttdc.gwt.client.presenters.tag.SearchTagListPresenter;
import org.ttdc.gwt.client.presenters.tag.TagListPresenterView;
import org.ttdc.gwt.client.presenters.topic.TopicConversationPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicFlatPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicHierarchyPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicNestedPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicSummaryPresenter;
import org.ttdc.gwt.client.presenters.users.CreateAccountPresenter;
import org.ttdc.gwt.client.presenters.users.LoginPresenter;
import org.ttdc.gwt.client.presenters.users.MoreSearchPresenter;
import org.ttdc.gwt.client.presenters.users.PublicUserProfilePresenter;
import org.ttdc.gwt.client.presenters.users.RequestPasswordResetPresenter;
import org.ttdc.gwt.client.presenters.users.UserListPresenter;
import org.ttdc.gwt.client.presenters.users.UserRowPresenter;
import org.ttdc.gwt.client.presenters.users.UserToolsPresenter;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.Navigation;
import org.ttdc.gwt.client.uibinder.SiteUpdatePanel;
import org.ttdc.gwt.client.uibinder.calendar.CalendarBreadCrumbPanel;
import org.ttdc.gwt.client.uibinder.calendar.CalendarPanel;
import org.ttdc.gwt.client.uibinder.calendar.DayPanel;
import org.ttdc.gwt.client.uibinder.calendar.InteractiveCalendarPanel;
import org.ttdc.gwt.client.uibinder.calendar.SmallMonthPanel;
import org.ttdc.gwt.client.uibinder.comment.CommentEditorPanel;
import org.ttdc.gwt.client.uibinder.dashboard.FilteredPost;
import org.ttdc.gwt.client.uibinder.dashboard.UserDashboardPanel;
import org.ttdc.gwt.client.uibinder.forum.ForumListItemPanel;
import org.ttdc.gwt.client.uibinder.forum.ForumListPanel;
import org.ttdc.gwt.client.uibinder.forum.ForumPanel;
import org.ttdc.gwt.client.uibinder.forum.ForumPostPanel;
import org.ttdc.gwt.client.uibinder.home.HomePanel;
import org.ttdc.gwt.client.uibinder.home.TrafficPersonPanel;
import org.ttdc.gwt.client.uibinder.movies.MovieListPanel;
import org.ttdc.gwt.client.uibinder.post.ChildPostPanel;
import org.ttdc.gwt.client.uibinder.post.IconOptionsPanel;
import org.ttdc.gwt.client.uibinder.post.MoreOptionsPopupPanel;
import org.ttdc.gwt.client.uibinder.post.NestedPostSpacerPanel;
import org.ttdc.gwt.client.uibinder.post.NewMoviePanel;
import org.ttdc.gwt.client.uibinder.post.PlainPostPanel;
import org.ttdc.gwt.client.uibinder.post.PostDetailPanel;
import org.ttdc.gwt.client.uibinder.post.PostExpanded;
import org.ttdc.gwt.client.uibinder.post.PostOptionsListPanel;
import org.ttdc.gwt.client.uibinder.post.PostPanel;
import org.ttdc.gwt.client.uibinder.post.PostSummaryPanel;
import org.ttdc.gwt.client.uibinder.post.ReviewSummaryListPanel;
import org.ttdc.gwt.client.uibinder.post.ReviewSummaryPanel;
import org.ttdc.gwt.client.uibinder.post.SmallPostSummaryPanel;
import org.ttdc.gwt.client.uibinder.post.TagListPanel;
import org.ttdc.gwt.client.uibinder.post.NestedPostPanel;
import org.ttdc.gwt.client.uibinder.post.TopicPanel;
import org.ttdc.gwt.client.uibinder.search.RefineSearchPanel;
import org.ttdc.gwt.client.uibinder.search.SearchBoxPanel;
import org.ttdc.gwt.client.uibinder.search.SearchPanel;
import org.ttdc.gwt.client.uibinder.search.SearchResultsPanel;
import org.ttdc.gwt.client.uibinder.shared.PaginationNanoPanel;
import org.ttdc.gwt.client.uibinder.shared.PaginationPanel;
import org.ttdc.gwt.client.uibinder.shared.StandardFooterPanel;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.client.uibinder.shared.UserIdentityPanel;
import org.ttdc.gwt.client.uibinder.users.PublicUserProfilePanel;
import org.ttdc.gwt.client.uibinder.users.UserListPanel;
import org.ttdc.gwt.client.uibinder.users.UserToolsPanel;

import com.google.gwt.user.client.ui.HasWidgets;

public class MockInjector implements Injector{
	private PostPresenter postPresenter;
	private SearchPresenter searchPresenter;
	private PostCollectionPresenter postCollectionPresenter;
	private RpcServiceAsync service;
	private PostPresenter.View postView;
	private SearchPresenter.View searchView;
	private PostCollectionPresenter.View postCollectionView;
	private HyperlinkPresenter hyperlinkPresenter;
	private HyperlinkPresenter.View personLinkView;
	private TagRemovePresenter tagRemovePresenter;
	private TagRemovePresenter.View tagRemoveView;
	
	private SearchTagListPresenter searchTagListPresenter;
	private PostTagListPresenter postTagListPresenter;
	private TagListPresenterView commonTagListView;
	
	private TagCloudPresenter tagCloudPresenter;
	private TagCloudPresenter.View tagCloudView;
	
	private SearchTagResultsPresenter searchTagResultsPresenter;
	private SearchTagResultsPresenter.View searchTagResultsView;
		
	public Injector getInjector() {
		return this;
	}

	/**
	 * If no one sets the PostPresenter instance, just return a mocked one
	 * Might want to do this for the others too.
	 */
	public PostPresenter getPostPresenter() {
		if(postPresenter == null)
			return new PostPresenter(this);
		else
			return postPresenter;
	}

	public void setPostPresenter(PostPresenter postPresenter) {
		this.postPresenter = postPresenter;
	}

	public SearchPresenter getSearchPresenter() {
		return searchPresenter;
	}

	public void setSearchPresenter(SearchPresenter searchPresenter) {
		this.searchPresenter = searchPresenter;
	}

	public PostCollectionPresenter getPostCollectionPresenter() {
		if(postCollectionPresenter == null)
			return new PostCollectionPresenter(this);
		else
			return postCollectionPresenter;
	}

	public void setPostCollectionPresenter(PostCollectionPresenter postCollectionPresenter) {
		this.postCollectionPresenter = postCollectionPresenter;
	}

	public RpcServiceAsync getService() {
		return service;
	}

	public void setService(RpcServiceAsync service) {
		this.service = service;
	}

	//Probaly remove this after you get the real one working
	public PostPanelWidget getPostPanelWidget() {
		// TODO Auto-generated method stub
		return null;
	}

	public SugestionOracle getTagSugestionOracle() {
		// TODO Auto-generated method stub
		return null;
	}

	public PostCollectionPresenter.View getPostCollectionView() {
		if(postCollectionView == null){
			return buildMockPostCollectionView();
		}
		else{
			return postCollectionView;
		}
	}
	
	public PostPresenter.View getPostView() {
		if(postView == null){
			return buildMockPostView();
		}
		else{
			return postView;
		}
	}

	public SearchPresenter.View getSearchView() {
		if(searchView == null){
			return buildMockSearchView();
		}
		else{
			return searchView;
		}
	}

	public void setPostView(PostPresenter.View postView) {
		this.postView = postView;
	}

	public void setSearchView(SearchPresenter.View searchView) {
		this.searchView = searchView;
	}

	public void setPostCollectionView(PostCollectionPresenter.View postCollectionView) {
		this.postCollectionView = postCollectionView;
	}
	
	private PostPresenter.View buildMockPostView(){
		PostPresenter.View view = mock(PostPresenter.View.class);
		//when(view.getPostTitle()).thenReturn(new MockHasText());//Sometimes tests are more troulbe than they are worth.
		when(view.getPostEntry()).thenReturn(new MockHasText());
		HasWidgets childWidgetBucket = mock(HasWidgets.class);
		when(view.getChildWidgetBucket()).thenReturn(childWidgetBucket);
		return view;
	}
	
	private PostCollectionPresenter.View buildMockPostCollectionView(){
		PostCollectionPresenter.View collectionView = mock(PostCollectionPresenter.View.class);
		when(collectionView.getToggleExpandHandler()).thenReturn(new MockHasClickHandlers());
		
		HasWidgets mockHasWidgets = mock(HasWidgets.class);
		when(collectionView.getPostWidgets()).thenReturn(mockHasWidgets);
		return collectionView;
	}
	
	private SearchPresenter.View buildMockSearchView(){
		SearchPresenter.View searchView = mock(SearchPresenter.View.class);
		
		//TODO: Trevis stop your tests from falling apart :-(
//		when(searchView.getPhraseField()).thenReturn(new MockHasValue<String>());
//		when(searchView.getSummaryDetail()).thenReturn(new MockHasText());
//		when(searchView.getSearchButton()).thenReturn(new MockHasClickHandlers());
		return searchView;
	}
	
	private HyperlinkPresenter.View buildMockPersonLinkView(){
		HyperlinkPresenter.View v = mock(HyperlinkPresenter.View.class);
		//when(v.getPerson()).thenReturn(new GPerson());
		when(v.getHistoryToken()).thenReturn(new HistoryToken()); //"view=personDetails&personId=1234"
		when(v.getDisplayName()).thenReturn(new MockHasText());//"trevTest"
		when(v.getLinkHandlers()).thenReturn(new MockHasClickHandlers());
		//when(searchView.getSummaryDetail()).thenReturn(new MockHasText());
		//when(searchView.getSearchButton()).thenReturn(new MockHasClickHandlers());
		return v;
	}
	
	private TagRemovePresenter.View buildMockTagRemoveView(){
		TagRemovePresenter.View v = mock(TagRemovePresenter.View.class);
		//when(v.getPerson()).thenReturn(new GPerson());
		when(v.getRemoveTagClickHandler()).thenReturn(new MockHasClickHandlers()); //"view=personDetails&personId=1234"
		when(v.getTextTarget()).thenReturn(new MockHasText());//"trevTest"
		//when(searchView.getSummaryDetail()).thenReturn(new MockHasText());
		//when(searchView.getSearchButton()).thenReturn(new MockHasClickHandlers());
		return v;
	}
	
	
	
	
	
	private TagListPresenterView buildMockCommonTagListView(){
		TagListPresenterView v = mock(TagListPresenterView.class);
		return v;
	}

	public void setHyperlinkPresenter(HyperlinkPresenter hyperlinkPresenter) {
		this.hyperlinkPresenter = hyperlinkPresenter;
	}

	public HyperlinkPresenter getHyperlinkPresenter() {
		if(hyperlinkPresenter == null)
			return new HyperlinkPresenter(this);
		else
			return hyperlinkPresenter;
	}

	public HyperlinkPresenter.View getHyperlinkView() {
		if(personLinkView == null){
			return buildMockPersonLinkView();
		}
		else{
			return personLinkView;
		}
	}
	
	public void setHyperlinkView(HyperlinkPresenter.View personLinkView) {
		this.personLinkView = personLinkView;
	}

	public TagRemovePresenter getTagRemovePresenter() {
		if(tagRemovePresenter == null)
			return new TagRemovePresenter(this);
		else
			return tagRemovePresenter;
	}

	public void setTagRemovePresenter(TagRemovePresenter tagRemovePresenter) {
		this.tagRemovePresenter = tagRemovePresenter;
	}

	public TagRemovePresenter.View getTagRemoveView() {
		if(tagRemoveView == null){
			return buildMockTagRemoveView();
		}
		else{
			return tagRemoveView;
		}
	}

	public void setTagRemoveView(TagRemovePresenter.View tagRemoveView) {
		this.tagRemoveView = tagRemoveView;
	}

	
	public void setCommonTagListView(TagListPresenterView commonTagListView) {
		this.commonTagListView = commonTagListView;
	}
	public TagListPresenterView getCommonTagListView() {
		if(commonTagListView == null)
			return buildMockCommonTagListView();
		else
			return commonTagListView;
	}

	public void setPostTagListPresenter(PostTagListPresenter postTagListPresenter) {
		this.postTagListPresenter = postTagListPresenter;
	}
	public PostTagListPresenter getPostTagListPresenter() {
		if(postTagListPresenter == null)
			return new PostTagListPresenter(this);
		else
			return postTagListPresenter;
	}

	public void setSearchTagListPresenter(SearchTagListPresenter searchTagListPresenter) {
		this.searchTagListPresenter = searchTagListPresenter;
	}
	public SearchTagListPresenter getSearchTagListPresenter() {
		if(searchTagListPresenter == null)
			return new SearchTagListPresenter(this);
		else
			return searchTagListPresenter;
	}

	//TagCloud mock stuff
	
	@Override
	public TagCloudPresenter getTagCloudPresenter() {
		if(tagCloudPresenter == null)
			return new TagCloudPresenter(this);
		else
			return tagCloudPresenter;
	}

	public void setCommonTagListView(TagCloudPresenter.View tagCloudView) {
		this.tagCloudView = tagCloudView;
	}
	
	@Override
	public TagCloudPresenter.View getTagCloudView() {
		if(tagCloudView == null)
			return buildMockTagCloudView();
		else
			return tagCloudView;
	}
	
	public void setTagCloudView(TagCloudPresenter.View tagCloudView) {
		this.tagCloudView = tagCloudView;
	}
	
	public TagCloudPresenter.View buildMockTagCloudView() {
		TagCloudPresenter.View v = mock(TagCloudPresenter.View.class);
		
		return v;
	}

	//Browse TagResults
	@Override
	public SearchTagResultsPresenter getSearchTagResultsPresenter() {
		if(searchTagResultsPresenter == null)
			return new SearchTagResultsPresenter(this);
		else
			return searchTagResultsPresenter;
	}

	public void setSearchTagResultsPresenter(SearchTagResultsPresenter searchTagResultsPresenter) {
		this.searchTagResultsPresenter = searchTagResultsPresenter;
	}

	@Override
	public SearchTagResultsPresenter.View getSearchTagResultsView() {
		if(searchTagResultsView == null)
			return buildMockSearchTagResultsView();
		else
			return searchTagResultsView;
	}
	public SearchTagResultsPresenter.View buildMockSearchTagResultsView(){
		SearchTagResultsPresenter.View v = mock(SearchTagResultsPresenter.View.class);
		return v;
	}

	public void setSearchTagResultsView(SearchTagResultsPresenter.View searchTagResultsView) {
		this.searchTagResultsView = searchTagResultsView;
	}
	
	

	@Override
	public SearchResultsPresenter getSearchResultsPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.post.SearchResultsPresenter.View getSearchResultsView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SiteSearchPresenter getSiteSearchPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.post.SiteSearchPresenter.View getSiteSearchView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchWithinTaggedSubsetPresenter getSearchWithinTaggedSubsetPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchWithinTaggedSubsetPresenter.View getSearchWithinTaggedSubsetView() {
		// TODO Auto-generated method stub
		return null;
	}

	PaginationPresenter paginationPresenter = null; 
	@Override
	public PaginationPresenter getPaginationPresenter() {
		if(paginationPresenter == null)	
			paginationPresenter = new PaginationPresenter(this);
		return paginationPresenter;
	}

	PaginationView paginationView = null;
	@Override
	public View getPaginationView() {
		if(paginationView == null)	
			paginationView = mock(PaginationView.class);
		return paginationView;
	}

	@Override
	public CalendarPostPresenter getCalendarPostSummaryPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.calendar.CalendarPostPresenter.View getCalendarPostSummaryView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CalendarPresenter getCalendarPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.calendar.CalendarPresenter.View getCalendarView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DayPresenter getDayPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.calendar.DayPresenter.View getDayView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HourPresenter getHourPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.calendar.HourPresenter.View getHourView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WeekPresenter getWeekPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.calendar.WeekPresenter.View getWeekView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DayOfMonthPresenter getDayOfMonthPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.calendar.DayOfMonthPresenter.View getDayOfMonthView() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public YearPresenter getYearPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.calendar.YearPresenter.View getYearView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MonthDetailPresenter getMonthDetailPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.calendar.MonthDetailPresenter.View getMonthDetailView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MonthPresenter getMonthPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.calendar.MonthPresenter.View getMonthView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScaleSelectorPresenter getScaleSelectorPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.calendar.ScaleSelectorPresenter.View getScaleSelectorView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicFlatPresenter getTopicFlatPreseter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.topic.TopicFlatPresenter.View getTopicFlatView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicPresenter getTopicPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.topic.TopicPresenter.View getTopicView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicConversationPresenter getTopicConversationPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.topic.TopicConversationPresenter.View getTopicConversationView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicHierarchyPresenter getTopicHierarchyPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.topic.TopicHierarchyPresenter.View getTopicHierarchyView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchWithinSubsetPresenter getSearchWithinSubsetPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.post.SearchWithinSubsetPresenter.View getSearchWithinSubsetView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicNestedPresenter getTopicNestedPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.topic.TopicNestedPresenter.View getTopicNestedView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicSummaryPresenter getTopicSummaryPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.topic.TopicSummaryPresenter.View getTopicSummaryView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PostSummaryPresenter getPostSummaryPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.post.PostSummaryPresenter.View getPostSummaryView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MovieListPresenter getMovieListPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.movies.MovieListPresenter.View getMovieListView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MovieRatingPresenter getMovieRatingPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter.View getMovieRatingView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdminToolsPresenter getAdminToolsPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.admin.AdminToolsPresenter.View getAdminToolsView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageManagementPresenter getImageManaementPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.admin.ImageManagementPresenter.View getImageManagementView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageUploadPresenter getImageUploadPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.shared.ImageUploadPresenter.View getImageUploadView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImagePresenter getImagePresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.shared.ImagePresenter.View getImageView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageRowPresenter getImageRowPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.admin.ImageRowPresenter.View getImageRowView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LoginPresenter getLoginPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.users.LoginPresenter.View getLoginView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageMessagesPresenter getPageMessagesPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.shared.PageMessagesPresenter.View getPageMessagesView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserToolsPresenter getUserToolsPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.users.UserToolsPresenter.View getUserToolsView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIdentityPresenter getUserIdentityPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.shared.UserIdentityPresenter.View getUserIdentityView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserAdministrationPresenter getUserAdministrationPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.admin.UserAdministrationPresenter.View getUserAdministrationView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserPrivilegeGadgetPresenter getUserPrivilegeGadgetPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.admin.UserPrivilegeGadgetPresenter.View getUserPrivilegeGadgetView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonStatusGadgetPresenter getUserStateGadgetPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.admin.PersonStatusGadgetPresenter.View getPersonStatusGadgetView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ButtonPresenter getButtonPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.shared.ButtonPresenter.View getButtonView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DemoPresenter getDemoPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.demo.DemoPresenter.View getDemoView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserObjectTemplateEditorPresenter getUserObjectTemplateEditorPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.admin.UserObjectTemplateEditorPresenter.View getUserObjectTemplateEditorView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserObjectTemplateRowPresenter getUserObjectTemplateRowPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.admin.UserObjectTemplateRowPresenter.View getUserObjectTemplateRowView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StyleManagementPresenter getStyleManagementPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.admin.StyleManagementPresenter.View getStyleManagementView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StyleRowPresenter getStyleRowPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.admin.StyleRowPresenter.View getStyleRowView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CreateAccountPresenter getCreateAccountPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.users.CreateAccountPresenter.View getCreateAccountView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestPasswordResetPresenter getRequestPasswordPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.users.RequestPasswordResetPresenter.View getRequestPasswordView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserListPresenter getUserListPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.users.UserListPresenter.View getUserListView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserRowPresenter getUserRowPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.users.UserRowPresenter.View getUserRowView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatePresenter getDatePresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.shared.DatePresenter.View getDateView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PublicUserProfilePresenter getPublicUserProfilePresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.users.PublicUserProfilePresenter.View getPublicUserProfileView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDashboardPresenter getUserDashboardPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.dashboard.UserDashboardPresenter.View getUserDashboardView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProfilePresenter getUserProfilePresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.dashboard.ProfilePresenter.View getUserProfileView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericTabularFlowPresenter getGenericTabularFlowPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.shared.GenericTabularFlowPresenter.View getGenericTabularFlowView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MovieCoverWithRatingPresenter getMovieCoverWithRatingPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.shared.MovieCoverWithRatingPresenter.View getMovieCoverWithRatingView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextPresenter getTextPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.shared.TextPresenter.View getTextView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WaitPresenter getWaitPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.shared.WaitPresenter.View getWaitView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditProfilePresenter getEditProfilePresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.dashboard.EditProfilePresenter.View getEditProfileView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemovableWebLinkPresenter getRemovableWebLinkPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.dashboard.RemovableWebLinkPresenter.View getRemovableWebLinkView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResetPasswordPresenter getResetPasswordPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.dashboard.ResetPasswordPresenter.View getResetPasswordView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SettingsPresenter getSettingsPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.dashboard.SettingsPresenter.View getSettingsView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConversationPresenter getConversationPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.ConversationPresenter.View getConversationView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlatPresenter getFlatPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.FlatPresenter.View getFlatView() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public NestedPresenter getNestedPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.NestedPresenter.View getNestedView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ThreadPresenter getThreadPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.ThreadPresenter.View getThreadView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Home2Presenter getHome2Presenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.Home2Presenter.View getHome2View() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TrafficPersonPresenter getTrafficPersonPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.TrafficPersonPresenter.View getTrafficPersonView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TrafficPresenter getTrafficPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.TrafficPresenter.View getTrafficView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InteractiveCalendarPresenter getInteractiveCalendarPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.InteractiveCalendarPresenter.View getInteractiveCalendarView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CalendarSelectorModulePresenter getCalendarSelectorModulePresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.CalendarSelectorModulePresenter.View getCalendarSelectorModuleView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchBoxPresenter getSearchBoxPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.search.SearchBoxPresenter.View getSearchBoxView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchBoxDatePresenter getSearchBoxDatePresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.search.SearchBoxDatePresenter.View getSearchBoxDateView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NewCommentPresenter getNewCommentPresenter() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public org.ttdc.gwt.client.presenters.comments.NewCommentPresenter.View getNewCommentView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemovableTagPresenter getRemovableTagPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.comments.RemovableTagPresenter.View getRemovableTagView() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PostPanel createPostPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MoreOptionsPopupPanel createOptionsPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PostSummaryPanel createPostSummaryPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PostExpanded createPostExpanded() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReviewSummaryListPanel createReviewSummaryListPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReviewSummaryPanel createReviewSummaryPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SiteUpdatePanel createSiteUpdatePanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NewMoviePanel createNewMoviePanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Navigation createNavigation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public FilteredPost createFilteredPost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EarmarkedPresenter getEarmarkedPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.EarmarkedPresenter.View getEarmarkedView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LikesPresenter getLikesPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.post.LikesPresenter.View getLikesView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MoreLatestPresenter getMoreLatestPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.home.MoreLatestPresenter.View getMoreLatestView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MoreSearchPresenter getMoreSearchPresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.ttdc.gwt.client.presenters.users.MoreSearchPresenter.View getMoreSearchView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SmallPostSummaryPanel createSmallSummaryPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StandardPageHeaderPanel createStandardPageHeaderPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PopupCalendarDatePresenter getPopupCalendarDatePresenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PopupCalendarDatePresenter.View getPopupCalendarDateView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIdentityPanel createUserIdentityPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CalendarPanel createCalendarPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DayPanel createDayPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CalendarBreadCrumbPanel createBreadCrumbPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SmallMonthPanel createSmallMonthPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InteractiveCalendarPanel createInteractiveCalendarPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefineSearchPanel createRefineSearchPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchBoxPanel createSearchBoxPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResultsPanel createSearchResultsPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TagListPanel createTagListPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NestedPostPanel createNestedPostPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicPanel createTopicPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserListPanel createUserListPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MovieListPanel createMovieListPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDashboardPanel createUserDashboardPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserToolsPanel createUserToolsPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PublicUserProfilePanel createPublicUserProfilePanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HomePanel createHomePanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TrafficPersonPanel createTrafficPersonPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StandardFooterPanel createStandardFooter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchPanel createSearchPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NestedPostSpacerPanel createNestedPostSpacerPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommentEditorPanel createCommentEditorPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PostOptionsListPanel createPostOptionsListPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlainPostPanel createPlainPostPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildPostPanel createChildPostPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IconOptionsPanel createIconOptionPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PostDetailPanel createPostDetailPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForumPanel createForumPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginationPanel createPaginationPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginationNanoPanel createPaginationNanoPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForumListItemPanel createForumListItemPanel() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ForumListPanel createForumListPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForumPostPanel createForumPostPanel() {
		// TODO Auto-generated method stub
		return null;
	}
}