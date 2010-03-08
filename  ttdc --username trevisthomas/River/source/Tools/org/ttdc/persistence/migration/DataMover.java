package org.ttdc.persistence.migration;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.ttdc.biz.network.services.ImageService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.PersistenceTtdc;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.util.Cryptographer;
import org.ttdc.util.ServiceException;
import org.ttdc.util.web.HTMLCalendar;

import com.microsoft.sqlserver.jdbc.SQLServerException;

@Deprecated
public class DataMover {
//	private static Logger log = Logger.getLogger(DataMover.class);
//	@SuppressWarnings("unchecked")
//	Session session_tt = null;
//	Session session = null;
//	final static String imagepath = "\\\\faith\\c$\\web\\images\\";
//	//final static String imagepath = "c:\\web\\images\\";
//	
//	
//	
//	Map<Integer, Person> users = new HashMap<Integer, Person>();
//	Map<Integer, Tag> forums = new HashMap<Integer, Tag>();
//	Map<Integer, Tag> sections = new HashMap<Integer, Tag>();
//	Map<Integer, Post> threads = new HashMap<Integer, Post>();
//	
//	static Person trevis = null;
//	public static void main(String[] args) throws Exception{
//		java.awt.Toolkit.getDefaultToolkit().beep();
//		DataMover dm = new DataMover();
//		Date start = new Date();
//		try{
//		dm.goDoIt();
//		}
//		finally{
//			Date end = new Date();
//			
//			log.info("Time taken: "+(end.getTime() - start.getTime())/1000.0);
//			java.awt.Toolkit.getDefaultToolkit().beep();
//			java.awt.Toolkit.getDefaultToolkit().beep();
//		}
//		
//		
//		/*
//		Person p = UserService.getInstance().readUser("trevis");
//		
//		String path = imagepath+"../images/movies/animatrix.jpg";
//		File file = new File(path);
//		String name = path.substring(path.lastIndexOf('/')+1);
//		//ImageService.getInstance().createImage(p, file, name);
//		ImageService.getInstance().createImage(p,"http://posters.imdb.com/Posters/h/hurlyburly1.jpg",null);
//		//ImageService.getInstance().createImage(p,"http://posters.imdb.com/Covers/24/74/25.jpg","test");
//		*/
//	}
//	
//	public void goDoIt(){
//		try{
//			session_tt = PersistenceTtdc.session();
//			session = Persistence.beginSession();
//			
//			buildUsers();
//			buildForumTags();
//			buildSectionTags();
//			
//			Persistence.commit();
//			session = Persistence.beginSession();
//			
//			buildThreads();
//			
//			Persistence.commit();
//			session = Persistence.beginSession();
//			
//			//loadComments();
//			
//			PersistenceTtdc.commit();
//			Persistence.commit();
//		}
//		catch(Throwable t){
//			log.error(t);
//			PersistenceTtdc.rollback();
//			Persistence.rollback();
//			t.printStackTrace();
//		}
//	}
//	
//	private void buildUsers(){
//		Query query = session_tt.getNamedQuery("webuser.getall");
//		List<WebUsers> list = (List<WebUsers>)query.list();
//		for(WebUsers u : list){
//			log.info(u.getUserName() +" "+ u.getGuid());
//			Person p = createPerson(u);
//			if(u.getId() == 1)
//				trevis = p;
//			users.put(u.getId(), p);
//		}
//	}
//	
//	private void buildForumTags(){
//		Query query = session_tt.getNamedQuery("webForums.getall");
//		List<WebForums> list = (List<WebForums>)query.list();
//		for(WebForums forum : list){
//			//log.info(sec.getId() + " : " +sec.getName() +" "+ sec.getSubject());
//			Tag t = createTag(forum);
//			forums.put(forum.getId(), t);
//		}
//	}
//	private void buildSectionTags(){
//		Query query = session_tt.getNamedQuery("webSectionNames.getall");
//		List<WebSectionNames> list = (List<WebSectionNames>)query.list();
//		for(WebSectionNames sec : list){
//			//log.info(sec.getId() + " : " +sec.getName() +" "+ sec.getDisplayName());
//			Tag t = createTag(sec);
//			sections.put(sec.getId(), t);
//		}
//	}
//	
//	private void buildThreads(){
//		Query query = session_tt.getNamedQuery("webmain.getall");
//		List<WebMain> list = (List<WebMain>)query.list();
//		for(WebMain main : list){
//			Post p = createPost(main);
//			threads.put(main.getId(),p);
//		}
//	}
//	
//	
//	private Person createPerson(WebUsers user){
//		Cryptographer crypto =  new Cryptographer(null);
//		Person p = new Person();
//		p.setBio(user.getBio());
//		p.setBirthday(user.getBirthday());
//		p.setDate(user.getDateAdded());
//		p.setEmail(user.getEmail().trim());
//		p.setHits(user.getHits());
//		p.setLogin(user.getUserName().trim());
//		p.setName(user.getFirstName().trim() +" "+ user.getLastName().trim());
//		p.setPassword(crypto.encrypt(user.getPassword().trim()));
//		p.setStatus(Person.STATUS_INACTIVE);
//		/*
//		if(user.getWebsite() != null && !user.getWebsite().equals("")){
//			UserObject o = new UserObject();
//			o.setDate(new Date());
//			o.setDescription("Website");
//			o.setType(UserObject.TYPE_WEBPAGE);
//			o.setUrl(user.getWebsite());
//			o.setOwner(p);
//			p.addObject(o);
//		}
//		*/
//		session.save(p);
//		return p;
//	}
//	
//	private Tag createTag(WebForums forum){
//		Tag t = new Tag();
////		t.setCreator(trevis);
//		t.setDate(forum.getDateAdded());
////		t.setDescription(forum.getSubject().trim());
//		t.setType(Tag.TYPE_TOPIC);
//		t.setValue(forum.getName().trim());
//		session.save(t);
//		return t;
//	}
//	
//	private Tag createTag(WebSectionNames sec){
//		Tag t = new Tag();
////		t.setCreator(trevis);
//		t.setDate(new Date());
//		//t.setDescription("");
//		t.setType(Tag.TYPE_TOPIC);
//		String name = sec.getName().trim();
//		if("movies".compareToIgnoreCase(name) == 0)
//			t.setValue("About Movies");
//		else
//			t.setValue(name);
//		
//		Query query = session.getNamedQuery("tag.getByValue");
//		Tag existing = (Tag)query.setString("value", t.getValue()).uniqueResult();
//		if(existing == null)
//			session.save(t);
//		else
//			t = existing;
//		return t;
//	}
//	
//	private Tag findOrCreateTag(Post post, String value, String type, Person creator, Date date){
//		value = value.trim();
//		Query query = session.getNamedQuery("tag.getByValueAndType");
//		Tag t = (Tag)query.setString("value", value).setString("type",type).uniqueResult();
//		if(t == null){
//			t = new Tag();
////			t.setCreator(creator);
//			t.setDate(date);
//			t.setType(type);
//			t.setValue(value);
//			session.save(t);
//		}
//		tag(post, t, creator, date);
//		return t;
//	}
//	
//	private void tag(Post post, Tag tag, Person person, Date date){
//		AssociationPostTag tagass = new AssociationPostTag();
//		tagass.setPost(post);
//		tagass.setCreator(person);
//		tagass.setTag(tag);
//		tagass.setDate(date);
//		session.save(tagass);
//	}
//	
////	private void tagCalender(Post post, Person creator){
////		Calendar cal = GregorianCalendar.getInstance();
////		cal.setTime(post.getDate());
////		findOrCreateTag(post, ""+cal.get(GregorianCalendar.DAY_OF_MONTH), Tag.TYPE_DATE_DAY, creator , post.getDate());
////		findOrCreateTag(post, HTMLCalendar.getMonthName(cal.get(GregorianCalendar.MONTH)+1), Tag.TYPE_DATE_MONTH, creator , post.getDate());
////		findOrCreateTag(post, ""+cal.get(GregorianCalendar.YEAR), Tag.TYPE_DATE_YEAR, creator , post.getDate());
////	}
//	
//	private String determineRating(String rating){
//		if(rating != null){
//			try{
//				float value = Float.parseFloat(rating);
//				if(value > 9.5){
//					return Tag.VALUE_RATING_5;
//				}
//				else if(value > 9.0){
//					return Tag.VALUE_RATING_4_5;
//				}
//				else if(value >= 8.9){
//					return Tag.VALUE_RATING_4;
//				}
//				else if(value >= 8.3){
//					return Tag.VALUE_RATING_3_5;
//				}
//				else if(value >= 8.0){
//					return Tag.VALUE_RATING_3;				
//				}
//				else if(value >= 7.3){
//					return Tag.VALUE_RATING_2_5;	
//				}
//				else if(value >= 7.0){
//					return Tag.VALUE_RATING_2;				
//				}
//				else if(value >= 6.5){
//					return Tag.VALUE_RATING_1_5;
//				}
//				else if(value >= 6.0){
//					return Tag.VALUE_RATING_1;				
//				}
//				else if(value >= 5.0){
//					return Tag.VALUE_RATING_0_5;
//				}
//				else{
//					return Tag.VALUE_RATING_0_5;
//				}
//			}
//			catch(NumberFormatException e){
//				if(rating.equals("A+")){
//					return Tag.VALUE_RATING_5;
//				}
//				else if(rating.equals("A")){
//					return Tag.VALUE_RATING_4_5;
//				}
//				else if(rating.equals("A-")){
//					return Tag.VALUE_RATING_4;
//				}
//				else if(rating.equals("B+")){
//					return Tag.VALUE_RATING_3_5;
//				}
//				else if(rating.equals("B")){
//					return Tag.VALUE_RATING_3;				
//				}
//				else if(rating.equals("B-")){
//					return Tag.VALUE_RATING_3;
//				}
//				else if(rating.equals("C+")){
//					return Tag.VALUE_RATING_2_5;	
//				}
//				else if(rating.equals("C")){
//					return Tag.VALUE_RATING_2;				
//				}
//				else if(rating.equals("C-")){
//					return Tag.VALUE_RATING_2;
//				}
//				else if(rating.equals("D+")){
//					return Tag.VALUE_RATING_1_5;
//				}
//				else if(rating.equals("D")){
//					return Tag.VALUE_RATING_1;				
//				}
//				else if(rating.equals("D-")){
//					return Tag.VALUE_RATING_0_5;
//				}
//				else if(rating.equals("F+")){
//					return Tag.VALUE_RATING_0_5;
//				}
//				else if(rating.equals("F")){
//					return Tag.VALUE_RATING_0_5;				
//				}
//				else if(rating.equals("F-")){
//					return Tag.VALUE_RATING_0_5;
//				}
//				else{
//					return null;
//				}
//			}
//		}
//		return null;
//	}
//	
//	private Post createPost(WebMain main){
//		Post p = new Post();
//		Entry entry = new Entry();
//		Post review = null;
//		Person creator = users.get(main.getUserId());
//		String movieReview = "";
//		String movieMeta = "";
//		if(main.getMovieTitle() != null){
//			movieReview = main.getEntry();
//			entry.setBody("");//Blanking the root review. 
//			/*
//			int index = movieReview.lastIndexOf("Directed");
//			if(index > 0){
//				movieReview.replace("i[", "");
//				movieReview.replace("]i", "");
//				index = movieReview.indexOf("Directed");
//				movieMeta = movieReview.substring(index,movieReview.length());
//				movieReview = movieReview.substring(0,index);
//				
//				if(movieReview == null){
//					movieReview = main.getEntry();
//				}
//				if(movieMeta == null)
//					movieMeta = "MISSING MOVIE INFO - But thought i had it.";
//				entry.setBody(movieMeta);
//			}
//			else{
//				entry.setBody("MISSING MOVIE INFO");
//			}
//			*/
//		}
//		else{
//			entry.setBody(main.getEntry());
//		}
//		p.setDate(main.getDateAdded());
//		session.save(p);
//		entry.setPost(p);
//		session.save(entry);
//		session.update(p);
//		
//		Tag topic = null;
//		Tag topic2 = null;
//		
//		if(main.getMovieTitle() != null){
//			//Reviews need to be split.  The title is root and the review is a child
//			review = new Post();
//			entry = new Entry();
//			entry.setBody(movieReview);
//			review.setDate(main.getDateAdded());
//			review.addEntry(entry);
//			session.save(review);
//			
//			findOrCreateTag(review, Tag.TYPE_REVIEW, Tag.TYPE_REVIEW, creator, main.getDateAdded());
//			findOrCreateTag(review, creator.getLogin(), Tag.TYPE_CREATOR, creator, main.getDateAdded());
//			tagCalender(review,creator);
//			p.addChild(review);
//			session.update(p);
//			//
//			// Movies are tagged with the title as the topic and as a title.  That way others can tag 
//			// threads about the movie that arent the movie. And they wont colide.
//			//topic = findOrCreateTag(p,main.getMovieTitle().getTitle(), Tag.TYPE_TOPIC, creator,main.getDateAdded()); 
//			findOrCreateTag(p, main.getMovieTitle().getTitle(), Tag.TYPE_TOPIC, creator, main.getDateAdded());//SWITCHED FROM TITLE AT BUILD 13
//			findOrCreateTag(p, main.getMovieTitle().getSortTitle(), Tag.TYPE_SORT_TITLE, creator, main.getDateAdded());
//			
//			findOrCreateTag(p, Tag.TYPE_MOVIE, Tag.TYPE_MOVIE, creator, main.getDateAdded());
//			
//			findOrCreateTag(p, main.getMovieTitle().getImdb(), Tag.TYPE_URL, creator, main.getDateAdded());
//			findOrCreateTag(p, Tag.TYPE_RATABLE, Tag.TYPE_RATABLE, creator, main.getDateAdded());
//			findOrCreateTag(p, main.getMovieTitle().getYear(), Tag.TYPE_RELEASE_YEAR, creator, main.getDateAdded());
//			
//			String rating = determineRating(main.getMovieRating());
//			if(rating != null){
//				findOrCreateTag(p, rating, Tag.TYPE_RATING, creator, main.getDateAdded());
//			}
//			String imgpath = main.getMovieTitle().getImage();
//			if(imgpath != null && !imgpath.trim().equals("")){
//				imgpath = imgpath.trim();
//				File file = new File(imagepath+imgpath);
//				
//				ImageFull fullImage = null;
//				try{
//					Persistence.commit();
//					if(file.exists()){
//						imgpath = imgpath.replace('\\', '/');
//						String name = imgpath.substring(imgpath.lastIndexOf('/')+1);
//						fullImage = ImageService.getInstance().createImage(creator, file, name);
//					}
//					else{
//						String name = main.getMovieTitle().getTitle().replace(" ", "");
//						fullImage = ImageService.getInstance().createImage(creator, imgpath,name);
//					}
//					session = Persistence.beginSession();
//					if(fullImage != null){
//						Image image = null;
//						session.save(fullImage);
//						Persistence.commit();
//						session = Persistence.beginSession();
//						
//						Query query = session.getNamedQuery("image.getById").setString("imageId", fullImage.getImageId());
//						image = (Image) query.uniqueResult();
//						p.setImage(image);
//						session.update(p);
//					}
//				}catch(ServiceException e){
//					log.info(e);
//					session = Persistence.beginSession();
//				}
//				
//			}
//			
//		}
//		else{
//			int sectionId = main.getSectionId();
//			if(sectionId == 5){
//				//it's a forum, use that as the topic
//				int forumId = main.getForumId();
//				topic2 = forums.get(forumId);
//			}
//			else{
//				topic2 = sections.get(sectionId);
//			}
//			topic = null; //No topic tags!
//			//topic = findOrCreateTag(p,main.getTitle(), Tag.TYPE_TOPIC, creator,main.getDateAdded()); 
//			findOrCreateTag(p,main.getTitle(), Tag.TYPE_TOPIC, creator, main.getDateAdded());//SWITCHED FROM TITLE AT BUILD 13
//			//tag(p,topic2,creator); //Moved to *after* title
//			
//			if(main.getEntry().toLowerCase().indexOf("http://") > 0){
//				findOrCreateTag(p,Tag.VALUE_LINK , Tag.TYPE_DISPLAY, creator,main.getDateAdded());
//			}
//					
//		}
//		findOrCreateTag(p,creator.getLogin(), Tag.TYPE_CREATOR, creator, main.getDateAdded());
//		
//		tagCalender(p,creator);
//		
//		//Adding the title of threads as a topic tag the thread and all replies too.
//		
//		if(topic2 != null)
//			tag(p,topic2,creator, new Date()); //This tag should be added after the Thread topic for sanity.
//		
//		Post target = null;
//		if(review != null)
//			target = review; //Of the main entity was a review, two posts were created. The comments should stick to the review, not the movie
//		else
//			target = p;
//		
//		log.info("Getting posts for: " + main.getTitle() + "(ID: "+main.getId()+")");
//		importComments(target,main.getId(),topic,topic2);
//		
//		Persistence.commit();
//		session = Persistence.beginSession(); 
//		return target;
//	}
//	
//	private void importComments(Post parent, int mainId, Tag titleTopic, Tag forumSectionTopic){
//		Query query = session_tt.getNamedQuery("webComments.getForMainId").setInteger("mainId", mainId);
//		List<WebComments> list = (List<WebComments>)query.list();
//		for(WebComments comment : list){
//			Post p = createPost(parent,comment,titleTopic,forumSectionTopic);
//		}
//	}
//	private Post createPost(Post parent, WebComments comment, Tag titleTopic, Tag forumSectionTopic){
//		Post post = null;
//		try{
//			log.info("Comment: "+comment.getId());
//			Person creator = users.get(comment.getUserId());
//			post = new Post();
//			Entry entry = new Entry();
//			entry.setBody(comment.getEntry());
//			post.setDate(comment.getDateAdded());
//			post.addEntry(entry);
//			session.save(post);
//			
//			parent.addChild(post);
//			session.update(parent);
//			// Removing topic tags from comments
//			/*
//			tag(post,titleTopic, creator,comment.getDateAdded());
//			if(forumSectionTopic != null)
//				tag(post,forumSectionTopic, creator, new Date());
//			*/
//			
//			
//			findOrCreateTag(post,creator.getLogin(), Tag.TYPE_CREATOR, creator, comment.getDateAdded());
//			tagCalender(post,creator);
//			if(comment.getEntry().toLowerCase().indexOf("http://") > 0){
//				findOrCreateTag(post,Tag.VALUE_LINK , Tag.TYPE_DISPLAY, creator,comment.getDateAdded());
//			}
//			
//		}
//		catch(NullPointerException e){
//			e.printStackTrace();
//		}
//		return post;
//		
//		
//	}
}



