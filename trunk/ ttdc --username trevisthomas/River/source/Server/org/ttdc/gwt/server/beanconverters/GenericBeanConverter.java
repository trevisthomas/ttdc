package org.ttdc.gwt.server.beanconverters;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GEntry;
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GPrivilege;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;

public class GenericBeanConverter {
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
	
	public static List<GTag> convertTags(List<Tag> tags){
		List<GTag> gTags = new ArrayList<GTag>();
		for(Tag t : tags){
			gTags.add(convertTag(t));
		}
		return gTags;
	}
	
	public static GPost convertPost(Post p) {
		GPost gPost = new GPost();
		gPost.setDate(p.getDate());
		gPost.setEntries(convertEntries(p.getEntries()));
		gPost.setPostId(p.getPostId());
		gPost.setPosts(convertPosts(p.getPosts()));
		gPost.setTagAssociations(convertAssociationsPostTag(p.getTagAssociations()));
		return gPost;
	}
	
	public static GEntry convertEntry(Entry e) {
		GEntry gEntry = new GEntry();
		gEntry.setBody(e.getBody());
		gEntry.setDate(e.getDate());
		gEntry.setEntryId(e.getEntryId());
		return gEntry;
	}
	
	public static GPerson convertPerson(Person p){
		if(p == null) return null; //I added this while i was working on tag search
		GPerson gPerson = new GPerson();
		gPerson.setBio(p.getBio());
		gPerson.setBirthday(p.getBirthday());
		gPerson.setDate(p.getDate());
		gPerson.setEmail(p.getEmail());
		gPerson.setHits(p.getHits());
		gPerson.setLastAccessDate(p.getLastAccessDate());
		gPerson.setLogin(p.getLogin());
		gPerson.setName(p.getName());
		gPerson.setPersonId(p.getPersonId());
		gPerson.setStatus(p.getStatus());
		gPerson.setObjects(convertUserObjects(p.getObjects()));
		gPerson.setPrivileges(convertPrivileges(p.getPrivileges()));
		gPerson.setImage(convertImage(p.getImage()));
		gPerson.setAnonymous(p.isAnonymous());
		return gPerson;
	}
	
	public static GTag convertTag(Tag t){
		GTag rpcTag = new GTag();
//		rpcTag.setCreator(convertPerson(t.getCreator()));
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
		gUo.setName(uo.getObjectId());
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
		return gImage;
	}
	
	public static GPrivilege convertPrivilege(Privilege privilege){
		GPrivilege gPrivilege = new GPrivilege();
		gPrivilege.setName(privilege.getName());
		gPrivilege.setPrivilegeId(privilege.getPrivilegeId());
		gPrivilege.setValue(privilege.getValue());
		return gPrivilege;
	}
}
