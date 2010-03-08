package org.ttdc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ttdc.persistence.objects.Post;

@Deprecated
public class MedianCommentAgeUtil {
	
	
	public static void assignPostRelativeAges(Post post, List<Post> latest){
		List<Post> posts = new ArrayList<Post>();
		posts.add(post);
		setDefaultRelativeAge(posts);
		assignPostRelativeAges(latest);
		//throw new ServiceException("Trevis, this call is probably broken because of the caching change");
	}
	
	private static void setDefaultRelativeAge(List<Post> list){
		throw new RuntimeException("Trevis thought that this method was no longer in use!!");
//		for(Post p : list){
//			p.setRelativeAge(Post.RELATIVE_AGE_DEFAULT);
//			setDefaultRelativeAge(p.getPosts());
//		}
	}
	/**
	 * Uses the median calculator to distribute posts into lists of relative age and then assign a string to denote it.
	 * This is used to show a gradient using css to present the relative age of new posts.
	 * 
	 * @param posts
	 * @param latest - a list of the latest posts so i know which ones to age.
	 */
	public static void assignPostRelativeAges(List<Post> latest){
		//List<Post> inSessionPosts = new ArrayList<Post>();
		//extractLatestPosts(inSessionPosts, posts, latest);
		MedianCommentAgeUtil util = new MedianCommentAgeUtil();
		Median<Post> calculator =  new Median<Post>();
		List<List<Post>> lists = calculator.medianDistribution(3, latest, new PostSourceValueReader());
		Map<String,Post> postLookupMap = new HashMap<String,Post>();
		util.buildLookupMap(latest,postLookupMap);
		
		List<Post> list = lists.get(0);
		list = lists.get(0);
		util.assignRelativeAge(postLookupMap, list,Post.RELATIVE_AGE_7);
		list = lists.get(1);
		util.assignRelativeAge(postLookupMap,list,Post.RELATIVE_AGE_6);
		list = lists.get(2);
		util.assignRelativeAge(postLookupMap,list,Post.RELATIVE_AGE_5);
		list = lists.get(3);
		util.assignRelativeAge(postLookupMap,list,Post.RELATIVE_AGE_4);
		list = lists.get(4);
		util.assignRelativeAge(postLookupMap,list,Post.RELATIVE_AGE_3);
		list = lists.get(5);
		util.assignRelativeAge(postLookupMap,list,Post.RELATIVE_AGE_2);
		list = lists.get(6);
		util.assignRelativeAge(postLookupMap,list,Post.RELATIVE_AGE_1);
		list = lists.get(7);
		util.assignRelativeAge(postLookupMap,list,Post.RELATIVE_AGE_0);
		
	}
	
	/**
	 * Loads a hierarchial list of posts into a flat map, keyed by the postid
	 * 
	 * @param posts
	 * @param map
	 */
	private void buildLookupMap(List<Post> posts, Map<String,Post> map){
		for(Post p : posts){
			//p.setRelativeAge(Post.RELATIVE_AGE_DEFAULT);
			map.put(p.getPostId(), p);
			//buildLookupMap(p.getPosts(),map);
		}
	}
	
	/**
	 * This bizare method looks like it's just shuffling the deck chairs which it kind of is i guess 
	 * but it has a purpose. This method takes a hierarchical list of all posts and a flat list of latest
	 * posts.  The latest post list is from a different hibernate session, so i want to 
	 * pull the in session ones form the Posts list. The way to avoid this would be to remove the
	 * commit call in the middle of the readFrontPagePosts method, but if i do that i'd have to
	 * recursively sort the child posts in the posts list. I dont know that that would be any faster  
	 * 
	 * @param active
	 * @param posts
	 * @param latest
	 */
	/*
	private void extractLatestPosts(List<Post> latestInSession, List<Post> posts, List<Post> latest){
		//All root level posts are expanded.
		for(Post p : posts){
			if(latest.contains(p) ){
				latestInSession.add(p);
			}
			extractLatestPosts(latestInSession,p.getPosts(),latest);
		}
	}
	*/
	/*
	private static void assignRelativeAge(List<Post> list, String value){
		for(Post p : list){
			p.setRelativeAge(value);
		}
	}
	*/
	
	private void assignRelativeAge(Map<String,Post> map, List<Post> list, String value){
		throw new RuntimeException("Trevis thought that this method was no longer in use!!");
//		for(Post p : list){
//			Post inSessionPost = map.get(p.getPostId());
//			if(inSessionPost != null)
//				inSessionPost.setRelativeAge(value);//Remember, they're not all here on a branch read.
//		}
	}
	
	private static class PostSourceValueReader implements Median.SourceValueReader<Post>{
		public long readSourceValue(Post target) {
			return target.getDate().getTime();
		}
	}
}
