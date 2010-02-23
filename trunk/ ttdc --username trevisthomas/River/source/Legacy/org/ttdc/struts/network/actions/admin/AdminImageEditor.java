package org.ttdc.struts.network.actions.admin;

import java.io.File;
import java.util.List;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.ImageService;
import org.ttdc.biz.network.services.helpers.Paginator;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Person;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Results({
	@Result( name="success", value="tiles.adminImageEditor", type=TilesResult.class)
})
public class AdminImageEditor extends ActionSupport implements SecurityAware {
	private Person person;
	private String action = "";
	
	private List<Image> images;
	private File upload;// The actual file
	private String uploadContentType; // The content type of the file
	private String uploadFileName; // The uploaded file name
	
	private String imageUrl;
	private String name;
	private String rename;
	private String imageId;
	private int page;
	private Paginator<Image> paginator;
		
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		try{
			if(action.equals("page")){
				images = ImageService.getInstance().getPaginatedPage(page);
				paginator = Paginator.getActivePaginator();
			}else if(action.equals("create")){
				if(imageUrl != null && imageUrl.trim().length() > 0)
					ImageService.getInstance().createImageAndSave(person, imageUrl, name);
				else{
					if(name != null && name.trim().length() > 0)
						ImageService.getInstance().createImageAndSave(person, upload, name);
					else
						ImageService.getInstance().createImageAndSave(person, upload, uploadFileName);
				}
				loadImages();
			}
			else if(action.equals("delete")){
				ImageService.getInstance().deleteImage(imageId);
				loadImages();
			}
			else if(action.equals("update")){
				ImageService.getInstance().renameImage(imageId,rename);
				loadImages();
			}
			else{
				loadImages();
			}
			return SUCCESS;
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			loadImages();
			return SUCCESS;
		}
		
	}
	
	private void loadImages(){
		try{
			images = ImageService.getInstance().getAllImages(person);
			paginator = Paginator.getActivePaginator();
			page = 1;
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
		}
		catch(Throwable t){
			addActionError(t.getMessage());
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

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Paginator<Image> getPaginator() {
		return paginator;
	}

	public void setPaginator(Paginator<Image> paginator) {
		this.paginator = paginator;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getRename() {
		return rename;
	}

	public void setRename(String rename) {
		this.rename = rename;
	}
	
	
 

}
