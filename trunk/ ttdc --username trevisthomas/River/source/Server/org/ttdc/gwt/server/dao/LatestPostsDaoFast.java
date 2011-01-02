package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.beans.GEntry;
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.objects.FullPost;


public class LatestPostsDaoFast extends FilteredPostPaginatedDaoBase{
	private int MAX_CONVERSATIONS = 10;
	private int MAX_FLAT = 2 * MAX_CONVERSATIONS;
	
	
	public PaginatedList<GPost> loadFlat(final InboxDao inboxDao){
		setPageSize(MAX_FLAT);
		PaginatedList<GPost> results = new PaginatedList<GPost>();
		
		results = executeLoadQuery("LatestPostsDaoFast.Flat");
		
		return results;
	}
	
	
	@SuppressWarnings("unchecked")
	private PaginatedList<GPost> executeLoadQuery(String query) {
		PaginatedList<GPost> results;
		if(getPageSize() > 0){
			List<GPost> list;
			List<String> ids;
			long count = (Long)session().getNamedQuery(query+"Count")
				.setParameter("filterMask", buildFilterMask(getFilterFlags()))
				.setParameterList("threadIds", getFilterThreadIds())
				.uniqueResult();
			
			
			ids = session().getNamedQuery(query)
				.setParameter("filterMask", buildFilterMask(getFilterFlags()))
				.setParameterList("threadIds", getFilterThreadIds())
				.setFirstResult(calculatePageStartIndex())
				.setMaxResults(getPageSize()).list();
			
			list = fetchPostsForIdsFlat(ids);
			
			results = DaoUtils.createResults(this, list, count);
		}
		else{
//			List<GPost> list;
//			List<String> ids;
//			ids = session().getNamedQuery(query)
//				.setParameter("filterMask", buildFilterMask(getFilterFlags()))
//				.setParameterList("threadIds", getFilterThreadIds())
//				.list();
//			results = DaoUtils.createResults(this, list, list.size());
			
			results = null;
		}
		
		return results;
	}


	@SuppressWarnings("unchecked")
	private List<GPost> fetchPostsForIdsFlat(List<String> ids) {
		List<FullPost> fullPosts;
		fullPosts = session().getNamedQuery("FullPost.flatPosts")
			.setParameterList("postIds", ids)
			.list();
		
		
		List<GPost> gPosts = digestFullPostsToFlat(fullPosts);
		
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
			tag.setValue(fp.getTitleValue() + "("+fp.getRootPublishYear()+")");
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
		image.setName(fp.getCreatorImageName());
		image.setThumbnailName(fp.getCreatorImageThumbnailName());
		return person;
	}
	
	private GEntry crackThatEntry(FullPost fp){
		GEntry entry = new GEntry();
		entry.setBody(fp.getEntryBody());
		entry.setSummary(fp.getEntrySummary());
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
