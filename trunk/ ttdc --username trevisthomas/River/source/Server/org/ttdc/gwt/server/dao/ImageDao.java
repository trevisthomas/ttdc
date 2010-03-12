package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Image;

public class ImageDao extends PaginatedDaoBase{
	private String name;
	private String imageId;
	
	public ImageDao(){}
	
	public static Image loadImage(String imageId) {
		Image image = (Image)session().load(Image.class, imageId);
		return image;
	}

	public Image load(){
		Image image = null; 
		if(StringUtils.isNotBlank(name)){
			image = loadImageByName();
		}
		else if(StringUtils.isNotBlank(imageId)){
			image = loadImageById();
		}
		else{
			throw new RuntimeException("ImageDao is very confused. Cant load with no image specificiation.");
		}
		return image;
	}

	private Image loadImageById() {
		Image image;
		Query query = session().getNamedQuery("image.getById").setString("imageId", imageId);
		image = (Image) query.uniqueResult();
		return image;
	}

	private Image loadImageByName() {
		Image image;
		String cleanName = name.replaceFirst(Image.SQUARE_THUMBNAIL_SUFFIX, "");
		Query query = session().getNamedQuery("image.getByName").setString("name", cleanName);
		image = (Image) query.uniqueResult();
		return image;
	}
	
	@SuppressWarnings("unchecked")
	public PaginatedList<Image> loadAll(){
		PaginatedList<Image> results = null;
		
		int count = session().getNamedQuery("image.getAllImages").list().size();
		
		List<Image> list = session().getNamedQuery("image.getAllImages")
			.setFirstResult(calculatePageStartIndex())
			.setMaxResults(getPageSize()).list();
		 
		results = DaoUtils.createResults(this, list, count);
		return results;
	}
	
	public Image rename(String name){
		Image image = load();
		if(image == null){
			throw new RuntimeException("Couldn't find that image");
		}
		if(name.length() < 2){
			throw new RuntimeException("Try again, that name is garbage.");
		}
		
		failIfImageNameExists(name);
		
		image.setName(name);
		session().update(image);
		return image;
	}

	private void failIfImageNameExists(String name) {
		ImageDao dao2 = new ImageDao();
		dao2.setName(name);
		Image image2 = dao2.load();
		if(image2 != null){
			throw new RuntimeException("Image "+name+" already exists.");
		}
	}
	
	public void delete(){
		Image image = load();
		if(image == null){
			throw new RuntimeException("Couldn't find that image");
		}
		//Trevis, this is the same old warning... what happens if the image is associated with something?
		session().delete(image);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	
	
}
