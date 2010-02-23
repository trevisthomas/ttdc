package org.ttdc.biz.network.services.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;

/**
 * This decorator adds post tags to the posts. It is used for pagination.
 * @author Trevis
 *
 */
public class PostDecorator implements Paginator.Decorator<Post>{
	private final Person person;
	private final boolean initMovieReviews;
	
	public PostDecorator(Person person) {
		this.person = person;
		initMovieReviews = true;
	}
	public PostDecorator(Person person, boolean initMovieReviews) {
		this.person = person;
		this.initMovieReviews = initMovieReviews;
	}
	public List<Post> prepare(List<Post> sublist) {
		List<Post> results = new ArrayList<Post>();
		List<String> postIds = new ArrayList<String>();
		for(Post p : sublist){
			postIds.add(p.getPostId());
		}
		results = PostHelper.loadPosts(postIds);
		PostHelper.initializePostsFlat(person, results, initMovieReviews);
		//initEntries(postIds);
		
		/* 
		 * I set the counters in the post shells. So i'm getting them back out
		 */
		for(Post p : results ){
			Post opp = sublist.get(sublist.indexOf(p));
			p.setPostCounter(opp.getPostCounter());
		}
		
		Collections.sort(results,new Post.ByReferenceComparator(sublist));
		
		return results;
		
	}
}
