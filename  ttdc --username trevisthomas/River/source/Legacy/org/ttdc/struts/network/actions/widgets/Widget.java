package org.ttdc.struts.network.actions.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.config.ParentPackage;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.biz.network.services.WidgetService;
import org.ttdc.biz.network.services.helpers.PersonsPost;
import org.ttdc.biz.network.services.helpers.TrafficCache;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.UserObjectTemplate;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;
import org.ttdc.util.web.Month;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@ParentPackage("ttdc-lite")
public class Widget extends ActionSupport implements SecurityAware {
	private Person person;
	private String action;
	private List<Person> traffic;
	private boolean update = false;
	private String guid;
	private long timestamp = new Date().getTime();
	private Month month;
	private boolean newcontent = false;
	private List<Post> posts;
	private String title;
	private boolean showUser = true;
	
	private String latestPostId;
	private AssociationPostTag latestTagAssociation;

	private List<PersonsPost> movieData;
	

	@Override
	public String execute() throws Exception {
		try{
			if(action.equals(UserObjectTemplate.WIDGET_TRAFFIC)){
				traffic = WidgetService.getInstance().getTraffic();
				return "traffic";
			}
			else if(action.equals(UserObjectTemplate.WIDGET_CALENDAR)){
				month = WidgetService.getInstance().getCurrentCalendar();
				return "calendar";
			}
			else if(action.equals(UserObjectTemplate.WIDGET_MOVIE)){
				movieData = WidgetService.getInstance().getMovieWidgetData();
				return "movie";
			}
			else if(action.equals(UserObjectTemplate.WIDGET_HOT_TOPICS)){
				posts = WidgetService.getInstance().getHotTopics(10);
				title = "Hot Topics";
				return "postsHotTopics";
			}
			else if(action.equals(UserObjectTemplate.WIDGET_NEW_THREADS)){
				posts = WidgetService.getInstance().getNewThreads(10);
				title = "Latest Threads";
				return "postsLatestThreads";
			}
			else if(action.equals(UserObjectTemplate.WIDGET_MOST_POPULAR_THREADS)){
				posts = WidgetService.getInstance().getMostPopularThreads(10);
				title = "Most Popular Threads";
				return "postsMostPopular";
			}
			else if(action.equals("refresh")){
				Date date = new Date(timestamp);
				//if(TrafficCache.getInstance().isUpdated(date)){
					traffic = TrafficCache.getInstance().getPeople();
				//}
				timestamp = new Date().getTime();
				return "traffic-json";
			}
			else if (action.equals("traffic-person")){
				Person user = UserService.getInstance().loadPerson(guid);
				traffic = new ArrayList<Person>();
				traffic.add(user);
				return "traffic-person";
			}
			
			else if(action.equals("checkstatus")){
				latestPostId = CommentService.getInstance().getLatestPostId();
				latestTagAssociation = CommentService.getInstance().getLatestTagAssociation(); 
				return "status-json";
			}
			
			else{
				return INPUT;
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			return INPUT;
		}
			
	}
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<Person> getTraffic() {
		return traffic;
	}

	public void setTraffic(List<Person> traffic) {
		this.traffic = traffic;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public boolean isNewcontent() {
		return newcontent;
	}

	public void setNewcontent(boolean newcontent) {
		this.newcontent = newcontent;
	}
	public String getLatestPostId() {
		return latestPostId;
	}

	public void setLatestPostId(String latestPostId) {
		this.latestPostId = latestPostId;
	}

	public List<PersonsPost> getMovieData() {
		return movieData;
	}

	public void setMovieData(List<PersonsPost> movieData) {
		this.movieData = movieData;
	}

	public AssociationPostTag getLatestTagAssociation() {
		return latestTagAssociation;
	}

	public void setLatestTagAssociation(AssociationPostTag latestTagAssociation) {
		this.latestTagAssociation = latestTagAssociation;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isShowUser() {
		return showUser;
	}

	public void setShowUser(boolean showUser) {
		this.showUser = showUser;
	}

	
}
