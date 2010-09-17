package org.ttdc.util;

import java.util.List;
import java.util.StringTokenizer;

import org.ttdc.persistence.objects.Post;

public class PathSegmentizer {

	public static int[] calculatePathSegmentMaximums(List<Post> posts) {
		int maxSegments = depthTest(posts);
		
		int [] results = new int[maxSegments];
		
		for(Post post : posts){
			StringTokenizer tokenizer = new StringTokenizer(post.getPath(),".");
			String tmp;
			int ndx = 0;
			while(tokenizer.hasMoreTokens()){
				tmp = tokenizer.nextToken();
				int val = Integer.parseInt(tmp);
				if(results[ndx] < val){
					results[ndx] = val;
				}
				ndx++;
			}
		}
		
		return results;
	}

	private static int depthTest(List<Post> posts) {
		int depth = 0;
		String grandpath = "";
		for(Post post : posts){
			if(post.getPath().length() > grandpath.length()){
				grandpath = post.getPath(); 
			}
		}
		StringTokenizer tokenizer = new StringTokenizer(grandpath,".");
		depth = tokenizer.countTokens();
		return depth;
	}

	public static int[] segmentizePath(Post post) {
		StringTokenizer tokenizer = new StringTokenizer(post.getPath(),".");
		int [] results = new int[tokenizer.countTokens()];
		int ndx = 0;
		while(tokenizer.hasMoreTokens()){
			int val = Integer.parseInt(tokenizer.nextToken());
			results[ndx] = val;
			ndx++;
		}
		
		return results;
	}
	
	public static void segmentizeChildPaths(Post thread) {
		List<Post> flatPosts = thread.getPosts();
		for(Post p : flatPosts){
			p.setPathSegments(segmentizePath(p));
		}
	}
}
