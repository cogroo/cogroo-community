package br.usp.ime.cogroo.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.AuthProviderFactory;
import org.brickred.socialauth.Profile;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.Services;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.util.BuildUtil;
import br.usp.ime.cogroo.util.CriptoUtils;

@Resource
public class LoginController {

	private final Result result;
	private UserDAO userDAO;
	private LoggedUser loggedUser;
	private Validator validator;
	private static final Logger LOG = Logger.getLogger(LoginController.class);
	
	private HttpServletRequest request;
	private ApplicationData appData;
	
	private static final String HEADER_TITLE = "Login";
	private static final String HEADER_DESCRIPTION = "Efetue login no CoGrOO Comunidade! É rápido e gratuito!";


	public LoginController(Result result, UserDAO userDAO,
			LoggedUser loggedUser, Validator validator, HttpServletRequest request, ApplicationData appData) {
		this.result = result;
		this.userDAO = userDAO;
		this.loggedUser = loggedUser;
		this.validator = validator;
		this.request = request;
		this.appData = appData;
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

		User userFromDB = userDAO.retrieveByLogin("cogroo", login);
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
		
		result.include("gaEventUserLogged", true).include("provider", "cogroo");
		
		String lastURL = loggedUser.getLastURLVisited();
		if(lastURL != null && lastURL.length() > 0) {
			result.redirectTo(loggedUser.getLastURLVisited());
		} else {
			result.redirectTo(IndexController.class).index();
		}
	}

	@Get
	@Path("/login/oauth/{service}")
	public void socialLogin(String service) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Trying to login with OAuth on service " + service);
		}
		
		if (!Services.contains(service)) {
			LOG.info("Trying to login with OAuth on invalid service " + service);
			validator.add(new ValidationMessage(
					ExceptionMessages.OAUTH_INVALID_SERVICE,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.page()).of(LoginController.class)
					.login();
		}

		try {
			AuthProvider provider = AuthProviderFactory.getInstance(service);

			String returnToUrl = BuildUtil.BASE_URL + "login/oauth";
			String redirectUrl = provider.getLoginRedirectURL(returnToUrl);

			request.getSession().setAttribute("service", service);
			request.getSession().setAttribute("SocialAuth", provider);
			result.redirectTo(redirectUrl);
		} catch (Exception e) {
			LOG.error("Could not authenticate user using OAuth on " + service,
					e);
			validator.add(new ValidationMessage(
					ExceptionMessages.OAUTH_AUTHENTICATION_ERROR,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.page()).of(LoginController.class)
					.login();
		}
	}

	@Get
	@Path("/login/oauth")
	public void socialLogin() {
		String service = (String) request.getSession().getAttribute("service");
		AuthProvider provider = (AuthProvider) request.getSession()
				.getAttribute("SocialAuth");
		if (service == null || provider == null)
			return;

		Profile p = null;
		try {
			p = provider.verifyResponse(request);
		} catch (Exception e) {
			LOG.error("Could not verify user using OAuth on " + service,
					e);
			validator.add(new ValidationMessage(
					ExceptionMessages.OAUTH_VERIFY_USER_ERROR,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.page()).of(LoginController.class).login();
		}
		if (p == null) {
			LOG.error("Could not verify user using OAuth on " + service);
			validator.add(new ValidationMessage(
					ExceptionMessages.OAUTH_VERIFY_USER_ERROR,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.page()).of(LoginController.class).login();
		}		
		

		// Try to get a valid name in the following order of priority:
		// 1. fullName
		// 2. firstName + lastName
		// 3. displayName
		String name = p.getFullName() != null ? p.getFullName() : (p
				.getFirstName() != null || p.getLastName() != null ? p
				.getFirstName() + " " + p.getLastName() : p.getDisplayName());

		// FIXME replace / and other characters (???)
		String login = "oauth-" + p.getValidatedId(); 

		User userFromDB = userDAO.retrieveByLogin(service, login);

		if (userFromDB == null) {
			// Register
			if (LOG.isDebugEnabled()) {
				LOG.debug("Registering new OAuth user " + login + " with service " + service);
			}
			
			User user = new User(service, login);
			if (p.getEmail() != null)
				user.setEmail(p.getEmail());
			if (name != null)
				user.setName(name);
			if (service.equals("twitter"))
				user.setTwitter(p.getDisplayName());
			userDAO.add(user);
			appData.incRegisteredMembers();

			result.include("gaEventUserRegistered", true).include("provider", service);

			userFromDB = user;
		}
		
		// Login
		if (LOG.isDebugEnabled()) {
			LOG.debug("Loging OAuth user " + login + " with service " + service);
			LOG.debug("Profile:");
			LOG.debug(p);
		}

		userFromDB.setLastLogin(System.currentTimeMillis());
		userDAO.update(userFromDB);
		loggedUser.login(userFromDB);

		result.include("gaEventUserLogged", true).include("provider", service);


		String lastURL = loggedUser.getLastURLVisited();
		if (lastURL != null && lastURL.length() > 0) {
			result.redirectTo(lastURL);
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
