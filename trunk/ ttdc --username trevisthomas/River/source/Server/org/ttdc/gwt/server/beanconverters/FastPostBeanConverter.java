package org.ttdc.gwt.server.beanconverters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Hibernate;
import org.mortbay.log.Log;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GEntry;
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GPrivilege;
import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.persistence.objects.Style;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;


public class FastPostBeanConverter {
	public static ArrayList<GPost> convertPosts(List<Post> persistentPostList){
		ArrayList<GPost> rpcPostList = new ArrayList<GPost>();
		for(Post p : persistentPostList){
			//if(!p.isHidden()){
				GPost rpcPost = convertPost(p);
				rpcPostList.add(rpcPost);
			//}
		}
		return rpcPostList;
	}
	public static ArrayList<GEntry> convertEntries(List<Entry> entryList){
		ArrayList<GEntry> rpcEntryList = new ArrayList<GEntry>();
		for(Entry e : entryList){
			rpcEntryList.add(convertEntry(e));
		}
		return rpcEntryList;
	}
	public static ArrayList<GAssociationPostTag> convertAssociationsPostTag(List<AssociationPostTag> asses){
		ArrayList<GAssociationPostTag> gAssList = new ArrayList<GAssociationPostTag>();
		for(AssociationPostTag ass : asses){
			gAssList.add(convertAssociationPostTag(ass));
		}
		return gAssList;
	}
	public static ArrayList<GUserObject> convertUserObjects(List<UserObject> objects){
		ArrayList<GUserObject> gObjects = new ArrayList<GUserObject>();
		if(Hibernate.isInitialized(objects)){
			for(UserObject object : objects){
				gObjects.add(convertUserObject(object));
			}
		}
		return gObjects;
	}
	public static List<GPrivilege> convertPrivileges(List<Privilege> privileges){
		List<GPrivilege> gPrivileges = new ArrayList<GPrivilege>();
		if(Hibernate.isInitialized(privileges)){
			for(Privilege privilege : privileges){
				gPrivileges.add(convertPrivilege(privilege));
			}
		}
		return gPrivileges;		
	}
	
	public static List<GImage> convertImages(List<Image> images){
		ArrayList<GImage> gImages = new ArrayList<GImage>();
		for(Image image : images){
			gImages.add(convertImage(image));
		}
		return gImages;
	}
	
	//Initially created for inflating movie review summaries
	public static ArrayList<GPost> convertPostsSimple(List<Post> persistentPostList){
		ArrayList<GPost> list = new ArrayList<GPost>();
		for(Post p : persistentPostList){
			GPost rpcPost = convertPostSimple(p);
			list.add(rpcPost);
		}
		return list;
	}
	//Initially created for inflating movie review summaries
	public static GPost convertPostSimple(Post p) {
		GPost gPost = new GPost();
		gPost.setDate(p.getDate());
		gPost.setLatestEntry(convertEntry(p.getEntry()));
		gPost.setPostId(p.getPostId());
		gPost.setPath(p.getPath());
		gPost.setMass(p.getMass());
		gPost.setReplyCount(p.getReplyCount());
		return gPost;
	}
	
	public static GPost convertPost(Post p) {
		GPost gPost = new GPost();
		gPost.setDate(p.getDate());
		//gPost.setEntries(convertEntries(p.getEntries()));

		//This full entry list may not be necessary (Dec 6 2009)
//		List<Entry> singleEntry = new ArrayList<Entry>();
//		singleEntry.add(p.getEntry());
//		gPost.setEntries(convertEntries(singleEntry));
		
		gPost.setLatestEntry(convertEntry(p.getEntry()));
		gPost.setPostId(p.getPostId());
		//gPost.setPosts(convertPosts(p.getPosts()));
		gPost.setTagAssociations(convertAssociationsPostTag(p.getTagAssociations()));
		gPost.setTitleTag(convertTag(p.getTitleTag()));
		gPost.setCreator(convertPerson(p.getCreator()));
		gPost.setAvgRatingTag(convertTag(p.getAvgRatingTag()));
		gPost.setPublishYear(p.getPublishYear());
		gPost.setMetaMask(p.getMetaMask());
		gPost.setUrl(p.getUrl());
		gPost.setRootPost(p.isRootPost());
		gPost.setThreadPost(p.isThreadPost());
		gPost.setPath(p.getPath());
		gPost.setMass(p.getMass());
		gPost.setReplyCount(p.getReplyCount());
		
		//If a post is a movie, get the reviews.  4/19/2010
		if(p.isMovie()){
			gPost.setPosts(convertPostsSimple(p.getPosts()));
		}
		else{
			if(!p.isRootPost())
				gPost.setRoot(convertPost(p.getRoot()));
			if(!p.isThreadPost() && !p.isRootPost())
				gPost.setThread(convertPost(p.getThread()));
		}
			
		
		
		if(p.getImage() != null)
			gPost.setImage(convertImage(p.getImage()));
		else if(p.isMovie())
			gPost.setImage(convertImage(InitConstants.DEFAULT_POSTER));
			
		return gPost;
	}
	
	public static GEntry convertEntry(Entry e) {
		GEntry gEntry = new GEntry();
		gEntry.setBody(e.getBody());
		gEntry.setDate(e.getDate());
		gEntry.setEntryId(e.getEntryId());
		gEntry.setSummary(e.getSummary());
		return gEntry;
	}
	
	
	public static GPerson convertPerson(Person p){
//		GPerson gPerson = new GPerson();
//		//gPerson.setBio(p.getBio());
//		gPerson.setBirthday(p.getBirthday());
//		gPerson.setDate(p.getDate());
//		gPerson.setEmail(p.getEmail());
//		gPerson.setHits(p.getHits());
//		gPerson.setLastAccessDate(p.getLastAccessDate());
//		gPerson.setLogin(p.getLogin());
//		gPerson.setName(p.getName());
//		gPerson.setPersonId(p.getPersonId());
//		gPerson.setPassword(p.getPassword());
//		gPerson.setStatus(p.getStatus());
		
		GPerson gPerson = convertPersonNakid(p);
		gPerson.setObjects(convertUserObjects(p.getObjects()));
		gPerson.setPrivileges(convertPrivileges(p.getPrivileges()));
		if(p.getImage() != null)
			gPerson.setImage(convertImage(p.getImage()));
		else
			gPerson.setImage(convertImage(InitConstants.DEFAULT_AVATAR));
		
		if(p.getStyle() != null){
			gPerson.setStyle(convertStyleLite(p.getStyle()));
		}
		else
			gPerson.setStyle(convertStyleLite(InitConstants.DEFAULT_STYLE));
		
		gPerson.setAnonymous(p.isAnonymous());
//		try{
//			Tag tag = TagDao.loadCreatorTag(p.getPersonId());
//			gPerson.setCreatorTagId(tag.getTagId());
//		}
//		catch (Exception e) {
//			Log.info("User has no creator tag. They must be brand new and not have any posts");
//		}
		
		return gPerson;
	}
	
	public static GPerson convertPersonWithBio(Person p){
		GPerson gPerson = convertPerson(p);
		gPerson.setBio(p.getBio());
		return gPerson;
	}
	
	public static GPerson convertPersonNakid(Person p){
		GPerson gPerson = new GPerson();
		//gPerson.setBio(p.getBio());
		gPerson.setBirthday(p.getBirthday());
		gPerson.setDate(p.getDate());
		gPerson.setEmail(p.getEmail());
		gPerson.setHits(p.getHits());
		gPerson.setLastAccessDate(p.getLastAccessDate());
		gPerson.setLogin(p.getLogin());
		gPerson.setName(p.getName());
		gPerson.setPersonId(p.getPersonId());
		gPerson.setStatus(p.getStatus());
		gPerson.setAnonymous(p.isAnonymous());
		return gPerson;
	}
	
	public static GTag convertTag(Tag t){
		if(t == null) return null;  // Some tags are optional now
		GTag rpcTag = new GTag();
		//rpcTag.setCreator(convertPerson(t.getCreator()));
//		rpcTag.setCreator(convertPersonNakid(t.getCreator()));
		rpcTag.setDate(t.getDate());
//		rpcTag.setDescription(t.getDescription());
		rpcTag.setTagId(t.getTagId());
		rpcTag.setType(t.getType());
		rpcTag.setValue(t.getValue());
		rpcTag.setMass(t.getMass());
		return rpcTag;
	}
	
	public static GAssociationPostTag convertAssociationPostTag(AssociationPostTag ass){
		GAssociationPostTag gAss = new GAssociationPostTag();
		gAss.setCreator(convertPerson(ass.getCreator()));
		gAss.setDate(ass.getDate());
		gAss.setGuid(ass.getGuid());
		//gAss.setPost(convertPost(ass.getPost()));
		gAss.setTag(convertTag(ass.getTag()));
		return gAss;
	}
	
	public static GUserObject convertUserObject(UserObject uo){
		GUserObject gUo = new GUserObject();
		gUo.setDate(uo.getDate());
		gUo.setDescription(uo.getDescription());
		gUo.setName(uo.getName());
		gUo.setObjectId(uo.getObjectId());
		//gUo.setOwner(convertPerson(uo.getOwner()));
		gUo.setTemplate(convertUserObjectTemplate(uo.getTemplate()));
		gUo.setType(uo.getType());
		gUo.setUrl(uo.getUrl());
		gUo.setValue(uo.getValue());
		return gUo;
	}
	
	public static GUserObjectTemplate convertUserObjectTemplate(UserObjectTemplate userObjectTemplate){
		if(userObjectTemplate == null) return null;
		GUserObjectTemplate gUserObjectTemplate = new GUserObjectTemplate();
		//gUserObjectTemplate.setCreator(convertPerson(userObjectTemplate.getCreator()));
		gUserObjectTemplate.setImage(convertImage(userObjectTemplate.getImage()));
		gUserObjectTemplate.setName(userObjectTemplate.getName());
		gUserObjectTemplate.setTemplateId(userObjectTemplate.getTemplateId());
		gUserObjectTemplate.setType(userObjectTemplate.getType());
		gUserObjectTemplate.setValue(userObjectTemplate.getValue());
		return gUserObjectTemplate;
	}
	
	public static GImage convertImage(Image image){
		if(image == null) return null;
		GImage gImage = new GImage();
		gImage.setDate(image.getDate());
		gImage.setHeight(image.getHeight());
		gImage.setWidth(image.getWidth());
		gImage.setImageId(image.getImageId());
		gImage.setName(image.getName());
		gImage.setThumbnailName(image.getSquareThumbnailName()); //Trevis... you better decide
		//gImage.setOwner(convertPerson(image.getOwner()));
		gImage.setOwner(convertPersonNakid(image.getOwner()));
		return gImage;
	}
	
	public static GImage convertImage(ImageFull image){
		if(image == null) return null;
		GImage gImage = new GImage();
		gImage.setDate(image.getDate());
		gImage.setHeight(image.getHeight());
		gImage.setWidth(image.getWidth());
		gImage.setImageId(image.getImageId());
		gImage.setName(image.getName());
		//gImage.setThumbnailName(image.getSquareThumbnailName()); //Trevis... you better decide
		//gImage.setOwner(convertPerson(image.getOwner()));
		gImage.setOwner(convertPersonNakid(image.getOwner()));
		return gImage;
	}
	
	public static GPrivilege convertPrivilege(Privilege privilege){
		GPrivilege gPrivilege = new GPrivilege();
		gPrivilege.setName(privilege.getName());
		gPrivilege.setPrivilegeId(privilege.getPrivilegeId());
		gPrivilege.setValue(privilege.getValue());
		return gPrivilege;
	}
	
	public static List<GPerson> convertPersonList(List<Person> list) {
		List<GPerson> gList = new ArrayList<GPerson>();
		GPerson gPerson;
		for(Person p : list){
			//Hibernate.initialize(p.getPrivileges());//Force init, so that they get transfered.
			gPerson = convertPerson(p);
			gList.add(gPerson);
		}
		return gList;
	}
	
	public static List<GUserObjectTemplate> convertUserObjectTemplateList(List<UserObjectTemplate> list){
		List<GUserObjectTemplate> gList = new ArrayList<GUserObjectTemplate>();
		GUserObjectTemplate gTemplate;
		for(UserObjectTemplate t : list){
			gTemplate = convertUserObjectTemplate(t);
			gList.add(gTemplate);
		}
		return gList;
	}
	
	public static enum Detail {PRIVILEGES,USEROBJECTS};
	public static List<GPerson> convertPersonList(List<Person> list, Detail ...details) {
		List<Detail> detailList = Arrays.asList(details);
		List<GPerson> gList = new ArrayList<GPerson>();
		GPerson gPerson;
		for(Person p : list){
			if(detailList.contains(Detail.PRIVILEGES))
				Hibernate.initialize(p.getPrivileges());//Force init, so that they get transfered.
			if(detailList.contains(Detail.USEROBJECTS))
				Hibernate.initialize(p.getObjects());//Force init, so that they get transfered.
			
			gPerson = convertPerson(p);
			gList.add(gPerson);
		}
		return gList;
	}
	
	public static GStyle convertStyleLite(Style style){
		if(style == null) return null;
		GStyle gStyle = new GStyle();
		gStyle.setCss(style.getCss());
		gStyle.setDate(style.getDate());
		gStyle.setDescription(style.getDescription());
		gStyle.setName(style.getName());
		gStyle.setStyleId(style.getStyleId());
		gStyle.setDefaultStyle(style.isDefaultStyle());
		return gStyle;
	}
	
	public static GStyle convertStyle(Style style){
		if(style == null) return null;
		GStyle gStyle = convertStyleLite(style);
		gStyle.setCreator(convertPerson(style.getCreator()));
		return gStyle;
		
	}
	public static List<GStyle> convertStyles(List<Style> list) {
		List<GStyle> gList = new ArrayList<GStyle>();
		GStyle gStyle;
		for(Style s : list){
			gStyle = convertStyle(s);
			gList.add(gStyle);
		}
		return gList;
	}
	
	public static List<GTag> convertTags(List<Tag> list){
		List<GTag> gList = new ArrayList<GTag>();
		GTag gTag;
		for(Tag t : list){
			gTag = convertTag(t);
			gList.add(gTag);
		}
		return gList;
	}
}
