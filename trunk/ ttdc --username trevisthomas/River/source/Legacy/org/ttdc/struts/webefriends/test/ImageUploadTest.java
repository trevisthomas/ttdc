package org.ttdc.struts.webefriends.test;

import java.io.File;

import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.biz.network.services.ImageService;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class ImageUploadTest extends ActionSupport {

	private File upload;// The actual file
	private String uploadContentType; // The content type of the file
	private String uploadFileName; // The uploaded file name
	private String fileCaption;// The caption of the file entered by user

	public String execute() throws Exception {
		Session session = Persistence.beginSession();
		Query query = session.getNamedQuery("person.getByGuid").setString("guid", "D379886C-8A0F-4BC4-AC24-99E495CCFEF0");
		Person p = (Person)query.uniqueResult();
		//ImageService.getInstance().writeImage(p, upload, uploadFileName);
		return SUCCESS;

	}

	public String getFileCaption() {
		return fileCaption;
	}

	public void setFileCaption(String fileCaption) {
		this.fileCaption = fileCaption;
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

}