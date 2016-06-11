package org.ttdc.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.client.services.RemoteServiceException;
import org.ttdc.gwt.server.RemoteServiceSessionServlet;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.dao.AccountDao;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.util.Cryptographer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		String path = request.getPathInfo();

		try {

			switch (path.toLowerCase()) {
			case "/latestposts":
				performLatestPosts(request, response);
				break;
			case "/login":
				performLogin(request, response);
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
			default:
				perfromInternalServerError(response);
			}

		} catch (Exception e) {
			log.info(e);
			perfromInternalServerError(response);
		}

	}


	/*
	 * 
	 */
	private void performPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PostCrudCommand cmd = mapper.readValue(request.getInputStream(), PostCrudCommand.class);
		CommandResult result = execute(cmd);
		mapper.writeValue(response.getWriter(), result);
	}

	/*
	 * 
	 */
	private void performTopic(HttpServletRequest request, HttpServletResponse response) throws Exception {
		TopicCommand cmd = mapper.readValue(request.getInputStream(), TopicCommand.class);
		CommandResult result = execute(cmd);
		mapper.writeValue(response.getWriter(), result);
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
			mapper.writeValue(response.getWriter(), result);
		} catch (RuntimeException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}


	/*
	 * Trevis, you added this to get "CONVERSATIONS" this search command seemed to be the only place that you provided
	 * it. It exposes a lot more than you have tested via json.
	 */
	private void performSearch(HttpServletRequest request, HttpServletResponse response) throws IOException {
		SearchPostsCommand cmd = mapper.readValue(request.getInputStream(), SearchPostsCommand.class);

		CommandResult result = execute(cmd);

		mapper.writeValue(response.getWriter(), result);
	}

	/*
	 * 
	 */
	private void performLatestPosts(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		LatestPostsCommand cmd = mapper.readValue(request.getInputStream(),
				LatestPostsCommand.class);

		CommandResult result = execute(cmd);

		mapper.writeValue(response.getWriter(), result);
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