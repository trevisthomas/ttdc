package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.util.StringTools;

public class TagDao {
	private final static Logger log = Logger.getLogger(TagDao.class);
	
	private String type;
	private String value;
	private String description;
	private Person creator;
	private Date date;
	
	private String orderBy = "mass";
	private boolean sortAscending = false; 
		
	public static Tag loadTag(String tagId){
		Tag tag = (Tag)session().load(Tag.class, tagId);
		return tag;
	}
	
	@SuppressWarnings("unchecked")
	public Tag load(){
		if(value == null || type == null)
			throw new RuntimeException("Value and Type are required to load a Tag");
		List<Tag> list = session().createCriteria(Tag.class)
		.add(Restrictions.eq("value", value.trim()))
		.add(Restrictions.eq("type", type.trim()))
		.list();

		Tag tag;
		if(list.size() == 0){
			tag = null;
		}
		else{
			tag = (Tag)list.get(0);
		}
		return tag;
	}
	
	/**
	 * Specialized loader which doesnt check value at all.  (We need to remove the value from creator tags)
	 * @return
	 */
	
//	@SuppressWarnings("unchecked")
//	private Tag loadCreatorTag(){
//		List<Tag> list = session().createCriteria(Tag.class)
//		.add(Restrictions.eq("creator.personId", creator.getPersonId()))
//		.add(Restrictions.eq("type", Tag.TYPE_CREATOR))
//		.list();
//		
//		Tag tag;
//		if(list.size() == 0){
//			tag = null;
//		}
//		else{
//			tag = (Tag)list.get(0);
//		}
//		
//		return tag;
//	}
	
	@SuppressWarnings("unchecked")
	public List<Tag> loadList(){
		Order order = null;
		if(sortAscending){
			order = Order.asc(orderBy);	
		}
		else{
			order = Order.desc(orderBy);	
		}
		
		List<Tag> list = session().createCriteria(Tag.class)
		.add(Restrictions.eq("type", type.trim()))
		.addOrder(order)
		.list();

		return list;
	}
	
	public static Tag loadCreatorTag(String personId){
		//tag.getCreatorTag
		return (Tag)session().createCriteria(Tag.class)
		.add(Restrictions.eq("creator.personId", personId))
		.add(Restrictions.eq("type", "CREATOR")).uniqueResult();
	}
	
	public Tag createOrLoad(){
		try{
			log.debug("Trying to create or load the requested tag");
			Tag tag;
			if(Tag.TYPE_CREATOR.equals(getType()))
				tag = loadCreatorTag(creator.getPersonId());
			else
				tag = load();
			if(tag == null){
				log.debug("Tag not found creating.");
				tag = create();
			}
			else{
				log.debug("Tag loaded.");
			}
			log.debug("Done");
			return tag;
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}
	
	public Tag create(){
		Tag tag = new Tag();
		validateForCreation();
		tag.setCreator(creator);
		tag.setType(type);
		tag.setValue(value);
		tag.setSortValue(StringTools.formatTitleForSort(value));
		if(date != null)
			tag.setDate(date);
		session().save(tag);
		session().flush();
		return tag;
	}

	private void validateForCreation() {
		if(creator == null || StringUtil.empty(type) || StringUtil.empty(value) || StringUtil.empty(value))
			throw new RuntimeException("Invalid parameters for creation.");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type.trim();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value.trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description.trim();
	}

	public Person getCreator() {
		return creator;
	}

	public void setCreator(Person creator) {
		this.creator = creator;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isSortAscending() {
		return sortAscending;
	}

	public void setSortAscending(boolean sortAscending) {
		this.sortAscending = sortAscending;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
