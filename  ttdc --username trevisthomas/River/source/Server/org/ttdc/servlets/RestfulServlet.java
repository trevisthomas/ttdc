package org.ttdc.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.client.services.RemoteServiceException;
import org.ttdc.gwt.server.RemoteServiceSessionServlet;
import org.ttdc.gwt.server.RpcServlet;
import org.ttdc.gwt.server.command.executors.LatestPostCommandExecutor;
import org.ttdc.gwt.server.dao.AccountDao;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.gwt.shared.commands.types.PostListType;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.PopulateCache;
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
			default:
				perfromInternalServerError(response);
			}

		} catch (Exception e) {
			log.info(e);
			perfromInternalServerError(response);
		}

	}

	private void perfromInternalServerError(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	private void performLogin(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException,
			IOException, RemoteServiceException, ClassNotFoundException {

		JsonNode root = mapper.readTree(request.getInputStream());

		String username = root.get("username").asText();
		String password = root.get("password").asText();
		
		RestfulToken token = new RestfulToken("testname", "testpwd");
		
		String t = RestfulTokenTool.toTokenString(token);
		
		RestfulToken t2 = RestfulTokenTool.fromTokenString(t);
		 
		// String username = root.get("username").toString().replace('\"',
		// ' ').trim();
		// String password = root.get("password").toString().replace('\"',
		// ' ').trim();

//		RpcServlet authenticationServlet = new RpcServlet();

		try {
			PersonCommandResult result = authenticate(
					username, password);
			mapper.writeValue(response.getWriter(), result);
		} catch (RuntimeException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	// Trevis: You copied these methods here because the real ones are burried
	// in RpcServlet, and that guy tries to put the new ueser into session,
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

	private void performLatestPosts(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		LatestPostsCommand cmd = mapper.readValue(request.getInputStream(),
				LatestPostsCommand.class);

		LatestPostCommandExecutor commandExecutor = new LatestPostCommandExecutor();

		PaginatedListCommandResult<GPost> result = commandExecutor.execute(cmd);

		mapper.writeValue(response.getWriter(), result);
	}

}
