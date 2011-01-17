package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GEntry;
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.server.beanconverters.ConversionUtils;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.util.PostFormatter;
import org.ttdc.gwt.shared.calender.CalendarPost;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Hour;
import org.ttdc.gwt.shared.util.PostFlag;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.objects.FullPost;
import org.ttdc.persistence.objects.FullTag;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Post;



/**
 * Given a list of post id's this class uses raw sql queries to serializable classes
 * with the fewest steps.  This was implemented to move away from hibernate and get the
 * site loading faster.
 * 
 * 
 * @author Trevis
 *
 */
public class FastGPostLoader {
	private final InboxDao inboxDao;
	private final Date cutoffTime;
	 
	public FastGPostLoader(final InboxDao inboxDao) {
		this.inboxDao = inboxDao;
		
		cutoffTime = new Date();
		cutoffTime.setTime(cutoffTime.getTime() - InitConstants.POST_EDIT_WINDOW_MS);
	}
	
	@SuppressWarnings("unchecked")
	public List<GPost> fetchPostsForIdsGrouped(List<String> ids, long filterMask) {
		List<FullPost> fullPosts;
		fullPosts = session().getNamedQuery("FullPost.groupedPosts")
			.setParameterList("postIds", ids)
			.setParameter("filterMask",filterMask)
			.list();
		
		List<GPost> gPosts = digestFullPostsToGrouped(fullPosts);
		Collections.sort(gPosts, new GPostByPostIdReferenceComparator(ids));
		return gPosts;
	}
	
	@SuppressWarnings("unchecked")
	public List<GPost> fetchPostsForIdsFlat(List<String> ids) {
		List<FullPost> fullPosts;
		fullPosts = session().getNamedQuery("FullPost.flatPosts")
			.setParameterList("postIds", ids)
			.list();
		
		
		List<GPost> gPosts = digestFullPostsToFlat(fullPosts, true);
		Collections.sort(gPosts, new GPostByPostIdReferenceComparator(ids));
		return gPosts;
	}
	
	@SuppressWarnings("unchecked")
	public List<GPost> fetchPostsForIdsMovieSummary(List<String> ids) {
		if(ids.size() == 0)
			return new ArrayList<GPost>();
		//TODO perform custom handling
		//return fetchPostsForIdsFlat(ids);
		
		List<FullPost> fullPosts;
		fullPosts = session().getNamedQuery("FullPost.flatPosts")
			.setParameterList("postIds", ids)
			.list();
		
		List<GPost> gPosts = digestFullPostsToFlat(fullPosts, false);
		Collections.sort(gPosts, new GPostByPostIdReferenceComparator(ids));
		return gPosts;
	}
	
	public void inflateDay(Day day){
		List<String> postIds = new ArrayList<String>();
		
		if(day.getHours() == null)
			return;
		
		for(Hour hour : day.getHours()){
			for(CalendarPost cp : hour.getCalendarPosts()){
				postIds.add(cp.getPostId());
			}
		}
		
		if(postIds.size() == 0)
			return;
		
		
		List<FullPost> fullPosts;
		fullPosts = session().getNamedQuery("FullPost.flatPosts")
			.setParameterList("postIds", postIds)
			.list();
		
		
		Map<String,GPost> gPosts = digestFullPostsToMap(fullPosts);
		
		for(Hour hour : day.getHours()){
			for(CalendarPost cp : hour.getCalendarPosts()){
				GPost gPost = gPosts.get(cp.getPostId());
				hour.addPost(gPost);
			}
		}
	}	
	

	public List<GPost> fetchPostsForPosts(List<Post> posts){
		if (posts.size() == 0) {
			return new ArrayList<GPost>();
		}
		List<String> ids = extractIds(posts);
		return fetchPostsForIdsFlat(ids);
	}	

	private static List<String> extractIds(List<Post> posts){
		List<String> postIds = new ArrayList<String>();
		for(Post pl : posts){
			postIds.add(pl.getPostId());
		}
		return postIds;
	}
	
	private List<GPost> digestFullPostsToGrouped(List<FullPost> fullPosts) {
		Map<String, GPost> threads = new HashMap<String, GPost>();
		
		Map<String, List<GAssociationPostTag>> assMap = buildAssociationPostTagMap(fullPosts);
		
		for(FullPost fp : fullPosts){
			GPost p = crackThatPost(fp, true);
			
			p.setTagAssociations(assMap.get(p.getPostId()));
			if(p.isReview()){
				p.getRoot().setTagAssociations(assMap.get(p.getRoot().getPostId()));
			}
			
			if(p.isThreadPost()){
				threads.put(p.getPostId(),p);
			}
			else{
				p.setSuggestSummary(true);
				threads.get(p.getThread().getPostId()).getPosts().add(p);
			}
		}
		List<GPost> list = new ArrayList<GPost>(threads.values());
		return list;
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<GAssociationPostTag>> buildAssociationPostTagMap(
			List<FullPost> fullPosts) {
		Map<String, List<GAssociationPostTag>> assMap = new HashMap<String, List<GAssociationPostTag>>();
		
		List<String> postIds = new ArrayList<String>();
		for(FullPost fp : fullPosts){
			postIds.add(fp.getPostId());
			if((fp.getMetaMask() & PostFlag.REVIEW.getBitmask()) == PostFlag.REVIEW.getBitmask()){
				//We want the tags for the root's of reviews.
				postIds.add(fp.getRootId());
			}
		}
		
		List<FullTag> fullTags;
		fullTags = session().getNamedQuery("FullTag.loadList")
			.setParameterList("postIds", postIds)
			.list();
		
		for(FullTag ft : fullTags){
			GAssociationPostTag ass = crackThatAssUp(ft);
			List<GAssociationPostTag> assList = assMap.get(ft.getPostId());
			if(assList == null){
				assList = new ArrayList<GAssociationPostTag>();
				assMap.put(ft.getPostId(), assList);
			}
			assList.add(ass);
		}
		
		return assMap;
	}

	private GAssociationPostTag crackThatAssUp(FullTag ft) {
		GAssociationPostTag ass = new GAssociationPostTag();
		GTag tag = new GTag();
		tag.setType(ft.getType());
		tag.setValue(ft.getValue());
		tag.setTagId(ft.getTagId());
		ass.setTag(tag);
		
		GPerson creator = new GPerson();
		creator.setPersonId(ft.getCreatorId());
		creator.setLogin(ft.getCreatorLogin());
		ass.setCreator(creator);
		ass.setGuid(ft.getAssId());
		
		return ass;
	}


	private Map<String, GPost> digestFullPostsToMap(List<FullPost> fullPosts) {
		Map<String, GPost> map = new HashMap<String, GPost>();
		Map<String, List<GAssociationPostTag>> assMap = buildAssociationPostTagMap(fullPosts);
		for(FullPost fp : fullPosts){
			GPost p = crackThatPost(fp, true);
			p.setTagAssociations(assMap.get(p.getPostId()));
			if(p.isReview()){
				p.getRoot().setTagAssociations(assMap.get(p.getRoot().getPostId()));
			}
			map.put(p.getPostId(),p);
		}
		return map;
	}
	

	private List<GPost> digestFullPostsToFlat(List<FullPost> fullPosts, boolean fullyInflateMovies) {
		List<GPost> list = new ArrayList<GPost>();
		Map<String, List<GAssociationPostTag>> assMap = buildAssociationPostTagMap(fullPosts);
		for(FullPost fp : fullPosts){
			GPost p = crackThatPost(fp, fullyInflateMovies);
			p.setTagAssociations(assMap.get(p.getPostId()));
			if(p.isReview()){
				p.getRoot().setTagAssociations(assMap.get(p.getRoot().getPostId()));
			}
			list.add(p);
		}
		return list;
	}
	
	

	private GPost crackThatPost(FullPost fp, boolean fullyInflateMovies) {
		GPost gp = new GPost();
		gp.setPostId(fp.getPostId());
		gp.setLatestEntry(crackThatEntry(fp));
		gp.setDate(fp.getDate());
		gp.setEditDate(fp.getEditDate());
		gp.setRootPost(fp.getRootId().equals( fp.getPostId()));
		gp.setRoot(crackThatRootPost(fp));
		gp.setThread(crackThatThreadPost(fp));
		gp.setThreadPost(fp.getPostId().equals(fp.getThreadId()));
		gp.setReplyCount(fp.getReplyCount());
		gp.setMass(fp.getMass());
		gp.setCreator(crackThatPerson(fp));
		gp.setUrl(fp.getUrl());
		gp.setPublishYear(fp.getPublishYear());
		gp.setParentPostId(fp.getParentId());
		gp.setParentPostCreator(fp.getParentPostCreator());
		gp.setParentPostCreatorId(fp.getParentPostCreatorId());
		gp.setTitleTag(crackThatTitleTag(fp));
		gp.setAvgRatingTag(crackThatRatingTag(fp));
		gp.setPath(fp.getPath());
		gp.setMetaMask(fp.getMetaMask());
		gp.setRateCount(fp.getRateCount());
		
		if(gp.getDate().after(cutoffTime)){
			gp.setInEditWindow(true);
		}
		
		if(gp.isMovie() && fullyInflateMovies){ //This hack falls back to the old school way of doing things when you need all detail about a movie
			gp = FastPostBeanConverter.convertPost(PostDao.loadPost(gp.getPostId()), inboxDao);
			return gp;
		}
		
		if(gp.isReview()){
			GImage image = new GImage();
			if(StringUtil.notEmpty(fp.getRootImageName())){
				image.setName(fp.getRootImageName());
				image.setThumbnailName(FullPost.translateImageNameToThumbnailName(fp.getRootImageName()));
				image.setWidth(fp.getRootImageWidth());
				image.setHeight(fp.getRootImageHeight());
				gp.setImage(image);
			}
			else{
				Image defaultPoster = InitConstants.DEFAULT_POSTER;
				image.setName(defaultPoster.getName());
				image.setThumbnailName(defaultPoster.getSquareThumbnailName());
				image.setWidth(defaultPoster.getWidth());
				image.setHeight(defaultPoster.getHeight());
				gp.setImage(image);
			}
		}
		else{
			gp.setImage(crackThatImage(fp));
		}
		if(inboxDao != null){
			gp.setRead(inboxDao.isRead(fp.getDate()));
		}
		return gp;
	}
	
	private GPost crackThatThreadPost(FullPost fp) {
		GPost gp = new GPost();
		gp.setPostId(fp.getThreadId());
		return gp;
	}
	
	private GPost crackThatRootPost(FullPost fp) {
		GPost gp = new GPost();
		gp.setPostId(fp.getRootId());
		return gp;
	}


	private GTag crackThatRatingTag(FullPost fp) {
		if(StringUtil.empty(fp.getTitleValue()))
			return null;
		GTag tag = new GTag();
		tag.setValue(fp.getRatingValue());
		return tag;
	}


	private GTag crackThatTitleTag(FullPost fp) {
		GTag tag = new GTag();
		if(StringUtil.notEmpty(fp.getRootPublishYear())){
			tag.setValue(PostFormatter.getInstance().format(fp.getTitleValue()) + " ("+fp.getRootPublishYear()+")");
		}
		else{
			tag.setValue(PostFormatter.getInstance().format(fp.getTitleValue()));
		}
		return tag;
	}

	private GPerson crackThatPerson(FullPost fp) {
		GPerson person = new GPerson();
		GImage image = new GImage();
		
		person.setLogin(fp.getCreatorLogin());
		person.setPersonId(fp.getCreatorId());
		person.setImage(image);
		image.setHeight(50);
		image.setWidth(50);
		if(!StringUtil.empty(fp.getCreatorImageName())){
			image.setName(fp.getCreatorImageName());
			image.setThumbnailName(fp.getCreatorImageThumbnailName());
		}
		else{
			image.setName(InitConstants.DEFAULT_AVATAR.getName());
			image.setThumbnailName(FullPost.translateImageNameToThumbnailName(InitConstants.DEFAULT_AVATAR.getName()));
		}
		return person;
	}
	
	private GEntry crackThatEntry(FullPost fp){
		GEntry entry = new GEntry();
		entry.setBody(PostFormatter.getInstance().format(fp.getEntryBody()));
		entry.setSummary(ConversionUtils.preparePostSummaryForDisplay(fp.getEntrySummary()));
		return entry;
	}

	private GImage crackThatImage(FullPost fp) {
		if(fp.getImageName() == null)
			return null;
		
		GImage image = new GImage();
		image.setName(fp.getImageName());
		image.setHeight(fp.getImageHeight());
		image.setWidth(fp.getImageWidth());
		image.setThumbnailName(fp.getImageThumbnailName());
		return image;
	}

	
}
