package org.ttdc.gwt.server.command.executors.utils;

import java.util.List;

import org.hibernate.Hibernate;
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.dao.InboxDao;
import org.ttdc.gwt.server.dao.Inflatinator;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;

public final class PaginatedResultConverters {
	private final static <S,D> PaginatedList<D> copyMetaData(PaginatedList<S> source){
		PaginatedList<D> destination = new PaginatedList<D>();
		
		destination.setCurrentPage(source.getCurrentPage());
		destination.setPageSize(source.getPageSize());
		destination.setPhrase(source.getPhrase());
		destination.setTotalResults(source.getTotalResults());
		
		return destination;
	}
	
	public final static PaginatedList<GPost> convertSearchResults(PaginatedList<Post> results, Person person){
		PaginatedList<GPost> gResults = copyMetaData(results);
//		Inflatinator inf = new Inflatinator(results.getList());
//		gResults.setList(inf.extractPosts());
		InboxDao inboxDao = new InboxDao(person);
		gResults.setList(FastPostBeanConverter.convertPosts(results.getList(), inboxDao));
		return gResults;
	}

	public static PaginatedList<GPost> convertSearchResultsHierarchy(PaginatedList<Post> results) {
		PaginatedList<GPost> gResults = copyMetaData(results);
		Inflatinator inf = new Inflatinator(results.getList());
		gResults.setList(inf.extractPostHierarchyAtRoot());
		return gResults;
	}
	
	public static PaginatedList<GPost> convertSearchResultsNested(PaginatedList<Post> results, Person person) {
		PaginatedList<GPost> gResults = copyMetaData(results);
		Inflatinator inf = new Inflatinator(results.getList());
		gResults.setList(inf.extractPostsNested(person));
		return gResults;
	}
	
	public final static PaginatedList<GImage> convertImageList(PaginatedList<Image> results){
		PaginatedList<GImage> gResults = copyMetaData(results);
		gResults.setList(FastPostBeanConverter.convertImages(results.getList()));
		return gResults;
	}
	
	public final static PaginatedList<GPerson> convertPersonListWithPriviledges(PaginatedList<Person> results){
		PaginatedList<GPerson> gResults = copyMetaData(results);
		gResults.setList(FastPostBeanConverter.convertPersonList(results.getList(),FastPostBeanConverter.Detail.PRIVILEGES));
		return gResults;
	}
	
	public final static PaginatedList<GPerson> convertPersonListWithFullDetails(PaginatedList<Person> results){
		PaginatedList<GPerson> gResults = copyMetaData(results);
		gResults.setList(FastPostBeanConverter.convertPersonList(results.getList(),FastPostBeanConverter.Detail.PRIVILEGES, FastPostBeanConverter.Detail.USEROBJECTS));
		return gResults;
	}
}
