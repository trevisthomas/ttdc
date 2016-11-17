package org.ttdc.servlets;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.client.services.RemoteServiceException;
import org.ttdc.gwt.server.RemoteServiceSessionServlet;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.dao.AccountDao;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.server.dao.UserObjectDao;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand.Mode;
import org.ttdc.gwt.shared.commands.ForumCommand;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.TagSuggestionCommand;
import org.ttdc.gwt.shared.commands.TagSuggestionCommandMode;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.gwt.shared.commands.results.TagSuggestionCommandResult;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.util.Cryptographer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class RestfulServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(RestfulServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ObjectMapper mapper;

	@Override
	public void init() throws ServletException {
		mapper = new ObjectMapper(); // can reuse, share globally
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doBoth(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doBoth(request, response);
	}

	private void doBoth(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Encoding", "gzip");
		String path = request.getPathInfo();

		try {

			switch (path.toLowerCase()) {
			case "/latestposts":
				performLatestPosts(request, response);
				break;
			case "/login":
				performLogin(request, response);
				break;
			case "/validate":
				performTokenValidation(request, response);
				break;
			case "/topic":
				performTopic(request, response);
				break;
			case "/post":
				performPost(request, response);
				break;
			case "/latestconversations":
				performSearch(request, response);
				break;
			case "/autocomplete":
				performAutoComplete(request, response);
				break;
			case "/forum":
				performForum(request, response);
				break;
			case "/register":
				performPushRegistration(request, response);
				break;
			case "/search":
				performSearch(request, response);
				break;
			case "/like":
				performLikePostRequest(request, response);
				break;
			case "/unlike":
				performUnLikePostRequest(request, response);
				break;
			default:
				perfromInternalServerError(response);
			}

		} catch (Exception e) {
			log.info(e);
			perfromInternalServerError(response);
		}

	}


	private void performPushRegistration(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonNode root = mapper.readTree(request.getInputStream());

		String deviceId = root.get("deviceToken").asText();
		String tokenString = root.has("token") ? root.get("token").asText() : null;
		RestfulToken token;
		
		if(tokenString == null){
			perfromInternalServerError(response);
			return;
		}
		
		try {
			token = RestfulTokenTool.fromTokenString(tokenString);
		} catch (ClassNotFoundException e) {
			log.error(
					"Blew up trying to turn restful token into token object. Allowing user to proceed anonymously.",
					e);
			perfromInternalServerError(response);
			return;
		}
		
		try {
			beginSession();

			Person person = PersonDao.loadPerson(token.getPersonId());
			UserObjectDao.updateUserSetting(person, UserObject.TYPE_DEVICE_TOKEN_FOR_PUSH_NOTIFICATION, deviceId);
			commit();
			response.setStatus(HttpServletResponse.SC_ACCEPTED); // SC_ACCEPTED = 202
		} catch (RuntimeException e) {
			rollback();
			perfromInternalServerError(response);
			throw (e);
		}
	}

	private void performAutoComplete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

		JsonNode root = mapper.readTree(request.getInputStream());

		String query = root.get("query").asText();

		String token = root.has("token") ? root.get("token").asText() : null;

		// PostCrudCommand cmd = mapper.readValue(request.getInputStream(), PostCrudCommand.class);

		SuggestOracle.Request r = new SuggestOracle.Request();
		r.setQuery(query);
		r.setLimit(10);

		TagSuggestionCommand cmd = new TagSuggestionCommand(TagSuggestionCommandMode.SEARCH_POSTS, r);
		cmd.setToken(StringUtil.empty(token) ? null : token);

		TagSuggestionCommandResult result = (TagSuggestionCommandResult) execute(cmd);

		AutoCompleteResult myResult = new AutoCompleteResult();

		for (Suggestion suggestion : result.getResponse().getSuggestions()) {
			SuggestionObject so = (SuggestionObject) suggestion;
			myResult.getItems().add(new AutoCompleteItem(so));
		}

		mapper.writeValue(new GZIPOutputStream(response.getOutputStream()),
				result);
	}

	// Throw away class to hack AutoComplete so that the request and response are small
	static class AutoCompleteResult {
		private List<AutoCompleteItem> items = new ArrayList<AutoCompleteItem>();

		public List<AutoCompleteItem> getItems() {
			return items;
		}

		public void setItems(List<AutoCompleteItem> items) {
			this.items = items;
		}
	}

	// Throw away class to hack AutoComplete so that the request and response are small
	static class AutoCompleteItem /* implements Serializable */{
		private String displayTitle;
		private String postId;

		// private int totalReplyCount;
		// private int conversationCount;

		AutoCompleteItem(SuggestionObject so) {
			// Trevis. so.getDisplayString() has html to bold things
			this.displayTitle = so.getDisplayString();
			this.postId = so.getPost().getPostId();
			// this.totalReplyCount = so.getPost().getMass();
			// this.conversationCount = so.getPost().getReplyCount();

			// this.postId = so.getPost().getMass()
		}

		public String getDisplayTitle() {
			return displayTitle;
		}

		public void setDisplayTitle(String displayTitle) {
			this.displayTitle = displayTitle;
		}

		public String getPostId() {
			return postId;
		}

		public void setPostId(String postId) {
			this.postId = postId;
		}

		// public int getTotalReplyCount() {
		// return totalReplyCount;
		// }
		//
		// public void setTotalReplyCount(int totalReplyCount) {
		// this.totalReplyCount = totalReplyCount;
		// }
		//
		// public int getConversationCount() {
		// return conversationCount;
		// }
		//
		// public void setConversationCount(int conversationCount) {
		// this.conversationCount = conversationCount;
		// }

	}

	/*
	 * 
	 */
	private void performPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PostCrudCommand cmd = mapper.readValue(request.getInputStream(), PostCrudCommand.class);
		cmd.setAddReviewsToMovies(false); // These are added because the website uses them to show summaries of the
											// movie review. Jackson cant handle them because they are circular
											// references.
		CommandResult result = execute(cmd);
		mapper.writeValue(new GZIPOutputStream(response.getOutputStream()),
				result);
	}

	/*
	 * 
	 */
	private void performTopic(HttpServletRequest request, HttpServletResponse response) throws Exception {
		TopicCommand cmd = mapper.readValue(request.getInputStream(), TopicCommand.class);
		CommandResult result = execute(cmd);
		// mapper.writeValue(response.getWriter(), result);
		mapper.writeValue(new GZIPOutputStream(response.getOutputStream()),
				result);
	}

	private void performForum(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ForumCommand cmd = mapper.readValue(request.getInputStream(), ForumCommand.class);
		CommandResult result = execute(cmd);
		mapper.writeValue(new GZIPOutputStream(response.getOutputStream()),
				result);
	}

	private void perfromInternalServerError(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	/*
	 * 
	 */
	private void performLogin(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException,
			IOException, RemoteServiceException, ClassNotFoundException {

		JsonNode root = mapper.readTree(request.getInputStream());

		String username = root.get("username").asText();
		String password = root.get("password").asText();

		try {
			PersonCommandResult result = authenticate(username, password);
			RestfulToken token = new RestfulToken(result.getPerson().getPersonId());
			result.setToken(RestfulTokenTool.toTokenString(token));
			mapper.writeValue(new GZIPOutputStream(response.getOutputStream()),
					result);
		} catch (RuntimeException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private void performTokenValidation(HttpServletRequest request, HttpServletResponse response)
			throws JsonProcessingException, IOException, RemoteServiceException, ClassNotFoundException {

		JsonNode root = mapper.readTree(request.getInputStream());

		String token = root.get("token").asText();
		RestfulToken t2 = RestfulTokenTool.fromTokenString(token);
		String personId = t2.getPersonId();

		try {
			Persistence.beginSession();
			Person person = PersonDao.loadPerson(personId);
			GPerson gPerson = processNewlyAuthenticatedUser(person);
			PersonCommandResult result = new PersonCommandResult(gPerson);
			result.setToken(token);

			mapper.writeValue(new GZIPOutputStream(response.getOutputStream()),
					result);
		} catch (RuntimeException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} finally {
			Persistence.commit();
		}

	}


	/*
	 * Trevis, you added this to get "CONVERSATIONS" this search command seemed to be the only place that you provided
	 * it. It exposes a lot more than you have tested via json.
	 */
	private void performSearch(HttpServletRequest request, HttpServletResponse response) throws IOException {
		SearchPostsCommand cmd = mapper.readValue(request.getInputStream(), SearchPostsCommand.class);

		CommandResult result = execute(cmd);

		// mapper.writeValue(response.getWriter(), result);
		mapper.writeValue(new GZIPOutputStream(response.getOutputStream()),
				result);
	}

	// protected void processUnLikePostRequest(String postId) throws IOException {
	// GPerson user = ConnectionId.getInstance().getCurrentUser();
	// Post post = PostDao.loadPost(postId);
	//
	// GPost gp = FastPostBeanConverter.convertPost(post);
	//
	// GAssociationPostTag association = gp.getLikedByPerson(user.getPersonId());
	// removeAssociation(association);
	// }

	private void performUnLikePostRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		JsonNode root = mapper.readTree(request.getInputStream());
		String postId = root.get("postId").asText();
		String token = root.has("token") ? root.get("token").asText() : null;

		RestfulToken t2 = RestfulTokenTool.fromTokenString(token);
		String personId = t2.getPersonId();

		try {
			Persistence.beginSession();

			Post post = PostDao.loadPost(postId);

			GPost gp = FastPostBeanConverter.convertPost(post);

			GAssociationPostTag association = gp.getLikedByPerson(personId);
			removeAssociation(association, token);
			Persistence.commit();
		} catch (RuntimeException e) {
			rollback();
			throw e;
		}

		response.setStatus(HttpServletResponse.SC_ACCEPTED);
	}


	private void performLikePostRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		JsonNode root = mapper.readTree(request.getInputStream());
		String postId = root.get("postId").asText();
		String token = root.has("token") ? root.get("token").asText() : null;

		RestfulToken t2 = RestfulTokenTool.fromTokenString(token);
		String personId = t2.getPersonId();

		createAssociation(postId, TagConstants.TYPE_LIKE, personId, token);

	}

	private void removeAssociation(GAssociationPostTag association, String token) throws IOException {
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		cmd.setMode(AssociationPostTagCommand.Mode.REMOVE);
		cmd.setAssociationId(association.getGuid());
		cmd.setMode(Mode.REMOVE);
		cmd.setToken(StringUtil.empty(token) ? null : token);
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		// TODO: If this is looking good, you might want to pull this class out of the movie area and use it as a
		// generic post refresh
		// injector.getService().execute(cmd, new MovieRatingPresenter.PostRatingCallback(post));
		execute(cmd);
	}

	private void createAssociation(String postId, String type, String value, String token) throws IOException {
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();

		GTag tag = new GTag();
		tag.setValue(value);
		tag.setType(type);
		cmd.setTag(tag);
		cmd.setPostId(postId);
		cmd.setMode(AssociationPostTagCommand.Mode.CREATE);
		// TODO: If this is looking good, you might want to pull this class out of the movie area and use it as a
		// generic post refresh
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		// injector.getService().execute(cmd, new MovieRatingPresenter.PostRatingCallback(post));

		cmd.setToken(StringUtil.empty(token) ? null : token);

		execute(cmd);
	}

	/*
	 * 
	 */
	private void performLatestPosts(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		LatestPostsCommand cmd = mapper.readValue(request.getInputStream(),
				LatestPostsCommand.class);

		CommandResult result = execute(cmd);

		mapper.writeValue(new GZIPOutputStream(response.getOutputStream()),
				result);
	}

	private <T extends CommandResult> T execute(Command<T> command) throws IOException {
		String personId = null;
		if (command.getToken() != null) {
			RestfulToken t2;
			try {
				t2 = RestfulTokenTool.fromTokenString(command.getToken());
				personId = t2.getPersonId();
			} catch (ClassNotFoundException e) {
				log.error(
						"Blew up trying to turn restful token into token object. Allowing user to proceed anonymously.",
						e);
			}
		}

		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(personId, command);
		CommandResult result = cmdexec.executeCommand();
		return (T) result;

	}

	// Trevis: You copied these methods here because the real ones are burried
	// in RpcServlet, and that guy tries to put the new user into session,
	// which you didnt want for the webservice
	private PersonCommandResult authenticate(String login, String password) {
		Persistence.beginSession();
		Person person = AccountDao.login(login, password);
		GPerson gPerson = processNewlyAuthenticatedUser(person);

		Cryptographer crypto = new Cryptographer(null);
		gPerson.setPassword(crypto.encrypt(password));// I do this on
														// authentication so
														// that the user can
														// save the cookie

		Persistence.commit();

		PersonCommandResult result = new PersonCommandResult(gPerson);
		return result;

	}

	private GPerson processNewlyAuthenticatedUser(Person person) {
		AccountDao.userHit(person.getPersonId());
		return RemoteServiceSessionServlet.broadcastPerson(person);
	}

}
