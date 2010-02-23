package org.ttdc.gwt.server.nugets;

import org.ttdc.persistence.objects.Person;
import org.ttdc.util.BizException;
import org.ttdc.util.SendGmail;
import org.ttdc.util.ServiceException;
import org.ttdc.util.struts.WebHelper;

public class EmailHelper {
	public static void sendActivateionEmail(Person p) throws ServiceException{
		//Trevis, you need to figure out a way to not need to hard code this crap 
		String html = "<html><body><h2>Welcome to We Be Friends!</h2><p>Click the following link to activate your account. <a href=\""+WebHelper.getSiteName()+"/user/activate.action?key="+p.getPersonId()+"\">Activate!</a></p></body></html>";
		try {
			SendGmail.sendMail(html, SendGmail.ContentType.HTML, "Instructions to complete activation", "Trevis Thomas", p.getEmail());
		} catch (BizException e) {
			throw new ServiceException(e);
		}
	}
}
