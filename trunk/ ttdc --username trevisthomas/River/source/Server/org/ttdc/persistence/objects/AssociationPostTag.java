package org.ttdc.persistence.objects;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;


/**
 * @author Trevis
 *
 */

@Entity
@Table(name="ASSOCIATION_POST_TAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NamedQueries({
	@NamedQuery(name="ass.getByPostId", query="SELECT ass FROM AssociationPostTag ass WHERE ass.post.postId=:postId"),
	@NamedQuery(name="ass.getByThreadId", query="SELECT ass FROM AssociationPostTag ass WHERE ass.post.root.postId=:threadId"),
	@NamedQuery(name="ass.getByAssByTagAndPost", query="SELECT ass FROM AssociationPostTag ass WHERE ass.post.postId=:postId AND ass.tag.tagId=:tagId"),
	@NamedQuery(name="ass.getByPostIds", query="SELECT ass FROM AssociationPostTag ass INNER JOIN FETCH ass.creator INNER JOIN FETCH ass.tag WHERE ass.post.postId IN (:postIds) ORDER BY ass.date"),
	@NamedQuery(name="ass.getAll", query="SELECT ass FROM AssociationPostTag ass"),
	@NamedQuery(name="ass.deleteById", query="DELETE FROM AssociationPostTag ass WHERE ass.guid=:guid"),
	@NamedQuery(name="ass.deleteByBranch", query="DELETE FROM AssociationPostTag ass WHERE ass.post.postId IN (select post.postId from Post post where post.root.postId = :rootId) AND ass.tag.tagId=:tagId"),
	@NamedQuery(name="ass.deleteByIds", query="DELETE FROM AssociationPostTag ass WHERE ass.guid in (:guids)"),
	@NamedQuery(name="ass.getLatest", query="SELECT ass FROM AssociationPostTag ass ORDER BY ass.date DESC"),
	@NamedQuery(name="ass.getLatestOfType", query="SELECT ass FROM AssociationPostTag ass WHERE ass.tag.type=:type ORDER BY ass.date DESC")
	
})
public class AssociationPostTag implements Comparable<AssociationPostTag>, HasGuid{
	private String guid;
	private Tag tag;
	private Post post;
	private Person creator;
	private boolean title;
	private Date date = new Date();
	public static int iCount = 0; 
		
	public AssociationPostTag(){
		iCount++;
	}
	@Override
	public String toString() {
		return "Tagged: "+tag+" on "+date+" by "+creator;
	}
	
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinColumn(name="TAG_GUID")
	public Tag getTag() {
		return tag;
	}
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinColumn(name="CREATOR_GUID")
	public Person getCreator() {
		return creator;
	}
	public void setCreator(Person creator) {
		this.creator = creator;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinColumn(name="POST_GUID")
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		//post.addTagAssociation(this);
		this.post = post;
	}
	public int compareTo(AssociationPostTag that) {
		//return this.date.compareTo(that.date); 
		//This impl is a hack until the db is fixed
		//return this.tag.getDate().compareTo(that.tag.getDate());
		//return that.tag.getDate().compareTo(this.tag.getDate());
		throw new RuntimeException("Trevis, you didnt know why this comparison imple worked the way it did so you added this exception to see if it is ever called.");
	}
	
	public void initialize(){
		Hibernate.initialize(tag);
		Hibernate.initialize(post);
	}
	public boolean isTitle() {
		return title;
	}
	public void setTitle(boolean title) {
		this.title = title;
	}
	
	@Transient
	public String getUniqueId() {
		return getGuid();
	}
	
}
