package org.ttdc.servlets.wrappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;

public class PostWrapper {
	/*
	 * let postId : String let date : Date let title : String let creator: Person? // let latestEntry: Entry let entry:
	 * String let replyCount : UInt let posts : [Post]? let mass : UInt let threadId: String? let threadPost: Bool //For
	 * conversations this is true. let parentPostCreator: String! let parentPostCreatorId: String! let
	 * tagAssociations:[TagAssociation]? let isMovie : Bool let isReview : Bool let image : Image? let reviewRating:
	 * Float? let isRootPost : Bool
	 */

	// final private String postId;
	// final private Date date;
	// final private String title;
	// //creator
	// final private String entry;
	// final private int replyCount;
	// final private List<PostWrapper> posts;
	// final private int mass;
	// final private String threadId;
	// final private boolean threadPost;
	// final private String parentPostCreator;
	// final private String parentPostCreatorId;
	// // let tagAssociations:[TagAssociation]?
	// final private boolean isMovie;
	// final private boolean isReview;
	// // let image : Image?
	// final private float reviewRating;
	// final private boolean isRootPost;

	private final GPost delegate;

	PostWrapper(GPost post) {
		delegate = post;
	}

	public String getPostId() {
		return delegate.getPostId();
	}

	public PersonWrapper getCreator() {
		return new PersonWrapper(delegate.getCreator());
	}

	public Date getDate() {
		return delegate.getDate();
	}

	public String getTitle() {
		return delegate.getTitle();
	}

	public String getEntry() {
		return delegate.getEntry();
	}

	public int getReplyCount() {
		return delegate.getReplyCount();
	}

	public List<PostWrapper> getPosts() {
		List<PostWrapper> list = new ArrayList<PostWrapper>(delegate.getPosts().size());
		for (GPost p : delegate.getPosts()) {
			list.add(new PostWrapper(p));
		}
		return list;
	}

	public int getMass() {
		return delegate.getMass();
	}

	// Warning! This is going to require a client change
	public String getThreadId() {
		if (delegate.getThread() != null) {
			return delegate.getThread().getPostId();
		} else {
			return null;
		}
	}

	public boolean isThreadPost() {
		return delegate.isThreadPost();
	}

	public String getParentPostCreator() {
		return delegate.getParentPostCreator();
	}

	public String getParentPostCreatorId() {
		return delegate.getParentPostCreatorId();
	}

	public boolean isMovie() {
		return delegate.isMovie();
	}

	public boolean isReview() {
		return delegate.isReview();
	}

	public Double getReviewRating() {
		return delegate.getReviewRating();
	}

	public boolean isRootPost() {
		return delegate.isRootPost();
	}

	public ImageWrapper getImage() {
		if (delegate.getImage() != null) {
			return new ImageWrapper(delegate.getImage());
		} else {
			return null;
		}
	}

	public List<TagAssociationWrapper> getTagAssociations() {
		List<TagAssociationWrapper> list = new ArrayList<TagAssociationWrapper>(delegate.getTagAssociations().size());
		for (GAssociationPostTag ass : delegate.getTagAssociations()) {
			list.add(new TagAssociationWrapper(ass));
		}
		return list;
	}

}
