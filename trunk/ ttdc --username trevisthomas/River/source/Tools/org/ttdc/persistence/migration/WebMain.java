package org.ttdc.persistence.migration;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;

import org.hibernate.annotations.BatchSize;

@Entity
@NamedQueries({
	@NamedQuery(name="webmain.getall", query="SELECT m FROM WebMain as m WHERE sectionid not in (9,7,4,8)")
})

@SecondaryTables({
    @SecondaryTable(name="webMovieReviews", 
    	pkJoinColumns=@PrimaryKeyJoinColumn(name="MainID", referencedColumnName="id")
    ),
    @SecondaryTable(name="webThreads", 
    	pkJoinColumns=@PrimaryKeyJoinColumn(name="MainID", referencedColumnName="id")
    )
})
public class WebMain {
	private int id;
	private int sectionId;
	private int userId;
	private String title;
	private Date dateAdded;
	private String entry;
	private Integer forumId;
	private String movieRating;//webMovieReviews
	private WebMovieTitles movieTitle;

	@Id
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSectionId() {
		return sectionId;
	}
	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	public String getEntry() {
		return entry;
	}
	public void setEntry(String entry) {
		this.entry = entry;
	}
	
	@Column(name="Rating", table="webMovieReviews")
	public String getMovieRating() {
		return movieRating;
	}
	public void setMovieRating(String movieRating) {
		this.movieRating = movieRating;
	}
	
	@ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="MovieTitleID")
	@BatchSize( size=100)
	public WebMovieTitles getMovieTitle() {
		return movieTitle;
	}
	public void setMovieTitle(WebMovieTitles movieTitle) {
		this.movieTitle = movieTitle;
	}
	
	@Column(name="ForumId", table="webThreads")
	@BatchSize( size=100)
	public Integer getForumId() {
		return forumId;
	}
	public void setForumId(Integer forumId) {
		this.forumId = forumId;
	}	
	
}
