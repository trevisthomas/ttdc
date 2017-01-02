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
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.server.activity.BroadcastEventJob;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.util.ApplicationProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	public final static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

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

	// private void pushIt(Set<String> deviceTokens, String title, String message, int badge) {
	// String payload;
	// if (!StringUtil.empty(message)) {
	// payload = APNS.newPayload().alertBody(message).alertTitle(title).sound("default").badge(badge).build();
	// } else {
	// payload = APNS.newPayload().badge(0).build();
	// }
	// pushIt(deviceTokens, payload);
	// }

	private void pushIt(Set<String> deviceTokens, String payload) {

		if (isNotInitialized()) {
			log.warn("Push notifications not sent because PushNotificationTool died.");
			return;
		}

		if (StringUtil.empty(payload)) {
			log.warn("Failed to push empty payload.");
			return;
		}

		if (deviceTokens.isEmpty()) {
			log.warn("Failed to push. DeviceTokens list is empty.");
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

	public void executePushEventCausedBy(String sourceDeviceToken, PostEvent event) {
		// Get all of the device tokens
		List<UserObject> list = getDeviceTokenUserObjects();
		// Set<String> deviceTokens = new HashSet<String>();

		Map<String, Set<String>> deviceTokenMap = new HashMap<String, Set<String>>();
		// Send push notifications to everyone except the DEVICE that caused it.
		for (UserObject uo : list) {
			// if (uo.getOwner().getPersonId().equals(personId)) {
			// continue;
			// }

			if (!BroadcastEventJob.applyFilterForPersonId(uo.getOwner().getPersonId(), event.getSource())) {
				continue;
			}

			String postPersonId = event.getSource().getCreator().getPersonId();
			String devicePersonId = uo.getOwner().getPersonId();

			if (PostEventType.NEW.equals(event.getType()) && postPersonId.equals(devicePersonId)) {
				continue; // I'm deliberately not sending this notification to the person who created the post. The idea
				// is that their badge count should stay zero, and they shouldn't be notified at all. If
				// they are not in the app, when the app launches they will check to see if there are newer
				// posts and find it. If not, then the app was already running and they dont need a push to
				// get it added.
			}

			Set<String> deviceTokens = deviceTokenMap.get(uo.getOwner().getPersonId());
			if (deviceTokens == null) {
				deviceTokens = new HashSet<String>();
				deviceTokenMap.put(uo.getOwner().getPersonId(), deviceTokens);
			}

			String deviceToken = uo.getValue();
			if (deviceToken.equals(sourceDeviceToken)) {
				continue;
			}

			deviceTokens.add(uo.getValue());
		}

		if (!deviceTokenMap.isEmpty()) {
			for (Map.Entry<String, Set<String>> entry : deviceTokenMap.entrySet()) {
				if (PostEventType.NEW.equals(event.getType())) {
					sendPushNotificationPostAdded(entry.getValue(), event.getSource(), entry.getKey());
				} else if (PostEventType.EDIT.equals(event.getType()) || PostEventType.DELETE.equals(event.getType())) {
					sendPushNotificationGenericServerEvent(entry.getValue(), event.getSource().getPostId(), event
							.getType()
							.name());
				}
			}
		}
	}

	public void executePushEventCausedBy(String sourceDeviceToken, PersonEvent event) {

		if (!PersonEventType.TRAFFIC.equals(event.getType())) {
			log.debug("Non traffic person event ignored.");
			return;
		}

		// Get all of the device tokens
		List<UserObject> list = getDeviceTokenUserObjects();
		// Set<String> deviceTokens = new HashSet<String>();

		Map<String, Set<String>> deviceTokenMap = new HashMap<String, Set<String>>();
		// Send push notifications to everyone except the DEVICE that caused it.
		for (UserObject uo : list) {

			Set<String> deviceTokens = deviceTokenMap.get(uo.getOwner().getPersonId());
			if (deviceTokens == null) {
				deviceTokens = new HashSet<String>();
				deviceTokenMap.put(uo.getOwner().getPersonId(), deviceTokens);
			}

			String deviceToken = uo.getValue();
			if (deviceToken.equals(sourceDeviceToken)) {
				continue;
			}

			deviceTokens.add(uo.getValue());
		}

		if (!deviceTokenMap.isEmpty()) {
			for (Map.Entry<String, Set<String>> entry : deviceTokenMap.entrySet()) {
				// sendPushNotificationTraffic(entry.getValue(), event.getSource(), entry.getKey());
				sendPushNotificationGenericServerEvent(entry.getValue(), event.getSource().getPersonId(), event
						.getType().name());

				// if (PostEventType.NEW.equals(event.getType())) {
				// sendPushNotificationPostAdded(entry.getValue(), event.getSource(), entry.getKey());
				// } else if (PostEventType.EDIT.equals(event.getType()) ||
				// PostEventType.DELETE.equals(event.getType())) {
				// sendPushNotificationPostChanged(entry.getValue(), event.getSource().getPostId(), event.getType()
				// .name());
				// }
			}
		}
	}

	// public void executePushEventCausedBy(String personId, PostEvent event) {
	// // Get all of the device tokens
	// List<UserObject> list = getDeviceTokenUserObjects();
	// // Set<String> deviceTokens = new HashSet<String>();
	//
	// Map<String, Set<String>> deviceTokenMap = new HashMap<String, Set<String>>();
	// // Send push notifications to everyone who did not cause the push
	// for (UserObject uo : list) {
	// if (uo.getOwner().getPersonId().equals(personId)) {
	// continue;
	// }
	//
	// if (!BroadcastEventJob.applyFilterForPersonId(uo.getOwner().getPersonId(), event.getSource())) {
	// continue;
	// }
	//
	// Set<String> deviceTokens = deviceTokenMap.get(uo.getOwner().getPersonId());
	// if (deviceTokens == null) {
	// deviceTokens = new HashSet<String>();
	// deviceTokenMap.put(uo.getOwner().getPersonId(), deviceTokens);
	// }
	//
	// if (PostEventType.NEW.equals(event.getType())) {
	// deviceTokens.add(uo.getValue());
	// }
	// }
	//
	// if (!deviceTokenMap.isEmpty()) {
	// for (Map.Entry<String, Set<String>> entry : deviceTokenMap.entrySet()) {
	// sendPushNotificationPostAdded(entry.getValue(), event.getSource(), entry.getKey());
	// }
	// }
	// }

	public void pushBadgeToZero(String personId) {
		try {
			Persistence.beginSession();
			Person p = PersonDao.loadPerson(personId);
			Set<String> deviceTokens = new HashSet<String>(p.getDeviceTokenIds());
			if (deviceTokens.size() > 0) {
				String payload = APNS.newPayload().badge(0).build();
				pushIt(deviceTokens, payload);

			}
		} finally {
			Persistence.commit();
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

	private void sendPushNotificationGenericServerEvent(Set<String> deviceTokens, String guid, String type) {
		// PushServerEvent event = new PushServerEvent(type, gPost.getPostId());
		String payload;
		try {

			Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
			Map<String, Object> aps = new HashMap<String, Object>();
			aps.put("content-available", 1);

			Map<String, Object> event = new HashMap<String, Object>();
			event.put("type", type);
			event.put("guid", guid);

			map.put("aps", aps);
			map.put("event", event);

			payload = mapper.writeValueAsString(map);

			// String eventString = mapper.writeValueAsString(event);
			// payload = APNS.newPayload().forNewsstand().customField("type", eventString)
			// .customField("guid", gPost.getPostId()).build();
			
			pushIt(deviceTokens, payload);
		} catch (JsonProcessingException e) {
			log.error("Error when trying to PostChanged push notification", e);
		}
	}

	private void sendPushNotificationPostAdded(Set<String> deviceTokens, GPost gPost, String personId) {
		StringBuilder builder = new StringBuilder();
		builder.append(gPost.getCreator().getLogin()).append(":").append(gPost.getLatestEntry().getSummary());
		String message = builder.toString();
		String title = gPost.getTitle();
		int badge = getBadgeCount(personId);

		String payload = APNS.newPayload().alertBody(message).alertTitle(title).sound("default").badge(badge)
				.customField("postId", gPost.getPostId()).build();

		pushIt(deviceTokens, payload);

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
