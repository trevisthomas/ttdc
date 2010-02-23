package org.ttdc.persistence.objects;

/**
 * This class was created to allow me to query tags with the aggrogate values in tact.  I could not figure out how to
 * perform this in straight hibernate so i did native sql. But i couldnt map to the real Tag entity object because 
 * it has attributes that are not in the group by clause so... i did this frankenstein
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;

import org.ttdc.util.Median;
import org.ttdc.util.Median.SourceValueReader;

@Entity
@NamedQueries({
	
	@NamedQuery(name="tagLiteHql.getForThread", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
				"FROM AssociationPostTag ass INNER JOIN ass.tag WHERE ass.post.root.postId=:rootId "+
				"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
	@NamedQuery(name="tagLiteHql.TagMassForSpider", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type IN ('TOPIC','CREATOR','DATE_MONTH','DATE_YEAR') " +
			"AND ass.tag.tagId NOT IN (:tagIds) " +
			"AND ass.post.postId IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
			 "WHERE ass.tag.tagId IN (:tagIds) GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
	@NamedQuery(name="tagLiteHql.TagMassForSpiderOthers", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type IN ('CREATOR','DATE_MONTH','DATE_YEAR') " +
			"AND ass.tag.tagId NOT IN (:tagIds) " +
			"AND ass.post.postId IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
			 "WHERE ass.tag.tagId IN (:tagIds) GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
	@NamedQuery(name="tagLiteHql.TagMassForSpiderTopic", query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type = 'TOPIC' " +
			"AND ass.tag.tagId NOT IN (:tagIds) " +
			"AND ass.post.postId IN ( SELECT ass.post.postId FROM AssociationPostTag ass " +
			 "WHERE ass.tag.tagId IN (:tagIds) GROUP BY ass.post.postId HAVING count(ass.post.postId) = :count)" +
			"GROUP BY ass.tag.tagId, ass.tag.type, ass.tag.value"),
	@NamedQuery(name="tagLiteHql.TagsForAutocomplete",query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type = 'TITLE' AND ass.tag.value like :value " +
			"GROUP BY ass.tag.tagId,ass.tag.value,ass.tag.type ORDER BY count(ass.tag.tagId) desc"), 
			
	@NamedQuery(name="tagLiteHql.TagMass",query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.type<>'TITLE' AND ass.tag.value like :value " +
			"GROUP BY ass.tag.tagId,ass.tag.value,ass.tag.type ORDER BY count(ass.tag.tagId) desc"),
	@NamedQuery(name="tagLiteHql.TagMassSubset",query="SELECT count(ass.tag.tagId) as count, ass.tag.tagId, ass.tag.type, ass.tag.value " +
			"FROM AssociationPostTag ass INNER JOIN ass.tag " +
			"WHERE ass.tag.tagId IN (:tagIds)" +
			"GROUP BY ass.tag.tagId,ass.tag.value,ass.tag.type ORDER BY count(ass.tag.tagId) desc")
})

@NamedNativeQueries({
@NamedNativeQuery(name="tagLite.TagsForAutocomplete", query="select count(ass.tag_guid) as count,ass.tag_guid as guid,t.value,t.type from association_post_tag ass inner join tag t on ass.tag_guid=t.guid" +
		" where t.type=:type AND t.value like :value group by ass.tag_guid,t.value,t.type order by count desc", resultSetMapping="tagLiteMapping"),

//tagLite.TagsForAutocompleteAll queries for all tags.  I limit the result set from this query to have a default list to show
@NamedNativeQuery(name="tagLite.TagsForAutocompleteAll", query="select count(ass.tag_guid) as count,ass.tag_guid as guid,t.value,t.type " +
				"from association_post_tag ass inner join tag t on ass.tag_guid=t.guid " +
				"where t.type=:type group by ass.tag_guid,t.value,t.type order by count desc", resultSetMapping="tagLiteMapping"),		
				

@NamedNativeQuery(name="tagLite.getForThread", query="select count(ass.tag_guid) as count,ass.tag_guid as guid,t.value,t.type " +
		"from association_post_tag ass inner join tag t on ass.tag_guid=t.guid " +
		"inner join post p on ass.post_guid=p.guid "+
		"where p.root_guid =:rootId group by ass.tag_guid,t.value,t.type order by count desc", resultSetMapping="tagLiteMapping"),

@NamedNativeQuery(name="tagLite.TagMass", query="select count(ass.tag_guid) as count,ass.tag_guid as guid,t.value,t.type from association_post_tag ass inner join tag t on ass.tag_guid=t.guid " +
				"where t.type='TOPIC' group by ass.tag_guid,t.value,t.type order by count desc", resultSetMapping="tagLiteMapping"),

@NamedNativeQuery(name="tagLite.TagMassSubset", query="select count(ass.tag_guid) as count,ass.tag_guid as guid,t.value,t.type from association_post_tag ass inner join tag t on ass.tag_guid=t.guid " +
		"where ass.tag_guid in (:tagIds) group by ass.tag_guid,t.value,t.type order by count desc", resultSetMapping="tagLiteMapping"),
		
				
@NamedNativeQuery(name="tagLite.TagMassForSpider", query=
			"select count(ass.tag_guid) as count,ass.tag_guid as guid,t.value,t.type from association_post_tag ass" +
			" inner join tag t on ass.tag_guid=t.guid " +
			"where (t.type='TOPIC' or t.type='CREATOR' or t.type='DATE_MONTH' or t.type='DATE_YEAR')  AND ass.post_guid " +
			"	in ( select ass.post_guid from association_post_tag ass" +
			"	where ass.tag_guid in (:tagIds)" +
			"	group by ass.post_guid" +
			"	having count(ass.post_guid) = :count) " +
			" AND ass.tag_guid not in (:tagIds) " +
			" group by ass.tag_guid,t.value,t.type order by t.value", resultSetMapping="tagLiteMapping"),		
		
@NamedNativeQuery(name="tagLite.TagMassForSpiderTopic", query=
		"select count(ass.tag_guid) as count,ass.tag_guid as guid,t.value,t.type from association_post_tag ass inner join tag t on ass.tag_guid=t.guid " +
		"where (t.type='TOPIC')  AND ass.post_guid " +
		"	in ( select ass.post_guid from association_post_tag ass" +
		"	where ass.tag_guid in (:tagIds)" +
		"	group by ass.post_guid" +
		"	having count(ass.post_guid) = :count) " +
		" AND ass.tag_guid not in (:tagIds) " +
		" group by ass.tag_guid,t.value,t.type order by count desc", resultSetMapping="tagLiteMapping"),
		
@NamedNativeQuery(name="tagLite.TagMassForSpiderTopicThread", query=
			"select count(ass.tag_guid) as count,ass.tag_guid as guid,t.value,t.type from association_post_tag ass inner join tag t on ass.tag_guid=t.guid " +
			"where (t.type='TOPIC') " +
			"AND ass.post_guid IN ( select post.guid from post where post.root_guid = :threadId ) " +
			"AND ass.post_guid " +
			"	in ( select ass.post_guid from association_post_tag ass" +
			"	where ass.tag_guid in (:tagIds)" +
			"	group by ass.post_guid" +
			"	having count(ass.post_guid) = :count) " +
			" AND ass.tag_guid not in (:tagIds)" +
			" group by ass.tag_guid,t.value,t.type order by count desc", resultSetMapping="tagLiteMapping"),
		
@NamedNativeQuery(name="tagLite.TagMassForSpiderOthers", query=
				"select count(ass.tag_guid) as count,ass.tag_guid as guid,t.value,t.type from association_post_tag ass inner join tag t on ass.tag_guid=t.guid " +
				"where (t.type <> 'TOPIC')  AND ass.post_guid " +
				"	in ( select ass.post_guid from association_post_tag ass" +
				"	where ass.tag_guid in (:tagIds)" +
				"	group by ass.post_guid" +
				"	having count(ass.post_guid) = :count) " +
				" AND ass.tag_guid not in (:tagIds) " +
				" group by ass.tag_guid,t.value,t.type order by t.value", resultSetMapping="tagLiteMapping"),
				
@NamedNativeQuery(name="tagLite.TagMassForSpiderOthersThread", query=
			"select count(ass.tag_guid) as count,ass.tag_guid as guid,t.value,t.type from association_post_tag ass inner join tag t on ass.tag_guid=t.guid " +
			"where (t.type <> 'TOPIC') " +
			"AND ass.post_guid IN ( select post.guid from post where post.root_guid = :threadId ) " +
			"AND ass.post_guid " +
			"	in ( select ass.post_guid from association_post_tag ass" +
			"	where ass.tag_guid in (:tagIds)" +
			"	group by ass.post_guid" +
			"	having count(ass.post_guid) = :count) " +
			" AND ass.tag_guid not in (:tagIds) " +
			" group by ass.tag_guid,t.value,t.type order by t.value", resultSetMapping="tagLiteMapping")
})
@SqlResultSetMapping(name="tagLiteMapping", entities=
		@EntityResult(entityClass=org.ttdc.persistence.objects.TagLite.class, fields = {
	        @FieldResult(name="tagId", column="guid"),
	        @FieldResult(name="type", column="type"),
	        @FieldResult(name="value", column="value"),
	        @FieldResult(name="count", column="count")
	    }))

public class TagLite implements Comparable<TagLite>{
	private String tagId;
	private String type; 
	private String value;
	private int count;
	private String mass;
	private int size; //I added this to quickly get the cloud tag thing presentable
	
	public final static String PERCENTILE_4 = "PERCENTILE_4";
	public final static String PERCENTILE_3 = "PERCENTILE_3";
	public final static String PERCENTILE_2 = "PERCENTILE_2";
	public final static String PERCENTILE_1 = "PERCENTILE_1";
	
	public final static String PERCENTILE_8 = "PERCENTILE_8";
	public final static String PERCENTILE_7 = "PERCENTILE_7";
	public final static String PERCENTILE_6 = "PERCENTILE_6";
	public final static String PERCENTILE_5 = "PERCENTILE_5";
	
	private final static List<String> months = new ArrayList<String>();
	static{
		months.add("January");
		months.add("February");
		months.add("March");
		months.add("April");
		months.add("May");
		months.add("June");
		months.add("July");
		months.add("August");
		months.add("September");
		months.add("October");
		months.add("November");
		months.add("December");
	}
	
	
	/**
	 * Sorts a list of LiteTags by the Month name
	 * @author Trevis
	 *
	 */
	public static class MonthComparator implements Comparator<TagLite>{
		
		public int compare(TagLite o1, TagLite o2) {
			Integer i1 = months.indexOf(o1.getValue());
			Integer i2 = months.indexOf(o2.getValue());
			return i1.compareTo(i2);
		}
	}
	
	/**
	 * 
	 * @param list Takes a list of display tags.  This assigns a mass percentile value 
	 * this is used for showing the tags sized in the tag browser.
	 */
	
	private static class TagLiteMedianValueReader implements SourceValueReader<TagLite>{
		public long readSourceValue(TagLite target) {
			return target.getCount();
		}
		
	}
	private static void assignMass(List<TagLite> list, String value){
		for(TagLite tl : list){
			tl.setMass(value);
			if(PERCENTILE_1.equals(value)){
				tl.setSize(22);
			}
			if(PERCENTILE_2.equals(value)){
				tl.setSize(18);
			}
			if(PERCENTILE_3.equals(value)){
				tl.setSize(14);
			}
			if(PERCENTILE_4.equals(value)){
				tl.setSize(10);
			}
		}
	}
	public static void calculatePercentile(List<TagLite> list){
		
		Median<TagLite> medianCalculator = new Median<TagLite>();
		/*
		List<List<TagLite>> lists = medianCalculator.medianDistribution(3, list, new TagLiteMedianValueReader() );
		
		List<TagLite> temp;
		temp = lists.get(7);
		assignMass(temp,PERCENTILE_1);
		temp = lists.get(6);
		assignMass(temp,PERCENTILE_2);
		temp = lists.get(5);
		assignMass(temp,PERCENTILE_3);
		temp = lists.get(4);
		assignMass(temp,PERCENTILE_4);
		temp = lists.get(3);
		assignMass(temp,PERCENTILE_5);
		temp = lists.get(2);
		assignMass(temp,PERCENTILE_6);
		temp = lists.get(1);
		assignMass(temp,PERCENTILE_7);
		temp = lists.get(0);
		assignMass(temp,PERCENTILE_8);
		*/
		List<List<TagLite>> lists = medianCalculator.medianDistribution(2, list, new TagLiteMedianValueReader() );
		
		List<TagLite> temp;
		temp = lists.get(3);
		assignMass(temp,PERCENTILE_1);
		temp = lists.get(2);
		assignMass(temp,PERCENTILE_2);
		temp = lists.get(1);
		assignMass(temp,PERCENTILE_3);
		temp = lists.get(0);
		assignMass(temp,PERCENTILE_4);
		
		Collections.sort(list);
	}
	
	@Override
	public boolean equals(Object that) {
		if(that == null) return false;
		return this.tagId.equals(((TagLite)that).tagId);
	}
	
	@Override
	public int hashCode() {
		return this.getTagId().hashCode();
	}
	
	@Id
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Transient
	public String getMass() {
		return mass;
	}
	public void setMass(String mass) {
		this.mass = mass;
	}
	public int compareTo(TagLite that) {
		return this.value.compareTo(that.value);
	}
	@Transient
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public static List<String> getMonths() {
		return months;
	}
	
}
