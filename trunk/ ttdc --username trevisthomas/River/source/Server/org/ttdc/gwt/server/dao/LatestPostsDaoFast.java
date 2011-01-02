package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.ttdc.gwt.client.beans.GEntry;
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.server.beanconverters.ConversionUtils;
import org.ttdc.gwt.server.util.PostFormatter;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.objects.FullPost;

import com.google.gwt.dev.util.collect.HashMap;


public class LatestPostsDaoFast extends FilteredPostPaginatedDaoBase{
	private int MAX_CONVERSATIONS = 10;
	private int MAX_FLAT = 4 * MAX_CONVERSATIONS;
	private InboxDao inboxDao = null;
	
	
	public InboxDao getInboxDao() {
		return inboxDao;
	}


	public void setInboxDao(InboxDao inboxDao) {
		this.inboxDao = inboxDao;
	}


	public PaginatedList<GPost> loadFlat(){
		setPageSize(MAX_FLAT);
		PaginatedList<GPost> results = new PaginatedList<GPost>();
		results = executeLoadQuery("LatestPostsDaoFast.Flat", false);
		return results;
	}
	
	public PaginatedList<GPost> loadGrouped(){
		setPageSize(MAX_CONVERSATIONS);
		PaginatedList<GPost> results = new PaginatedList<GPost>();
		results = executeLoadQuery("LatestPostsDaoFast.Grouped", true);
		return results;
	}
	
	@SuppressWarnings("unchecked")
	private PaginatedList<GPost> executeLoadQuery(String query, boolean grouped) {
		PaginatedList<GPost> results;
		List<String> ids;
		long count;
		long filterMask = buildFilterMask(getFilterFlags());
		if(getPageSize() > 0){
			count = (Long)session().getNamedQuery(query+"Count")
				.setParameter("filterMask", filterMask)
				.setParameterList("threadIds", getFilterThreadIds())
				.uniqueResult();
			
			
			ids = session().getNamedQuery(query)
				.setParameter("filterMask", filterMask)
				.setParameterList("threadIds", getFilterThreadIds())
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
		}
		else{
			ids = session().getNamedQuery(query)
				.setParameter("filterMask", filterMask)
				.setParameterList("threadIds", getFilterThreadIds())
				.list();
			
			count = ids.size();
		}
		
		List<GPost> list;
		if(grouped){
			list = fetchPostsForIdsGrouped(ids, filterMask);
		}
		else{
			list = fetchPostsForIdsFlat(ids);
		}
		
		results = DaoUtils.createResults(this, list, count);
		
		return results;
	}

	@SuppressWarnings("unchecked")
	private List<GPost> fetchPostsForIdsGrouped(List<String> ids, long filterMask) {
		List<FullPost> fullPosts;
		fullPosts = session().getNamedQuery("FullPost.groupedPosts")
			.setParameterList("postIds", ids)
			.setParameter("filterMask",filterMask)
			.list();
		
		List<GPost> gPosts = digestFullPostsToGrouped(fullPosts);
		Collections.sort(gPosts, new GPostByPostIdReferenceComparator(ids));
		return gPosts;
	}

	private List<GPost> digestFullPostsToGrouped(List<FullPost> fullPosts) {
		Map<String, GPost> threads = new HashMap<String, GPost>();
		
		for(FullPost fp : fullPosts){
			GPost p = crackThatPost(fp);
			
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
	private List<GPost> fetchPostsForIdsFlat(List<String> ids) {
		List<FullPost> fullPosts;
		fullPosts = session().getNamedQuery("FullPost.flatPosts")
			.setParameterList("postIds", ids)
			.list();
		
		
		List<GPost> gPosts = digestFullPostsToFlat(fullPosts);
		Collections.sort(gPosts, new GPostByPostIdReferenceComparator(ids));
		return gPosts;
	}


	private List<GPost> digestFullPostsToFlat(List<FullPost> fullPosts) {
		List<GPost> list = new ArrayList<GPost>();
		for(FullPost fp : fullPosts){
			list.add(crackThatPost(fp));
		}
		return list;
	}
	
	

	private GPost crackThatPost(FullPost fp) {
		GPost gp = new GPost();
		gp.setPostId(fp.getPostId());
		gp.setImage(crackThatImage(fp));
		gp.setLatestEntry(crackThatEntry(fp));
		gp.setDate(fp.getDate());
		gp.setEditDate(fp.getEditDate());
		gp.setRootPost(fp.getRootId() == null);
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
			tag.setValue(fp.getTitleValue() + " ("+fp.getRootPublishYear()+")");
		}
		else{
			tag.setValue(fp.getTitleValue());
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
