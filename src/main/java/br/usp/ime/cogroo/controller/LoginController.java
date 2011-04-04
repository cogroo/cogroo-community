package br.usp.ime.cogroo.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.util.CriptoUtils;

@Resource
public class LoginController {

	private final Result result;
	private UserDAO userDAO;
	private LoggedUser loggedUser;
	private Validator validator;
	private static final Logger LOG = Logger.getLogger(LoginController.class);
	
	private static final String HEADER_TITLE = "Login";
	private static final String HEADER_DESCRIPTION = "Efetue login no CoGrOO Comunidade! É rápido e gratuito!";


	public LoginController(Result result, UserDAO userDAO,
			LoggedUser loggedUser, Validator validator, HttpServletRequest request) {
		this.result = result;
		this.userDAO = userDAO;
		this.loggedUser = loggedUser;
		this.validator = validator;
	}

	@Get
	@Path("/login")
	public void login() {
		result.include("headerTitle", HEADER_TITLE).include(
				"headerDescription", HEADER_DESCRIPTION);
	}

	@Post
	@Path("/login")
	public void login(String login, String password) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("User trying to loging " + login);
		}

		if (login.trim().isEmpty() || password.trim().isEmpty()) {
			validator.add(new ValidationMessage(ExceptionMessages.USER_CANNOT_BE_EMPTY,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.page()).of(LoginController.class)
					.login();
		}

		User userFromDB = userDAO.retrieveByLogin(login);
		if (userFromDB == null) {
			LOG.info("User unknown[" + login
					+ "]. Redirecting to register page.");
			validator.add(new ValidationMessage(ExceptionMessages.USER_DONT_EXISTS,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.page()).of(RegisterController.class).register();
		}

		String passCripto = CriptoUtils.digestMD5(login, password);
		String passFromDB = userFromDB.getPassword();

		if (!passCripto.equalsIgnoreCase(passFromDB)) {
			LOG.info("Password Failed[" + login
					+ "]. Redirecting to login page.");
			validator.add(new ValidationMessage(ExceptionMessages.USER_PASSWORD_FAILED,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.page()).of(LoginController.class)
					.login();
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("User exists.");
		}
		userFromDB.setLastLogin(System.currentTimeMillis());
		userDAO.update(userFromDB);
		loggedUser.login(userFromDB);
		
		result.include("justLogged", true).include("login", login);
		
		String lastURL = loggedUser.getLastURIVisited();
		if(lastURL != null && lastURL.length() > 0) {
			result.redirectTo(loggedUser.getLastURIVisited());
		} else {
			result.redirectTo(IndexController.class).index();
		}
	}

	@Get
	@Path("/logout")
	public void logout() {
		loggedUser.logout();
		result.redirectTo(IndexController.class).index();
	}

}
