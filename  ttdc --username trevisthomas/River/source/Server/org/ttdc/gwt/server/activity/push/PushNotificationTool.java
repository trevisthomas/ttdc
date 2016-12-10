package org.ttdc.gwt.server.activity.push;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.UserObject;

public class PushNotificationTool {
	private final static Logger log = Logger.getLogger(PushNotificationTool.class);

	public void executePushEventCausedBy(String personId, PostEvent event) {
		// Get all of the device tokens
		List<UserObject> list = getDeviceTokenUserObjects();

		// Send push notifications to everyone who did not cause the push
		for (UserObject uo : list) {
			if (uo.getOwner().getPersonId().equals(personId)) {
				continue;
			}
			if (PostEventType.NEW.equals(event.getType())) {
				sendPushNotificationPostAdded(uo.getValue(), event.getSource());
			}
		}

	}

	private void sendPushNotificationPostAdded(String deviceToken, GPost gPost) {
		System.out.println("MOCK! Sending new post " + gPost.getPostId() + "to device token: " + deviceToken);

	}

	private List<UserObject> getDeviceTokenUserObjects() {
		List<UserObject> list = new ArrayList<UserObject>();
		try {

			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("object.getAllOfType").setString("type",
					UserObject.TYPE_DEVICE_TOKEN_FOR_PUSH_NOTIFICATION);
			list.addAll(query.list());

			Persistence.commit();
			return list;
		} catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			// throw new ServiceException(t);
			return list;
		}

	}
}
