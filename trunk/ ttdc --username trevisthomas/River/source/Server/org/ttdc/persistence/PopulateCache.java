package org.ttdc.persistence;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;

public class PopulateCache {
	private static Logger log = Logger.getLogger(PopulateCache.class);
	
	@SuppressWarnings("unchecked")
	public static void init(){
		List<AssociationPostTag> asses;
		List<Tag> tags;
		List<Person> people;
		List<Image> images;
		List<UserObject> objects;
		List<UserObjectTemplate> objectTemplates;
		List<Privilege> privileges;
		List<Post> posts;
		
		
		Session session = Persistence.beginSession();
		Query query;
		Date start,end;
		
		start = new Date();
		query = session.getNamedQuery("privilege.getAll");
		privileges = query.list();
		end = new Date();
		log.info("Loaded all privileges in: "+(end.getTime() - start.getTime())/1000.0);
		
		start = new Date();
		query = session.getNamedQuery("person.getAllForCache");
		people = query.list();
		end = new Date();
		log.info("Loaded all persons in: "+(end.getTime() - start.getTime())/1000.0);
		
		start = new Date();
		query = session.getNamedQuery("userObjectTemplate.getAll");
		objectTemplates = query.list();
		end = new Date();
		log.info("Loaded all user object templates in: "+(end.getTime() - start.getTime())/1000.0);

		start = new Date();
		query = session.getNamedQuery("object.getAll");
		objects = query.list();
		end = new Date();
		log.info("Loaded all user objects in: "+(end.getTime() - start.getTime())/1000.0);

		start = new Date();
		query = session.getNamedQuery("image.getAll");
		images = query.list();
		end = new Date();
		log.info("Loaded all images in: "+(end.getTime() - start.getTime())/1000.0);
		
		start = new Date();
		query = session.getNamedQuery("tag.getAll");
		tags = query.list();
		end = new Date();
		log.info("Loaded all tags in: "+(end.getTime() - start.getTime())/1000.0);
		
		start = new Date();
		query = session.getNamedQuery("post.getAll");
		posts = query.list();
		for(Post post : posts){
			post.getTagAssociations().toString();
			post.getPosts().toString();
		}
		end = new Date();
		log.info("Loaded all posts in: "+(end.getTime() - start.getTime())/1000.0);
		
		/*
		start = new Date();
		query = session.getNamedQuery("ass.getAll");
		asses = query.list();
		end = new Date();
		log.info("Loaded all tag associations in: "+(end.getTime() - start.getTime())/1000.0);
		*/
		
		start = new Date();
		Persistence.commit();
		end = new Date();
		log.info("Commit took: "+(end.getTime() - start.getTime())/1000.0);
		
		log.info("Loaded... ");
		//log.info("Tag Associations: "+asses.size());
		//log.info("Posts: "+posts.size());
		log.info("People: "+people.size());
		log.info("Tags: "+tags.size());
		log.info("Images: "+images.size());
		log.info("UserObjects: "+objects.size());
		log.info("User Object Templates: "+objectTemplates.size());
		log.info("Privileges: "+privileges.size());
		log.info("Posts: "+posts.size());
		log.info("Done.");
	}
}
