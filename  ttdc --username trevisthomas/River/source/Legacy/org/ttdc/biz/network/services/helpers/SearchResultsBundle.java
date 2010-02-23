package org.ttdc.biz.network.services.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.TagLite;
import org.ttdc.util.web.HTMLCalendar;
import org.ttdc.util.web.Month;

public class SearchResultsBundle {
	public static final int MAX_INITIAL_TAG_SUGESTIONS = 20;
	public static final int MAX_INITIAL_THREAD_COUNT = 5;
	public static final int MAX_INITIAL_POST_COUNT = 5;
	public static final int MAX_INITIAL_TAG_COUNT = 5;
	
	private List<Post> posts = new ArrayList<Post>();
	private List<Post> threads = new ArrayList<Post>();
	private List<TagLite> suggestions = new ArrayList<TagLite>();
	private List<TagLite> tags = new ArrayList<TagLite>();
	private List<TagLite> suggestionsPeople = new ArrayList<TagLite>();
	private List<TagLite> suggestionsMonths = new ArrayList<TagLite>();
	private List<TagLite> suggestionsYears = new ArrayList<TagLite>();
	private List<TagLite> suggestionsDays = new ArrayList<TagLite>();
		
	private int nwsCount = 0; 
	private int infCount = 0;
	private int reviewCount = 0;
	private int linkCount = 0;
	private int movieCount = 0;
	private int privateCount = 0;
	
	private int totalPosts;
	private int totalThreads;
	private int totalSuggestions;
	private int totalTags;
	
	private TagLite tagReview;
	private TagLite tagInf;
	private TagLite tagNws;
	private TagLite tagLink;
	private TagLite tagMovie;
	private TagLite tagPrivate;
	
	private String searchPhrase = null;
	
	public final static String MODE_FULL_THREAD = "fullThreadMode";
	public final static String MODE_FULL_POST = "fullPostMode";
	public final static String MODE_TAG_BROWSER_SUMMARY = "tagBrowserSummaryMode";
	public final static String MODE_SEARCH_SUMMARY = "searchSummaryMode";
	
	private String mode;
	private String threadId; //Thread tag for when the SearchResultsBundle is driving the Thread view.
	private String threadTitle; //Title of the thread if results are limited to a thread
	
	private boolean showCalender = false;
	
	private Month month;
		
	public String getMODE_FULL_THREAD() {
		return MODE_FULL_THREAD;
	}
	public String getMODE_FULL_POST() {
		return MODE_FULL_POST;
	}
	public String getMODE_TAG_BROWSER_SUMMARY() {
		return MODE_TAG_BROWSER_SUMMARY;
	}
	public String getMODE_SEARCH_SUMMARY(){
		return MODE_SEARCH_SUMMARY;
	}
	public List<Post> getPosts() {
		return posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
	public List<Post> getThreads() {
		return threads;
	}
	public void setThreads(List<Post> threads) {
		this.threads = threads;
	}
	public List<TagLite> getSuggestions() {
		return suggestions;
	}
	public void setSuggestions(List<TagLite> suggestions) {
		this.suggestions = suggestions;
	}
	
	public boolean getHasPosts(){
		if(posts != null){
			return posts.size() > 0;
		}
		return false;
	}
	public boolean getHasThreads(){
		if(threads != null){
			return threads.size() > 0;
		}
		return false;
	}
	public boolean getHasSuggestions(){
		if(suggestions != null){
			return suggestions.size() > 0;
		}
		return false;
	}
	
	
	public int getCurrentPosts() {
		return getPosts().size();
	}
	public void setCurrentPosts(int currentPosts) {
		//Do nothinhg
	}
	public int getCurrentThreads() {
		return getThreads().size();
	}
	public void setCurrentThreads(int currentThreads) {
		//Do nothing
	}
	public int getTotalPosts() {
		return totalPosts;
	}
	public void setTotalPosts(int totalPosts) {
		this.totalPosts = totalPosts;
	}
	public int getTotalThreads() {
		return totalThreads;
	}
	public void setTotalThreads(int totalThreads) {
		this.totalThreads = totalThreads;
	}
	public int getTotalSuggestions() {
		return totalSuggestions;
	}
	public void setTotalSuggestions(int totalSugestions) {
		this.totalSuggestions = totalSugestions;
	}
	
	public boolean isPostsExceedThreshold() {
		if(getHasPosts())
			return getTotalPosts() > getPosts().size();
		else 
			return false;
	}
	public void setPostsExceedThreshold(boolean postsExceedThreshold) {
		//DoNothing
	}
	public boolean isThreadsExceedThreshold() {
		if(getHasThreads())
			return getTotalThreads() > getThreads().size();
		else 
			return false;
	}
	public void setThreadsExceedThreshold(boolean threadsExceedThreshold) {
		//DoNothing
	}
	public boolean isSuggestionsExceedThreshold() {
		if(getHasSuggestions())
			return getTotalSuggestions() > getSuggestions().size();
		else 
			return false;
	}
	public void setSuggestionsExceedThreshold(boolean suggestionsExceedThreshold) {
		//DoNothing
	}
	public int getMaxInitialThreadCount() {
		return MAX_INITIAL_THREAD_COUNT;
	}
	public void setMaxInitialThreadCount(int maxInitialThreadCount) {
		//Nope
	}
	public int getMaxInitialPostCount() {
		return MAX_INITIAL_POST_COUNT;
	}
	public void setMaxInitialPostCount(int maxInitialPostCount) {
		//Nope.
	}
	public int getMaxInitialTagSuggestionCount() {
		return MAX_INITIAL_TAG_SUGESTIONS;
	}
	public void setMaxInitialTagSuggestionCount(int maxInitialTagSuggestions) {
		//no
	}
	public boolean isTagBrowserSummary(){
		return MODE_TAG_BROWSER_SUMMARY.equals(getMode());
	}
	public boolean isFullThreadListing() {
		return MODE_FULL_THREAD.equals(getMode());
	}
	public boolean isSearchSummaryMode(){
		return MODE_SEARCH_SUMMARY.equals(getMode());
	}
	public void setFullThreadListing(boolean fullThreadListing) {
		//Nope
	}
	public boolean isFullPostListing() {
		return MODE_FULL_POST.equals(getMode());
	}
	public void setFullPostListing(boolean fullPostListing) {
		//nope
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<TagLite> getTags() {
		return tags;
	}
	public void setTags(List<TagLite> tags) {
		this.tags = tags;
	}
	public int getTotalTags() {
		return totalTags;
	}
	public void setTotalTags(int totalTags) {
		this.totalTags = totalTags;
	}
	public List<TagLite> getSuggestionsPeople() {
		return suggestionsPeople;
	}
	public void setSuggestionsPeople(List<TagLite> suggestionsPeople) {
		this.suggestionsPeople = suggestionsPeople;
	}
	public List<TagLite> getSuggestionsMonths() {
		return suggestionsMonths;
	}
	public void setSuggestionsMonths(List<TagLite> suggestionsMonths) {
		this.suggestionsMonths = suggestionsMonths;
	}
	public List<TagLite> getSuggestionsYears() {
		return suggestionsYears;
	}
	public void setSuggestionsYears(List<TagLite> suggestionsYears ){
		this.suggestionsYears = suggestionsYears;
	}
	public int getNwsCount() {
		return nwsCount;
	}
	public void setNwsCount(int nwsCount) {
		this.nwsCount = nwsCount;
	}
	public void incrementNwsCount(){
		this.nwsCount++;
	}
	public int getInfCount() {
		return infCount;
	}
	public void setInfCount(int infCount) {
		this.infCount = infCount;
	}
	public int getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}
	public int getLinkCount() {
		return linkCount;
	}
	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}
	public boolean getHasSpecialContent() {
		return 0 != (nwsCount + infCount + reviewCount + linkCount);
	}
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public int getMovieCount() {
		return movieCount;
	}
	public void setMovieCount(int movieCount) {
		this.movieCount = movieCount;
	}
	public TagLite getTagReview() {
		return tagReview;
	}
	public void setTagReview(TagLite tagReview) {
		this.tagReview = tagReview;
	}
	public TagLite getTagInf() {
		return tagInf;
	}
	public void setTagInf(TagLite tagInf) {
		this.tagInf = tagInf;
	}
	public TagLite getTagNws() {
		return tagNws;
	}
	public void setTagNws(TagLite tagNws) {
		this.tagNws = tagNws;
	}
	public TagLite getTagLink() {
		return tagLink;
	}
	public void setTagLink(TagLite tagLink) {
		this.tagLink = tagLink;
	}
	public TagLite getTagMovie() {
		return tagMovie;
	}
	public void setTagMovie(TagLite tagMovie) {
		this.tagMovie = tagMovie;
	}	
	/**
	 * I needed this functionality from more than one place so i moved it here.  This
	 * method takes a list of tag lite objects and loads the tags into the results bundle.
	 * 
	 * These 'other' tags are tags that are not Topic tags.  Topic tags are handled seperately
	 * because they have 'mass'? 
	 * 
	 * @param litetags
	 */
	public void loadOtherTags(List<TagLite> litetags){
		for(TagLite tl : litetags){
			if(Tag.TYPE_CREATOR.equals(tl.getType())){
				this.getSuggestionsPeople().add(tl);
			}
			else if(Tag.TYPE_DATE_MONTH.equals(tl.getType())){
				this.getSuggestionsMonths().add(tl);
			}
			else if(Tag.TYPE_DATE_YEAR.equals(tl.getType())){
				this.getSuggestionsYears().add(tl);
			}
			else if(Tag.TYPE_DATE_DAY.equals(tl.getType())){
				this.getSuggestionsDays().add(tl);
			}
			else if(Tag.TYPE_TOPIC.equals(tl.getType())){
				this.getTags().add(tl);
			}
			else if(Tag.TYPE_REVIEW.equals(tl.getType())){
				this.setReviewCount(tl.getCount());
				this.setTagReview(tl);
			}
			else if (Tag.TYPE_MOVIE.equals(tl.getType())){
				this.setMovieCount(tl.getCount());
				this.setTagMovie(tl);
			}
			else if(Tag.TYPE_DISPLAY.equals(tl.getType())){
				if(Tag.VALUE_INF.equals(tl.getValue())){
					this.setInfCount(tl.getCount());
					this.setTagInf(tl);
				}
				else if (Tag.VALUE_NWS.equals(tl.getValue())){
					this.setNwsCount(tl.getCount());
					this.setTagNws(tl);
				}
				else if (Tag.VALUE_LINK.equals(tl.getValue())){
					this.setLinkCount(tl.getCount());
					this.setTagLink(tl);
				}
				else if(Tag.VALUE_PRIVATE.equals(tl.getValue())){
					this.setPrivateCount(tl.getCount());
					this.setTagPrivate(tl);
				}
				else{
					//Hm.
				}
			}
			else{
				//weird
			}
		}
		
		TagLite.calculatePercentile(getSuggestionsPeople());
		TagLite.calculatePercentile(getSuggestionsMonths());
		TagLite.calculatePercentile(getSuggestionsYears());
		TagLite.calculatePercentile(getTags());
		
		Collections.sort(getSuggestionsMonths(),new TagLite.MonthComparator());
		
	}
	public String getThreadTitle() {
		return threadTitle;
	}
	public void setThreadTitle(String threadTitle) {
		this.threadTitle = threadTitle;
	}
	public String getSearchPhrase() {
		return searchPhrase;
	}
	public void setSearchPhrase(String searchPhrase) {
		this.searchPhrase = searchPhrase;
	}
	public List<TagLite> getSuggestionsDays() {
		return suggestionsDays;
	}
	public void setSuggestionsDays(List<TagLite> suggestionsDays) {
		this.suggestionsDays = suggestionsDays;
	}
	public int getPrivateCount() {
		return privateCount;
	}
	public void setPrivateCount(int privateCount) {
		this.privateCount = privateCount;
	}
	public TagLite getTagPrivate() {
		return tagPrivate;
	}
	public void setTagPrivate(TagLite tagPrivate) {
		this.tagPrivate = tagPrivate;
	}
	public boolean isShowCalender() {
		return showCalender;
	}
	public void setShowCalender(boolean showCalender) {
		this.showCalender = showCalender;
	}
	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}

	
	
	
}
