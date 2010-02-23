package org.ttdc.gwt.client.presenters.admin;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class AdminToolsPresenter extends BasePagePresenter<AdminToolsPresenter.View>{
	public interface View extends BasePageView{
		void displayImageManagementTab();
		void displayUserAdminTab();
		void displayTempalateAdminTab();
		void displayStyleManagementTab();
		
		HasWidgets imagePanel();
		HasWidgets usersPanel();
		HasWidgets headerPanel(); //TODO: i'm thinking that this should probably come from the BasePagePresenter...
		HasWidgets templatePanel();
		HasWidgets stylePanel();
		
	}
	
	@Inject
	public AdminToolsPresenter(Injector injector) {
		super(injector,injector.getAdminToolsView());
		view.headerPanel().add(injector.getUserIdentityPresenter().getWidget());
	}

	@Override
	public void show(HistoryToken token) {
		ImageManagementPresenter imageMgtPresenter = injector.getImageManaementPresenter();
		view.imagePanel().add(imageMgtPresenter.getWidget());
		
		UserAdministrationPresenter userAdminPresenter = injector.getUserAdministrationPresenter();
		view.usersPanel().add(userAdminPresenter.getWidget());
		
		UserObjectTemplateEditorPresenter userTemplateEditorPresenter = injector.getUserObjectTemplateEditorPresenter();
		view.templatePanel().add(userTemplateEditorPresenter.getWidget());
		
		StyleManagementPresenter styleMgtPresenter = injector.getStyleManagementPresenter();
		view.stylePanel().add(styleMgtPresenter.getWidget());
		
		String tab = token.getParameter(HistoryConstants.TAB_KEY);
		if(StringUtil.notEmpty(tab)){
			if(HistoryConstants.ADMIN_IMAGE_TAB.equals(tab)){
				view.displayImageManagementTab();
				imageMgtPresenter.init(token);
			}
			else if(HistoryConstants.ADMIN_USER_TAB.equals(tab)){
				view.displayUserAdminTab();
				userAdminPresenter.init(token);
			}
			else if(HistoryConstants.ADMIN_USER_OBJECT_TEMPLATE_TAB.equals(tab)){
				view.displayTempalateAdminTab();
				userTemplateEditorPresenter.init(token);
			}
			else if(HistoryConstants.ADMIN_STYLE_TAB.equals(tab)){
				view.displayStyleManagementTab();
				styleMgtPresenter.init(token);
			}
			else{
				view.displayImageManagementTab();
			}
		}
		else{
			view.displayImageManagementTab();
			imageMgtPresenter.init(token);
		}
		
		
		view.show();
	}
	
}
