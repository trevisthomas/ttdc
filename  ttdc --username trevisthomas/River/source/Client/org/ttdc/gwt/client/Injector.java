package org.ttdc.gwt.client;

import org.ttdc.gwt.client.autocomplete.SuggestionOracle;
import org.ttdc.gwt.client.components.widgets.PostPanelWidget;
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
import org.ttdc.gwt.client.presenters.shared.PopupCalendarDatePresenter;
import org.ttdc.gwt.client.presenters.shared.TextPresenter;
import org.ttdc.gwt.client.presenters.shared.UserIdentityPresenter;
import org.ttdc.gwt.client.presenters.shared.WaitPresenter;
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
import org.ttdc.gwt.client.uibinder.comment.ReparentPanel;
import org.ttdc.gwt.client.uibinder.dashboard.FilteredPost;
import org.ttdc.gwt.client.uibinder.dashboard.UserDashboardPanel;
import org.ttdc.gwt.client.uibinder.forum.ForumListItemPanel;
import org.ttdc.gwt.client.uibinder.forum.ForumListPanel;
import org.ttdc.gwt.client.uibinder.forum.ForumPanel;
import org.ttdc.gwt.client.uibinder.forum.ForumPostPanel;
import org.ttdc.gwt.client.uibinder.home.CalendarPairPanel;
import org.ttdc.gwt.client.uibinder.home.HomePanel;
import org.ttdc.gwt.client.uibinder.home.SplitHomePanel;
import org.ttdc.gwt.client.uibinder.home.TrafficPanel;
import org.ttdc.gwt.client.uibinder.home.TrafficPersonAvatarOnlyPanel;
import org.ttdc.gwt.client.uibinder.home.TrafficPersonPanel;
import org.ttdc.gwt.client.uibinder.movies.MovieListPanel;
import org.ttdc.gwt.client.uibinder.post.ChildPostPanel;
import org.ttdc.gwt.client.uibinder.post.IconOptionsPanel;
import org.ttdc.gwt.client.uibinder.post.MoreOptionsPopupPanel;
import org.ttdc.gwt.client.uibinder.post.NestedPostPanel;
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
import org.ttdc.gwt.client.uibinder.post.RootPostPanel;
import org.ttdc.gwt.client.uibinder.post.SmallPostSummaryPanel;
import org.ttdc.gwt.client.uibinder.post.TagListPanel;
import org.ttdc.gwt.client.uibinder.post.TopicPanel;
import org.ttdc.gwt.client.uibinder.search.RefineSearchPanel;
import org.ttdc.gwt.client.uibinder.search.SearchBoxPanel;
import org.ttdc.gwt.client.uibinder.search.SearchPanel;
import org.ttdc.gwt.client.uibinder.search.SearchResultsPanel;
import org.ttdc.gwt.client.uibinder.shared.PageSizeComponent;
import org.ttdc.gwt.client.uibinder.shared.PaginationNanoPanel;
import org.ttdc.gwt.client.uibinder.shared.PaginationPanel;
import org.ttdc.gwt.client.uibinder.shared.SortOrderComponent;
import org.ttdc.gwt.client.uibinder.shared.StandardFooterPanel;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.client.uibinder.shared.UserIdentityPanel;
import org.ttdc.gwt.client.uibinder.users.PublicUserProfilePanel;
import org.ttdc.gwt.client.uibinder.users.UserListPanel;
import org.ttdc.gwt.client.uibinder.users.UserToolsPanel;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.user.client.ui.Widget;

@GinModules(InjectorModule.class)
public interface Injector extends Ginjector {  
	public RpcServiceAsync getService();
	
	public DemoPresenter.View getDemoView();
	public DemoPresenter getDemoPresenter();
	
	public SearchPresenter getSearchPresenter();
	public SearchPresenter.View getSearchView();
	
	public Injector getInjector();
	
	public PostPanelWidget getPostPanelWidget();//For testing
	
	public PostPresenter getPostPresenter();
	public PostPresenter.View getPostView();
	
	
	public PostCollectionPresenter getPostCollectionPresenter();
	public PostCollectionPresenter.View getPostCollectionView();
	
	public HyperlinkPresenter getHyperlinkPresenter();
	public HyperlinkPresenter.View getHyperlinkView();
	
	public SuggestionOracle getTagSugestionOracle();
	
	public TagRemovePresenter.View getTagRemoveView();
	public TagRemovePresenter getTagRemovePresenter();
	
	public PostTagListPresenter getPostTagListPresenter();
	public SearchTagListPresenter getSearchTagListPresenter();
	public TagListPresenterView getCommonTagListView();
	
	public TagCloudPresenter getTagCloudPresenter();
	public TagCloudPresenter.View getTagCloudView();
	
	public SearchResultsPresenter getSearchResultsPresenter();
	public SearchResultsPresenter.View getSearchResultsView();
	
	public SearchTagResultsPresenter.View getSearchTagResultsView();
	public SearchTagResultsPresenter getSearchTagResultsPresenter();
	
	public SiteSearchPresenter.View getSiteSearchView();
	public SiteSearchPresenter getSiteSearchPresenter();
	
	public SearchWithinTaggedSubsetPresenter.View getSearchWithinTaggedSubsetView();
	public SearchWithinTaggedSubsetPresenter getSearchWithinTaggedSubsetPresenter();
	
	public PaginationPresenter.View getPaginationView();
	public PaginationPresenter getPaginationPresenter();
	
	public CalendarPresenter.View getCalendarView();
	public CalendarPresenter getCalendarPresenter();
	
	public WeekPresenter.View getWeekView();
	public WeekPresenter getWeekPresenter();
	
	public DayPresenter.View getDayView();
	public DayPresenter getDayPresenter();
	
	public HourPresenter.View getHourView();
	public HourPresenter getHourPresenter();
	
	public CalendarPostPresenter.View getCalendarPostSummaryView();
	public CalendarPostPresenter getCalendarPostSummaryPresenter();
	
	public MonthPresenter.View getMonthView();
	public MonthPresenter getMonthPresenter();
	
	public MonthDetailPresenter.View getMonthDetailView();
	public MonthDetailPresenter getMonthDetailPresenter();
	
	public YearPresenter.View getYearView();
	public YearPresenter getYearPresenter();

	public DayOfMonthPresenter.View getDayOfMonthView();
	public DayOfMonthPresenter getDayOfMonthPresenter();

	public ScaleSelectorPresenter.View getScaleSelectorView();
	public ScaleSelectorPresenter getScaleSelectorPresenter();

	public TopicPresenter.View getTopicView();
	public TopicPresenter getTopicPresenter();

	public TopicFlatPresenter.View getTopicFlatView();
	public TopicFlatPresenter getTopicFlatPreseter();

	public TopicConversationPresenter.View getTopicConversationView();
	public TopicConversationPresenter getTopicConversationPresenter();

	public TopicHierarchyPresenter.View getTopicHierarchyView();
	public TopicHierarchyPresenter getTopicHierarchyPresenter();

	public TopicSummaryPresenter.View getTopicSummaryView();
	public TopicSummaryPresenter getTopicSummaryPresenter();

	public SearchWithinSubsetPresenter getSearchWithinSubsetPresenter();
	public SearchWithinSubsetPresenter.View getSearchWithinSubsetView();

	public TopicNestedPresenter.View getTopicNestedView();
	public TopicNestedPresenter getTopicNestedPresenter();

	public PostSummaryPresenter.View getPostSummaryView();
	public PostSummaryPresenter getPostSummaryPresenter();

	public MovieListPresenter.View getMovieListView();
	public MovieListPresenter getMovieListPresenter();

	public MovieRatingPresenter.View getMovieRatingView();
	public MovieRatingPresenter getMovieRatingPresenter();

	public ImageManagementPresenter.View getImageManagementView();
	public ImageManagementPresenter getImageManaementPresenter();

	public AdminToolsPresenter.View getAdminToolsView();
	public AdminToolsPresenter getAdminToolsPresenter();

	public ImageUploadPresenter.View getImageUploadView();
	public ImageUploadPresenter getImageUploadPresenter();

	public ImagePresenter.View getImageView();
	public ImagePresenter getImagePresenter();

	public ImageRowPresenter.View getImageRowView();
	public ImageRowPresenter getImageRowPresenter();

	public UserToolsPresenter.View getUserToolsView();
	public UserToolsPresenter getUserToolsPresenter();

	public PageMessagesPresenter getPageMessagesPresenter();
	public PageMessagesPresenter.View getPageMessagesView();

	public LoginPresenter getLoginPresenter();
	public LoginPresenter.View getLoginView();

	public UserIdentityPresenter.View getUserIdentityView();
	public UserIdentityPresenter getUserIdentityPresenter();

	public PersonStatusGadgetPresenter.View getPersonStatusGadgetView();
	public PersonStatusGadgetPresenter getUserStateGadgetPresenter();

	public UserPrivilegeGadgetPresenter.View getUserPrivilegeGadgetView();
	public UserPrivilegeGadgetPresenter getUserPrivilegeGadgetPresenter();

	public UserAdministrationPresenter.View getUserAdministrationView();
	public UserAdministrationPresenter getUserAdministrationPresenter();

	public ButtonPresenter.View getButtonView();
	public ButtonPresenter getButtonPresenter();

	public UserObjectTemplateRowPresenter.View getUserObjectTemplateRowView();
	public UserObjectTemplateRowPresenter getUserObjectTemplateRowPresenter();

	public UserObjectTemplateEditorPresenter.View getUserObjectTemplateEditorView();
	public UserObjectTemplateEditorPresenter getUserObjectTemplateEditorPresenter();

	public StyleRowPresenter.View getStyleRowView();
	public StyleRowPresenter getStyleRowPresenter();

	public StyleManagementPresenter.View getStyleManagementView();
	public StyleManagementPresenter getStyleManagementPresenter();

	public CreateAccountPresenter.View getCreateAccountView();
	public CreateAccountPresenter getCreateAccountPresenter();

	public RequestPasswordResetPresenter.View getRequestPasswordView();
	public RequestPasswordResetPresenter getRequestPasswordPresenter();

	public UserRowPresenter.View getUserRowView();
	public UserRowPresenter getUserRowPresenter();

	public UserListPresenter.View getUserListView();
	public UserListPresenter getUserListPresenter();

	public DatePresenter.View getDateView();
	public DatePresenter getDatePresenter();

	public PublicUserProfilePresenter.View getPublicUserProfileView();
	public PublicUserProfilePresenter getPublicUserProfilePresenter();

	public ProfilePresenter.View getUserProfileView();
	public ProfilePresenter getUserProfilePresenter();
	
	public UserDashboardPresenter.View getUserDashboardView();
	public UserDashboardPresenter getUserDashboardPresenter();

	public MovieCoverWithRatingPresenter.View getMovieCoverWithRatingView();
	public MovieCoverWithRatingPresenter getMovieCoverWithRatingPresenter();

	public GenericTabularFlowPresenter.View getGenericTabularFlowView();
	public GenericTabularFlowPresenter getGenericTabularFlowPresenter();

	public WaitPresenter.View getWaitView();
	public WaitPresenter getWaitPresenter();

	public TextPresenter.View getTextView();
	public TextPresenter getTextPresenter();

	public EditProfilePresenter.View getEditProfileView();
	public EditProfilePresenter getEditProfilePresenter();

	public RemovableWebLinkPresenter.View getRemovableWebLinkView();
	public RemovableWebLinkPresenter getRemovableWebLinkPresenter();

	public ResetPasswordPresenter.View getResetPasswordView();
	public ResetPasswordPresenter getResetPasswordPresenter();

	public SettingsPresenter.View getSettingsView();
	public SettingsPresenter getSettingsPresenter();

	public ConversationPresenter.View getConversationView();
	public ConversationPresenter getConversationPresenter();
	
	public NestedPresenter.View getNestedView();
	public NestedPresenter getNestedPresenter();
	
	public FlatPresenter.View getFlatView();
	public FlatPresenter getFlatPresenter();
	
	public ThreadPresenter.View getThreadView();
	public ThreadPresenter getThreadPresenter();
	
	public Home2Presenter getHome2Presenter();
	public Home2Presenter.View getHome2View();

	public TrafficPersonPresenter.View getTrafficPersonView();
	public TrafficPersonPresenter getTrafficPersonPresenter();

	public TrafficPresenter.View getTrafficView();
	public TrafficPresenter getTrafficPresenter();

	public InteractiveCalendarPresenter.View getInteractiveCalendarView();
	public InteractiveCalendarPresenter getInteractiveCalendarPresenter();

	public CalendarSelectorModulePresenter.View getCalendarSelectorModuleView();
	public CalendarSelectorModulePresenter getCalendarSelectorModulePresenter();

	public SearchBoxPresenter.View getSearchBoxView();
	public SearchBoxPresenter getSearchBoxPresenter();

	public SearchBoxDatePresenter.View getSearchBoxDateView();
	public SearchBoxDatePresenter getSearchBoxDatePresenter();

	public NewCommentPresenter.View getNewCommentView();
	public NewCommentPresenter getNewCommentPresenter();

	public RemovableTagPresenter.View getRemovableTagView();
	public RemovableTagPresenter getRemovableTagPresenter();
	
	public EarmarkedPresenter.View getEarmarkedView();
	public EarmarkedPresenter getEarmarkedPresenter();
	
	public LikesPresenter.View getLikesView();
	public LikesPresenter getLikesPresenter();
	
	public MoreLatestPresenter.View getMoreLatestView();
	public MoreLatestPresenter getMoreLatestPresenter();
	
	public MoreSearchPresenter.View getMoreSearchView();
	public MoreSearchPresenter getMoreSearchPresenter();
	
	public PopupCalendarDatePresenter.View getPopupCalendarDateView();
	public PopupCalendarDatePresenter getPopupCalendarDatePresenter();
	
	//
	public PostPanel createPostPanel();
	public MoreOptionsPopupPanel createOptionsPanel();
	public PostSummaryPanel createPostSummaryPanel();
	public PostExpanded createPostExpanded();
	public ReviewSummaryListPanel createReviewSummaryListPanel();
	public ReviewSummaryPanel createReviewSummaryPanel();
	public SiteUpdatePanel createSiteUpdatePanel();
	public NewMoviePanel createNewMoviePanel();
	public Navigation createNavigation();
	public FilteredPost createFilteredPost();
	public SmallPostSummaryPanel createSmallSummaryPanel();
	public StandardPageHeaderPanel createStandardPageHeaderPanel();
	public UserIdentityPanel createUserIdentityPanel();
	public CalendarPanel createCalendarPanel();
	public DayPanel createDayPanel();
	public CalendarBreadCrumbPanel createBreadCrumbPanel();
	public SmallMonthPanel createSmallMonthPanel();
	public InteractiveCalendarPanel createInteractiveCalendarPanel();
	public RefineSearchPanel createRefineSearchPanel();
	public SearchBoxPanel createSearchBoxPanel();
	public SearchResultsPanel createSearchResultsPanel();
	public TagListPanel createTagListPanel();
	public NestedPostPanel createNestedPostPanel();
	public TopicPanel createTopicPanel();
	public UserListPanel createUserListPanel();
	public MovieListPanel createMovieListPanel();
	public UserDashboardPanel createUserDashboardPanel();
	public UserToolsPanel createUserToolsPanel();
	public PublicUserProfilePanel createPublicUserProfilePanel();
	public HomePanel createHomePanel();
	public TrafficPersonPanel createTrafficPersonPanel();
	public StandardFooterPanel createStandardFooter();
	public SearchPanel createSearchPanel();
	public NestedPostSpacerPanel createNestedPostSpacerPanel();
	public CommentEditorPanel createCommentEditorPanel();
	public PostOptionsListPanel createPostOptionsListPanel();
	public PlainPostPanel createPlainPostPanel();
	public ChildPostPanel createChildPostPanel();
	public IconOptionsPanel createIconOptionPanel();
	public PostDetailPanel createPostDetailPanel();
	public ForumPanel createForumPanel();
	public PaginationPanel createPaginationPanel();
	public PaginationNanoPanel createPaginationNanoPanel();
	
	public ForumListItemPanel createForumListItemPanel();
	public ForumListPanel createForumListPanel();
	public ForumPostPanel createForumPostPanel();
	public ReparentPanel createReparentPanel();
	public PageSizeComponent createPageSizeComponent();
	public SortOrderComponent createSortOrderComponent();
	public SplitHomePanel createSplitHomePanel();
	public TrafficPanel createTrafficPanel();

	public TrafficPersonAvatarOnlyPanel createTrafficPersonAvatarOnlyPanel();

	public CalendarPairPanel createCalendarPairPanel();

	public RootPostPanel createRootPostPanel();
}

