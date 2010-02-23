package org.ttdc.struts.network.actions.movies;

import java.util.List;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.SearchService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.biz.network.services.helpers.Paginator;
import org.ttdc.biz.network.services.helpers.PostHelper;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Results({
	@Result( name="success", value="tiles.movies", type=TilesResult.class)
})
public class Movies extends ActionSupport implements SecurityAware {
	private Person person;
	private List<Post> posts;
	private String action = "";
	private Paginator<Post> paginator;
	private int page = 0;
	private String sort = SearchService.SORT_TITLE;
	private boolean reverse = false;
	private String personIdFilter = "";
	private List<Person> voters;
	private Person user; 
	private String title;
	
	public boolean isTitleSort(){
		return SearchService.SORT_TITLE.equals(sort);
	}
	public boolean isYearSort(){
		return SearchService.SORT_YEAR.equals(sort);
	}
	public boolean isRatingSort(){
		return SearchService.SORT_RATING.equals(sort);	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		try{
			voters = CommentService.getInstance().readMovieVoters();
			if(personIdFilter != null && !personIdFilter.equals("") ){
				user = UserService.getInstance().loadPerson(personIdFilter);
				if(action.equals("speedrate")){
					title = "Movies not rated by "+user.getLogin();
				}
				else{
					title = "Movies for "+user.getLogin();
				}
				
			}
			else{
				title="Movies";				
			}
			if(action.equals("")){
				posts = SearchService.getInstance().readMovies(person,sort,reverse,personIdFilter,true);
				paginator = Paginator.getActivePaginator();
				page = 1;
			}
			else if(action.equals("speedrate")){
				//If personID were empty this wouldnt work so... check that somewhere
				posts = SearchService.getInstance().readMovies(person,sort,reverse,personIdFilter,false);
				paginator = Paginator.getActivePaginator();
				page = 1;
			}
			else if(action.equals("page")){
				posts = PostHelper.getPaginatedPage(page);
				paginator = Paginator.getActivePaginator();
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
		}
		return SUCCESS;
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

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public Paginator<Post> getPaginator() {
		return paginator;
	}

	public void setPaginator(Paginator<Post> paginator) {
		this.paginator = paginator;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}
	public List<Person> getVoters() {
		return voters;
	}
	public void setVoters(List<Person> voters) {
		this.voters = voters;
	}
	public String getPersonIdFilter() {
		return personIdFilter;
	}
	public void setPersonIdFilter(String personIdFilter) {
		this.personIdFilter = personIdFilter;
	}
	public Person getUser() {
		return user;
	}
	public void setUser(Person user) {
		this.user = user;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

}
