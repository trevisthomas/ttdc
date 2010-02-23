package org.ttdc.persistence.objects;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="INBOX_CACHE")
@NamedQueries({
	@NamedQuery(name="inboxCache.getAll", query="SELECT cache FROM InboxCache cache"),
	@NamedQuery(name="inboxCache.getForPerson", query="SELECT cache FROM InboxCache cache WHERE cache.person.personId = :personId"),
	@NamedQuery(name="inboxCache.deletePost", query="DELETE FROM InboxCache cache WHERE cache.post.postId=:postId"),
	@NamedQuery(name="inboxCache.deletePostForPerson", query="DELETE FROM InboxCache cache WHERE cache.post.postId=:postId AND cache.person.personId=:personId"),
	@NamedQuery(name="inboxCache.deletePerson", query="DELETE FROM InboxCache cache WHERE cache.person.personId=:personId"),
	@NamedQuery(name="inboxCache.deleteThreadForPerson", query="DELETE FROM InboxCache cache WHERE cache.post.postId in (SELECT post.postId FROM Post post WHERE post.root.postId = :rootId ) AND cache.person.personId=:personId")
})
public class InboxCache {
	private Person person;
	private Post post;
	private String guid;
	private Date date;
	
	@Id @GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	@Column(name="GUID")
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	@JoinColumn(name="PERSON_GUID")
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	@JoinColumn(name="POST_GUID")
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
