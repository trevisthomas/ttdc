package org.ttdc.biz.network.services.helpers;

import java.util.List;

import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

/**
 * This class was created when i was making the movie widget.  I need to be able to as
 * if the post is rated or reviewed by a specific user.  I have that info in the action but 
 * the jsp can't pass it in, so i made this hack of a wrapper to take care of that problem for me.
 * 
 * @author Trevis
 *
 */
public class PersonsPost {
	private Post post;
	private Person person;
	public PersonsPost(Post post, Person person){
		this.post = post;
		this.person = person;
	}
	
	public boolean getHasRating(){
//		if(post.hasTagAssociation(Tag.TYPE_RATING,person))
//			return true;
		return false;
	}
	
//	public String getRating(){
//		List<AssociationPostTag> ratings = post.loadTagAssociations(Tag.TYPE_RATING,person);
//		if(ratings.size() > 0)
//			return ratings.get(0).getTag().getValueRating();
//		else
//			return 	"";
//	}
	
	
	
//	public boolean getHasReview(){
//		List<Post> reviews = post.getReviews();
//		for(Post p : reviews){
//			if(p.hasTagAssociation(Tag.TYPE_REVIEW, person)){
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public Post getReview(){
//		List<Post> reviews = post.getReviews();
//		for(Post p : reviews){
//			if(p.getCreator().equals(person))
//				return p;
//		}
//		return null;
//	}
	
	
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	
	
	
}
