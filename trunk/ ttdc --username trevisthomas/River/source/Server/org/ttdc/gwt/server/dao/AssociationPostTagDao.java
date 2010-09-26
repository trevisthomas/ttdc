package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

final public class AssociationPostTagDao {
	private Post post;
	private Tag tag;
	private Person creator;
	
	public static AssociationPostTag load(String associationId){
		AssociationPostTag ass;
		ass = (AssociationPostTag)session().load(AssociationPostTag.class, associationId);
		return ass;
	}
	
	/*
	 * Disabled L2 cache to get this working for movie ratings. Worked fine in unit
	 * tests but not from ap.  disabling cache made it work.  Tried session.refresh but that 
	 * didnt work.  4/27/2010
	 * 
	 * 4/29/2010 re-enabled the damned cache.  I removed the cache stratagy from the post class
	 * and now it seems to work
	 */
	public AssociationPostTag create(){
		AssociationPostTag ass = new AssociationPostTag();
		
		validateForCreation();
		
		ass.setCreator(creator);
		ass.setTag(tag);
		ass.setPost(post);

//		increaseMass(tag);
		session().save(ass);
		session().flush();
		session().refresh(post);
		return ass;
	}
	
	public static void reTag(String associationId, Tag tag){
		if(tag == null) 
			throw new RuntimeException("New Tag is invalid, null actually.");
		
		AssociationPostTag ass = load(associationId);
		Tag oldTag = ass.getTag();
//		decreaseMass(oldTag);
//		increaseMass(tag);
		ass.setTag(tag);
		session().save(ass);
	}

//	private static void increaseMass(Tag tag) {
//		tag.setMass(tag.getMass()+1);
//		session().save(tag);
//	}
//
//	private static void decreaseMass(Tag tag) {
//		tag.setMass(tag.getMass()-1);
//		session().save(tag);
//	}
	
	/*
	 * Disabled L2 cache to get this working for movie ratings. Worked fine in unit
	 * tests but not from ap.  disabling cache made it work.  Tried session.refresh but that 
	 * didnt work.  4/27/2010
	 * 
	 * 4/29/2010 re-enabled the damned cache.  I removed the cache stratagy from the post class
	 * and now it seems to work
	 */
	/**
	 * REMEMBER you must call this with a fresh session and commit when you're done 
	 * to see the changes.  If the remove method is called while the object you're removing
	 * is still attached to another object in session you'll get an exception.
	 * 
	 * Check out the unit test to see how to use it.
	 * 
	 * WARNING: FEB 20, 2010  I'm fighting a war with hibernate because ehcache isnt refreshing 
	 * so to make this remove work i had to disable it.
	 * 
	 * ... later that night... i if i remove the ass from the post manually before deleting it my
	 * unit test passes so i re-enabled the L2 cache.
	 * 
	 * @return
	 */
	public static AssociationPostTag remove(String associationId){
		if(StringUtils.isEmpty(associationId))
			throw new RuntimeException("Association ID is missing, nothing to remove.");
		
		AssociationPostTag ass = load(associationId);
		ass.getPost().getTagAssociations().remove(ass);
		
//		decreaseMass(ass.getTag());
		session().delete(ass);
		session().flush();
		return ass;
	}

	private void validateForCreation() {
		if(creator == null)
			throw new RuntimeException("Creator is required");
		if(post == null)
			throw new RuntimeException("Post is required for for association");
		if(tag == null)
			throw new RuntimeException("Tag is required for for association");
		
		if(creator.isAnonymous()){
			throw new RuntimeException("Anonymous users can't perform this action.");
		}
	}
	
	public boolean hasTitleAssociation(Post post){
		return (readTitleAssociationForPost(post) != null);
	}
	
	@SuppressWarnings("unchecked")
	public AssociationPostTag readTitleAssociationForPost(Post post){
		List<AssociationPostTag> list = session().createCriteria(AssociationPostTag.class)
			.add(Restrictions.eq("post.postId", post.getPostId()))	
			.add(Restrictions.eq("title", true))
			.list();
		
		if(list.size() == 0){
			return null;
		}
		else{
			return list.get(0);
		}
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public Person getCreator() {
		return creator;
	}

	public void setCreator(Person creator) {
		this.creator = creator;
	}
}
