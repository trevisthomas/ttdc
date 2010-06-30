package org.ttdc.gwt.client;

import org.ttdc.gwt.client.presenters.admin.AdminToolsPresenter;
import org.ttdc.gwt.client.presenters.admin.AdminToolsView;
import org.ttdc.gwt.client.presenters.admin.ImageManagementPresenter;
import org.ttdc.gwt.client.presenters.admin.ImageManagementView;
import org.ttdc.gwt.client.presenters.admin.ImageRowPresenter;
import org.ttdc.gwt.client.presenters.admin.ImageRowView;
import org.ttdc.gwt.client.presenters.admin.PersonStatusGadgetPresenter;
import org.ttdc.gwt.client.presenters.admin.PersonStatusGadgetView;
import org.ttdc.gwt.client.presenters.admin.StyleManagementPresenter;
import org.ttdc.gwt.client.presenters.admin.StyleManagementView;
import org.ttdc.gwt.client.presenters.admin.StyleRowPresenter;
import org.ttdc.gwt.client.presenters.admin.StyleRowView;
import org.ttdc.gwt.client.presenters.admin.UserAdministrationPresenter;
import org.ttdc.gwt.client.presenters.admin.UserAdministrationView;
import org.ttdc.gwt.client.presenters.admin.UserObjectTemplateEditorPresenter;
import org.ttdc.gwt.client.presenters.admin.UserObjectTemplateEditorView;
import org.ttdc.gwt.client.presenters.admin.UserObjectTemplateRowPresenter;
import org.ttdc.gwt.client.presenters.admin.UserObjectTemplateRowView;
import org.ttdc.gwt.client.presenters.admin.UserPrivilegeGadgetPresenter;
import org.ttdc.gwt.client.presenters.admin.UserPrivilegeGadgetView;
import org.ttdc.gwt.client.presenters.calendar.CalendarPostPresenter;
import org.ttdc.gwt.client.presenters.calendar.CalendarPostView;
import org.ttdc.gwt.client.presenters.calendar.CalendarPresenter;
import org.ttdc.gwt.client.presenters.calendar.CalendarView;
import org.ttdc.gwt.client.presenters.calendar.DayOfMonthPresenter;
import org.ttdc.gwt.client.presenters.calendar.DayOfMonthView;
import org.ttdc.gwt.client.presenters.calendar.DayPresenter;
import org.ttdc.gwt.client.presenters.calendar.DayView;
import org.ttdc.gwt.client.presenters.calendar.HourPresenter;
import org.ttdc.gwt.client.presenters.calendar.HourView;
import org.ttdc.gwt.client.presenters.calendar.MonthDetailPresenter;
import org.ttdc.gwt.client.presenters.calendar.MonthDetailView;
import org.ttdc.gwt.client.presenters.calendar.MonthPresenter;
import org.ttdc.gwt.client.presenters.calendar.MonthView;
import org.ttdc.gwt.client.presenters.calendar.ScaleSelectorPresenter;
import org.ttdc.gwt.client.presenters.calendar.ScaleSelectorView;
import org.ttdc.gwt.client.presenters.calendar.WeekPresenter;
import org.ttdc.gwt.client.presenters.calendar.WeekView;
import org.ttdc.gwt.client.presenters.calendar.YearPresenter;
import org.ttdc.gwt.client.presenters.calendar.YearView;
import org.ttdc.gwt.client.presenters.comments.NewCommentPresenter;
import org.ttdc.gwt.client.presenters.comments.NewCommentView;
import org.ttdc.gwt.client.presenters.comments.RemovableTagPresenter;
import org.ttdc.gwt.client.presenters.comments.RemovableTagView;
import org.ttdc.gwt.client.presenters.dashboard.EditProfilePresenter;
import org.ttdc.gwt.client.presenters.dashboard.EditProfileView;
import org.ttdc.gwt.client.presenters.dashboard.ProfilePresenter;
import org.ttdc.gwt.client.presenters.dashboard.ProfileView;
import org.ttdc.gwt.client.presenters.dashboard.RemovableWebLinkPresenter;
import org.ttdc.gwt.client.presenters.dashboard.RemovableWebLinkView;
import org.ttdc.gwt.client.presenters.dashboard.ResetPasswordPresenter;
import org.ttdc.gwt.client.presenters.dashboard.ResetPasswordView;
import org.ttdc.gwt.client.presenters.dashboard.SettingsPresenter;
import org.ttdc.gwt.client.presenters.dashboard.SettingsView;
import org.ttdc.gwt.client.presenters.dashboard.UserDashboardPresenter;
import org.ttdc.gwt.client.presenters.dashboard.UserDashboardView;
import org.ttdc.gwt.client.presenters.demo.DemoPresenter;
import org.ttdc.gwt.client.presenters.demo.DemoView;
import org.ttdc.gwt.client.presenters.home.CalendarSelectorModulePresenter;
import org.ttdc.gwt.client.presenters.home.CalendarSelectorModuleView;
import org.ttdc.gwt.client.presenters.home.ConversationPresenter;
import org.ttdc.gwt.client.presenters.home.ConversationView;
import org.ttdc.gwt.client.presenters.home.EarmarkedPresenter;
import org.ttdc.gwt.client.presenters.home.EarmarkedView;
import org.ttdc.gwt.client.presenters.home.FlatPresenter;
import org.ttdc.gwt.client.presenters.home.FlatView;
import org.ttdc.gwt.client.presenters.home.Home2Presenter;
import org.ttdc.gwt.client.presenters.home.Home2View;
import org.ttdc.gwt.client.presenters.home.InteractiveCalendarPresenter;
import org.ttdc.gwt.client.presenters.home.InteractiveCalendarView;
import org.ttdc.gwt.client.presenters.home.MoreLatestPresenter;
import org.ttdc.gwt.client.presenters.home.MoreLatestView;
import org.ttdc.gwt.client.presenters.home.NestedPresenter;
import org.ttdc.gwt.client.presenters.home.NestedView;
import org.ttdc.gwt.client.presenters.home.ThreadPresenter;
import org.ttdc.gwt.client.presenters.home.ThreadView;
import org.ttdc.gwt.client.presenters.home.TrafficPersonPresenter;
import org.ttdc.gwt.client.presenters.home.TrafficPersonView;
import org.ttdc.gwt.client.presenters.home.TrafficPresenter;
import org.ttdc.gwt.client.presenters.home.TrafficView;
import org.ttdc.gwt.client.presenters.movies.MovieListPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieListView;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieRatingView;
import org.ttdc.gwt.client.presenters.post.LikesPresenter;
import org.ttdc.gwt.client.presenters.post.LikesView;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostCollectionView;
import org.ttdc.gwt.client.presenters.post.PostPresenter;
import org.ttdc.gwt.client.presenters.post.PostSummaryPresenter;
import org.ttdc.gwt.client.presenters.post.PostSummaryView;
import org.ttdc.gwt.client.presenters.post.PostView;
import org.ttdc.gwt.client.presenters.post.SearchPresenter;
import org.ttdc.gwt.client.presenters.post.SearchResultsPresenter;
import org.ttdc.gwt.client.presenters.post.SearchResultsView;
import org.ttdc.gwt.client.presenters.post.SearchTagResultsPresenter;
import org.ttdc.gwt.client.presenters.post.SearchTagResultsView;
import org.ttdc.gwt.client.presenters.post.SearchView;
import org.ttdc.gwt.client.presenters.post.SearchWithinSubsetPresenter;
import org.ttdc.gwt.client.presenters.post.SearchWithinSubsetView;
import org.ttdc.gwt.client.presenters.post.SearchWithinTaggedSubsetPresenter;
import org.ttdc.gwt.client.presenters.post.SearchWithinTaggedSubsetView;
import org.ttdc.gwt.client.presenters.post.SiteSearchPresenter;
import org.ttdc.gwt.client.presenters.post.SiteSearchView;
import org.ttdc.gwt.client.presenters.post.TagCloudPresenter;
import org.ttdc.gwt.client.presenters.post.TagCloudView;
import org.ttdc.gwt.client.presenters.post.TagRemovePresenter;
import org.ttdc.gwt.client.presenters.post.TagRemoveView;
import org.ttdc.gwt.client.presenters.search.SearchBoxDatePresenter;
import org.ttdc.gwt.client.presenters.search.SearchBoxDateView;
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.search.SearchBoxView;
import org.ttdc.gwt.client.presenters.shared.ButtonPresenter;
import org.ttdc.gwt.client.presenters.shared.ButtonView;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.DateView;
import org.ttdc.gwt.client.presenters.shared.GenericTabularFlowPresenter;
import org.ttdc.gwt.client.presenters.shared.GenericTabularFlowView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkView;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.shared.ImageUploadPresenter;
import org.ttdc.gwt.client.presenters.shared.ImageUploadView;
import org.ttdc.gwt.client.presenters.shared.ImageView;
import org.ttdc.gwt.client.presenters.shared.MovieCoverWithRatingPresenter;
import org.ttdc.gwt.client.presenters.shared.MovieCoverWithRatingView;
import org.ttdc.gwt.client.presenters.shared.PageMessagesPresenter;
import org.ttdc.gwt.client.presenters.shared.PageMessagesView;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationView;
import org.ttdc.gwt.client.presenters.shared.TextPresenter;
import org.ttdc.gwt.client.presenters.shared.TextView;
import org.ttdc.gwt.client.presenters.shared.UserIdentityPresenter;
import org.ttdc.gwt.client.presenters.shared.UserIdentityView;
import org.ttdc.gwt.client.presenters.shared.WaitPresenter;
import org.ttdc.gwt.client.presenters.shared.WaitView;
import org.ttdc.gwt.client.presenters.tag.CommonTagListView;
import org.ttdc.gwt.client.presenters.tag.TagListPresenterView;
import org.ttdc.gwt.client.presenters.topic.TopicConversationPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicConversationView;
import org.ttdc.gwt.client.presenters.topic.TopicFlatPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicFlatView;
import org.ttdc.gwt.client.presenters.topic.TopicHierarchyPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicHierarchyView;
import org.ttdc.gwt.client.presenters.topic.TopicNestedPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicNestedView;
import org.ttdc.gwt.client.presenters.topic.TopicPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicSummaryPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicSummaryView;
import org.ttdc.gwt.client.presenters.topic.TopicView;
import org.ttdc.gwt.client.presenters.users.CreateAccountPresenter;
import org.ttdc.gwt.client.presenters.users.CreateAccountView;
import org.ttdc.gwt.client.presenters.users.LoginPresenter;
import org.ttdc.gwt.client.presenters.users.LoginView;
import org.ttdc.gwt.client.presenters.users.MoreSearchPresenter;
import org.ttdc.gwt.client.presenters.users.MoreSearchView;
import org.ttdc.gwt.client.presenters.users.PublicUserProfilePresenter;
import org.ttdc.gwt.client.presenters.users.PublicUserProfileView;
import org.ttdc.gwt.client.presenters.users.RequestPasswordResetPresenter;
import org.ttdc.gwt.client.presenters.users.RequestPasswordResetView;
import org.ttdc.gwt.client.presenters.users.UserListPresenter;
import org.ttdc.gwt.client.presenters.users.UserListView;
import org.ttdc.gwt.client.presenters.users.UserRowPresenter;
import org.ttdc.gwt.client.presenters.users.UserRowView;
import org.ttdc.gwt.client.presenters.users.UserToolsPresenter;
import org.ttdc.gwt.client.presenters.users.UserToolsView;
import org.ttdc.gwt.client.services.RpcServiceAsync;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class InjectorModule extends AbstractGinModule {
	protected void configure() {
		//Singleton presenters
		bind(RpcServiceAsync.class).in(Singleton.class);
		bind(TopicConversationPresenter.class).in(Singleton.class);
		bind(TopicNestedPresenter.class).in(Singleton.class);
		bind(PublicUserProfilePresenter.class).in(Singleton.class);
		bind(CalendarPresenter.class).in(Singleton.class);
		
		//bind .to .in
		bind(DemoPresenter.View.class).to(DemoView.class);
		bind(SearchPresenter.View.class).to(SearchView.class);
		
		bind(PostCollectionPresenter.View.class).to(PostCollectionView.class);
		bind(PostPresenter.View.class).to(PostView.class);
		bind(HyperlinkPresenter.View.class).to(HyperlinkView.class);
		bind(TagRemovePresenter.View.class).to(TagRemoveView.class);
		bind(TagListPresenterView.class).to(CommonTagListView.class);
		bind(TagCloudPresenter.View.class).to(TagCloudView.class);
		bind(SearchTagResultsPresenter.View.class).to(SearchTagResultsView.class);
		bind(SearchResultsPresenter.View.class).to(SearchResultsView.class);
		bind(SiteSearchPresenter.View.class).to(SiteSearchView.class);
		bind(SearchWithinTaggedSubsetPresenter.View.class).to(SearchWithinTaggedSubsetView.class);
		bind(PaginationPresenter.View.class).to(PaginationView.class);
		bind(CalendarPresenter.View.class).to(CalendarView.class);
		bind(WeekPresenter.View.class).to(WeekView.class);
		bind(DayPresenter.View.class).to(DayView.class);
		bind(HourPresenter.View.class).to(HourView.class);
		bind(CalendarPostPresenter.View.class).to(CalendarPostView.class);
		bind(MonthDetailPresenter.View.class).to(MonthDetailView.class);
		bind(MonthPresenter.View.class).to(MonthView.class);
		bind(YearPresenter.View.class).to(YearView.class);
		bind(DayOfMonthPresenter.View.class).to(DayOfMonthView.class);
		bind(ScaleSelectorPresenter.View.class).to(ScaleSelectorView.class);
		bind(TopicPresenter.View.class).to(TopicView.class);
		bind(TopicFlatPresenter.View.class).to(TopicFlatView.class);
		bind(TopicConversationPresenter.View.class).to(TopicConversationView.class);
		bind(TopicHierarchyPresenter.View.class).to(TopicHierarchyView.class);
		bind(SearchWithinSubsetPresenter.View.class).to(SearchWithinSubsetView.class);
		bind(TopicNestedPresenter.View.class).to(TopicNestedView.class);
		bind(TopicSummaryPresenter.View.class).to(TopicSummaryView.class);
		bind(PostSummaryPresenter.View.class).to(PostSummaryView.class);
		bind(MovieListPresenter.View.class).to(MovieListView.class);
		bind(MovieRatingPresenter.View.class).to(MovieRatingView.class);
		bind(AdminToolsPresenter.View.class).to(AdminToolsView.class);
		bind(ImageManagementPresenter.View.class).to(ImageManagementView.class);
		bind(ImageUploadPresenter.View.class).to(ImageUploadView.class);
		bind(ImagePresenter.View.class).to(ImageView.class);
		bind(ImageRowPresenter.View.class).to(ImageRowView.class);
		bind(LoginPresenter.View.class).to(LoginView.class);
		bind(PageMessagesPresenter.View.class).to(PageMessagesView.class);
		bind(UserToolsPresenter.View.class).to(UserToolsView.class);
		bind(UserIdentityPresenter.View.class).to(UserIdentityView.class);
		
		bind(UserAdministrationPresenter.View.class).to(UserAdministrationView.class);
		bind(UserPrivilegeGadgetPresenter.View.class).to(UserPrivilegeGadgetView.class);
		bind(PersonStatusGadgetPresenter.View.class).to(PersonStatusGadgetView.class);
		bind(ButtonPresenter.View.class).to(ButtonView.class);
		
		bind(UserObjectTemplateRowPresenter.View.class).to(UserObjectTemplateRowView.class);
		bind(UserObjectTemplateEditorPresenter.View.class).to(UserObjectTemplateEditorView.class);
		bind(StyleRowPresenter.View.class).to(StyleRowView.class);
		bind(StyleManagementPresenter.View.class).to(StyleManagementView.class);
		bind(CreateAccountPresenter.View.class).to(CreateAccountView.class);
		bind(RequestPasswordResetPresenter.View.class).to(RequestPasswordResetView.class);
		bind(UserRowPresenter.View.class).to(UserRowView.class);
		bind(UserListPresenter.View.class).to(UserListView.class);
		bind(DatePresenter.View.class).to(DateView.class);
		
		bind(UserDashboardPresenter.View.class).to(UserDashboardView.class);
		bind(PublicUserProfilePresenter.View.class).to(PublicUserProfileView.class);
		bind(ProfilePresenter.View.class).to(ProfileView.class);
		bind(MovieCoverWithRatingPresenter.View.class).to(MovieCoverWithRatingView.class);
		bind(GenericTabularFlowPresenter.View.class).to(GenericTabularFlowView.class);
		bind(WaitPresenter.View.class).to(WaitView.class);
		bind(TextPresenter.View.class).to(TextView.class);
		bind(EditProfilePresenter.View.class).to(EditProfileView.class);
		bind(RemovableWebLinkPresenter.View.class).to(RemovableWebLinkView.class);
		bind(ResetPasswordPresenter.View.class).to(ResetPasswordView.class);
		bind(SettingsPresenter.View.class).to(SettingsView.class);
		
		bind(Home2Presenter.View.class).to(Home2View.class);
		bind(ConversationPresenter.View.class).to(ConversationView.class);
		bind(ThreadPresenter.View.class).to(ThreadView.class);
		bind(FlatPresenter.View.class).to(FlatView.class);
		bind(NestedPresenter.View.class).to(NestedView.class);
		bind(TrafficPresenter.View.class).to(TrafficView.class);
		bind(TrafficPersonPresenter.View.class).to(TrafficPersonView.class);
		bind(InteractiveCalendarPresenter.View.class).to(InteractiveCalendarView.class);
		bind(CalendarSelectorModulePresenter.View.class).to(CalendarSelectorModuleView.class);
		bind(SearchBoxPresenter.View.class).to(SearchBoxView.class);
		bind(SearchBoxDatePresenter.View.class).to(SearchBoxDateView.class);
		bind(NewCommentPresenter.View.class).to(NewCommentView.class);
		bind(RemovableTagPresenter.View.class).to(RemovableTagView.class);
		bind(EarmarkedPresenter.View.class).to(EarmarkedView.class);
		bind(LikesPresenter.View.class).to(LikesView.class);
		bind(MoreLatestPresenter.View.class).to(MoreLatestView.class);
		bind(MoreSearchPresenter.View.class).to(MoreSearchView.class);
				
	}
}
