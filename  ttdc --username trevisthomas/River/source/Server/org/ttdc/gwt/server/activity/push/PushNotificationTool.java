package org.ttdc.gwt.server.activity.push;

import static org.ttdc.persistence.Persistence.session;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.server.activity.BroadcastEventJob;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.util.ApplicationProperties;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;

//application.properties
//PUSH_CERT=<path to p12 cert>
//PUSH_PASSWORD=<password of cert>
//PUSH_ENVIRONMENT=<prod or dev>

public class PushNotificationTool {

	enum Environment {
		PROD, DEV
	}

	private final static Logger log = Logger.getLogger(PushNotificationTool.class);

	private Environment env = null;
	private String password = null;
	private String certPath;

	public PushNotificationTool() {
		try {
			certPath = ApplicationProperties.getAppProperties().getProperty("PUSH_CERT");

			String pushEnv = ApplicationProperties.getAppProperties().getProperty("PUSH_ENVIRONMENT");
			if ("prod".equals(pushEnv)) {
				env = Environment.PROD;
			} else if ("dev".equals(pushEnv)) {
				env = Environment.DEV;
			} else {
				env = null;
			}

			password = ApplicationProperties.getAppProperties().getProperty("PUSH_PASSWORD");
			log.debug("Push notification tool initalized!");
		} catch (IOException e) {
			log.debug("Push notification tool failed to initalize!");
			log.error(e);
		}

	}

	public boolean isNotInitialized() {
		return certPath == null || env == null || StringUtil.empty(password);
	}

	private void pushIt(Set<String> deviceTokens, String title, String message, int badge) {

		if (isNotInitialized()) {
			log.warn("Push notifications not sent because PushNotificationTool died.");
			return;
		}

		try {
			URL certUrl = getClass().getClassLoader().getResource(certPath);
			ApnsServiceBuilder serviceBuilder = APNS.newService();
			switch (env) {
			case PROD: {
				log.debug("Using Prod push API");
				serviceBuilder.withCert(certUrl.openStream(), password).withProductionDestination();
				break;
			}
			case DEV: {
				log.debug("Using Dev push API");
				serviceBuilder.withCert(certUrl.openStream(), password).withSandboxDestination();
				break;
			}
			default: {
				throw new RuntimeException("Invalid environment");
			}
			}

			ApnsService service = serviceBuilder.build();

			// Payload with custom fields

			String payload;
			if (!StringUtil.empty(message)) {
				payload = APNS.newPayload().alertBody(message).alertTitle(title).sound("default").badge(badge).build();
			} else {
				payload = APNS.newPayload().badge(0).build();
			}

			// //Payload with custom fields
			// String payload = APNS.newPayload()
			// .alertBody(message).build();

			// //String payload example:
			// String payload =
			// "{\"aps\":{\"alert\":{\"title\":\"My Title 1\",\"body\":\"My message 1\",\"category\":\"Personal\"}}}";


			for (String deviceToken : deviceTokens) {
				if (StringUtil.empty(deviceToken)) {
					removeBadDeviceToken(deviceToken);
					continue;
				}

				log.debug("Push: " + payload + " to " + deviceToken);
				try {
					service.push(deviceToken, payload);
				} catch (Throwable t) {
					log.error("Failed to push to deviceToken: " + deviceToken, t);
					
					removeBadDeviceToken(deviceToken);
				}
			}

		} catch (Throwable t) {
			// env = null; // Disable?
			log.error("Unrecoverable push notification error.", t);
		}
	}

	public void executePushEventCausedBy(String personId, PostEvent event) {
		// Get all of the device tokens
		List<UserObject> list = getDeviceTokenUserObjects();
		// Set<String> deviceTokens = new HashSet<String>();

		Map<String, Set<String>> deviceTokenMap = new HashMap<String, Set<String>>();
		// Send push notifications to everyone who did not cause the push
		for (UserObject uo : list) {
			if (uo.getOwner().getPersonId().equals(personId)) {
				continue;
			}

			if (!BroadcastEventJob.applyFilterForPersonId(uo.getOwner().getPersonId(), event.getSource())) {
				continue;
			}

			Set<String> deviceTokens = deviceTokenMap.get(uo.getOwner().getPersonId());
			if (deviceTokens == null) {
				deviceTokens = new HashSet<String>();
				deviceTokenMap.put(uo.getOwner().getPersonId(), deviceTokens);
			}

			if (PostEventType.NEW.equals(event.getType())) {
				deviceTokens.add(uo.getValue());
			}
		}

		if (!deviceTokenMap.isEmpty()) {
			for (Map.Entry<String, Set<String>> entry : deviceTokenMap.entrySet()) {
				sendPushNotificationPostAdded(entry.getValue(), event.getSource(), entry.getKey());
			}
		}
	}

	public void pushBadgeToZero(String personId) {
		Person p = PersonDao.loadPerson(personId);
		Set<String> deviceTokens = new HashSet<String>(p.getDeviceTokenIds());
		if (deviceTokens.size() > 0) {
			pushIt(deviceTokens, null, null, 0);
		}
	}


	private int getBadgeCount(String personId) {
		Long count = 0L;
		try {
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("object.postsSinceLastAccessDate").setString("personId", personId);
			// @SuppressWarnings("unchecked")
			count = (Long) query.uniqueResult();
		} catch (RuntimeException e) {
			log.error(e);
		} finally {
			Persistence.commit();
		}
		return count.intValue();
	}

	private void sendPushNotificationPostAdded(Set<String> deviceTokens, GPost gPost, String personId) {
		StringBuilder builder = new StringBuilder();
		builder.append(gPost.getCreator().getLogin()).append(":").append(gPost.getLatestEntry().getSummary());
		pushIt(deviceTokens, gPost.getTitle(), builder.toString(), getBadgeCount(personId));
	}

	@SuppressWarnings("unchecked")
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

	private void removeBadDeviceToken(String deviceToken) {
		List<UserObject> list = new ArrayList<UserObject>();
		try {

			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("object.getAllOfType").setString("type",
					UserObject.TYPE_DEVICE_TOKEN_FOR_PUSH_NOTIFICATION);
			list.addAll(query.list());

			for (UserObject uo : list) {
				if (deviceToken.equals(uo.getValue())
						&& UserObject.TYPE_DEVICE_TOKEN_FOR_PUSH_NOTIFICATION.equals(uo.getType())) {
					Person owner = uo.getOwner();
					session().delete(uo);
					session().flush();
					session().refresh(owner);
					log.debug("Removing device token that failed to push to apple: " + deviceToken);
					break;
				}
			}

			Persistence.commit();

		} catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			// throw new ServiceException(t);

		}
	}
}
