package org.ttdc.gwt.client.presenters.users;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;
import org.ttdc.gwt.client.presenters.util.CookieTool;
import org.ttdc.gwt.shared.util.StringUtil;


import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

@Deprecated
public class UserToolsPresenter extends BasePagePresenter<UserToolsPresenter.View>{
	public interface View extends BasePageView{
		void displayCreateAccountTab();
		void displayLoginTab();
		void displayRequestPasswordResetTab();
		HasWidgets createAccountPanel();
		HasWidgets loginPanel();
		HasWidgets requestPasswordResetPanel();
	}
	
	@Inject
	public UserToolsPresenter(Injector injector) {
		super(injector,injector.getUserToolsView());
	}

	@Override
	public void show(HistoryToken token) {
		LoginPresenter loginPresenter = injector.getLoginPresenter();
		view.loginPanel().add(loginPresenter.getWidget());
		
		CreateAccountPresenter createAccountPresenter = injector.getCreateAccountPresenter();
		view.createAccountPanel().add(createAccountPresenter.getWidget());
		createAccountPresenter.init();
		
		RequestPasswordResetPresenter requestPassowrdResetPresenter = injector.getRequestPasswordPresenter();
		view.requestPasswordResetPanel().add(requestPassowrdResetPresenter.getWidget());
		requestPassowrdResetPresenter.init();
				
		String tab = token.getParameter(HistoryConstants.TAB_KEY);
		
		CookieTool.clear(); //I added this here after removing it from the LoginPresenter
		
		if(StringUtil.notEmpty(tab)){
			if(HistoryConstants.USER_LOGIN_TAB.equals(tab)){
				view.displayLoginTab();
			}
			else if(HistoryConstants.USER_CREATE_ACCOUNT_TAB.equals(tab)){
				view.displayCreateAccountTab();
			}
			else if(HistoryConstants.USER_REQUEST_PASSWORD_RESET_TAB.equals(tab)){
				view.displayRequestPasswordResetTab();
			}
			else{
				view.displayLoginTab();
			}
		}
		else{
			view.displayLoginTab();
		}
		
		view.show();	
	}

}
