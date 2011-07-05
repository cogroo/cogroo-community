package br.usp.ime.cogroo.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;

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
import br.usp.ime.cogroo.logic.TextSanitizer;
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
	private TextSanitizer sanitizer;
	
	private static final String HEADER_TITLE = "Login";
	private static final String HEADER_DESCRIPTION = "Efetue login no CoGrOO Comunidade! É rápido e gratuito!";


	public LoginController(Result result, UserDAO userDAO,
			LoggedUser loggedUser, Validator validator,
			HttpServletRequest request, ApplicationData appData,
			TextSanitizer sanitizer) {
		this.result = result;
		this.userDAO = userDAO;
		this.loggedUser = loggedUser;
		this.validator = validator;
		this.request = request;
		this.appData = appData;
		this.sanitizer = sanitizer;
	}

	@Get
	@Path("/login")
	public void login() {
		if (loggedUser.isLogged()) {
			String lastURL = loggedUser.getLastURLVisited();
			if(lastURL != null && lastURL.length() > 0) {
				result.redirectTo(loggedUser.getLastURLVisited());
			} else {
				result.redirectTo(IndexController.class).index();
			}
			return;
		}
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
		
		if (!login.trim().isEmpty()) {
			if (login.trim().startsWith("oauth")) {
				validator.add(new ValidationMessage(
						ExceptionMessages.FORBIDDEN_LOGIN,
						ExceptionMessages.INVALID_ENTRY));
				validator.onErrorUse(Results.page()).of(LoginController.class)
						.login();
			}
		}
		
		if (!userDAO.existLogin("cogroo", login)) {
			LOG.info("User unknown[" + login
					+ "]. Redirecting to register page.");
			validator.add(new ValidationMessage(ExceptionMessages.USER_DONT_EXISTS,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.page()).of(LoginController.class).login();
		}
		User userFromDB = userDAO.retrieveByLogin("cogroo", login);

		String passCripto = CriptoUtils.digestMD5(login, password);
		String passFromDB = userFromDB.getPassword();

		if (!passCripto.equalsIgnoreCase(passFromDB)) {
			LOG.info("Password Failed[" + login
					+ "|" + userFromDB.getEmail() + "]. Redirecting to login page.");
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
	public void oauthLogin(String service) {
		if (loggedUser.isLogged()) {
			String lastURL = loggedUser.getLastURLVisited();
			if(lastURL != null && lastURL.length() > 0) {
				result.redirectTo(loggedUser.getLastURLVisited());
			} else {
				result.redirectTo(IndexController.class).index();
			}
			return;
		}
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
			SocialAuthConfig config = SocialAuthConfig.getDefault();
			config.load();
			
			SocialAuthManager manager = new SocialAuthManager();
			manager.setSocialAuthConfig(config);

			String returnToUrl = BuildUtil.BASE_URL + "login/oauth";
			String redirectUrl = manager.getAuthenticationUrl(service, returnToUrl);

			request.getSession().setAttribute("authManager", manager);
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
	public void oauthLogin() {
		if (loggedUser.isLogged()) {
			String lastURL = loggedUser.getLastURLVisited();
			if(lastURL != null && lastURL.length() > 0) {
				result.redirectTo(loggedUser.getLastURLVisited());
			} else {
				result.redirectTo(IndexController.class).index();
			}
			return;
		}
		SocialAuthManager manager = (SocialAuthManager) request.getSession()
				.getAttribute("authManager");
		if (manager == null) {
			result.redirectTo(LoginController.class).login();
			return;
		}

		AuthProvider provider = null;
		String service = null;
		Profile p = null;
		try {
			provider = manager.connect(SocialAuthUtil
					.getRequestParametersMap(request));
			service = provider.getProviderId();
			p = (Profile) provider.getUserProfile();
		} catch (Exception e) {
			LOG.error("Could not verify user using OAuth on " + service, e);
			validator.add(new ValidationMessage(
					ExceptionMessages.OAUTH_VERIFY_USER_ERROR,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.page()).of(LoginController.class)
					.login();
		}

		if (LOG.isDebugEnabled()) {	
			LOG.debug("OAuth profile for service " + service);
			LOG.debug(p);
		}

		// Try to get a valid name in the following order of priority:
		// 1. fullName
		// 2. firstName + lastName
		// 3. displayName
		String name = p.getFullName() != null ? p.getFullName() : (p
				.getFirstName() != null || p.getLastName() != null ? p
				.getFirstName() + " " + p.getLastName() : p.getDisplayName());

		// TODO replace / and other special characters, if necessary.
		String login = "oauth-" + service + "-" + p.getValidatedId();

		if (!userDAO.existLogin(service, login)) {
			// Register
			if (LOG.isDebugEnabled()) {
				LOG.debug("Trying to register OAuth user " + login
						+ " with service " + service);
			}

			User user = new User(service, login);
			if (p.getEmail() != null)
				user.setEmail(p.getEmail());
			if (name != null)
				user.setName(name);
			if (service.equals("twitter"))
				user.setTwitter(p.getDisplayName());
			result.include(user);

			return;
		}

		oauthLogin(service, login);
	}

	@Post
	@Path("/register/oauth")
	public void oauthRegister(String provider, String login, String email,
			String name, String twitter, boolean iAgree) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Registering new OAuth user " + login + " with service "
					+ provider);
		}

		// TODO move to logic
		provider = sanitizer.sanitize(provider, false);
		login = sanitizer.sanitize(login, false);
		email = sanitizer.sanitize(email, false);
		name = sanitizer.sanitize(name, false);
		twitter = sanitizer.sanitize(twitter, false);

		email = email.trim();

		if (provider.trim().isEmpty())
			validator.add(new ValidationMessage(ExceptionMessages.EMPTY_FIELD,
					ExceptionMessages.INVALID_ENTRY));

		if (name.trim().isEmpty())
			validator.add(new ValidationMessage(
					ExceptionMessages.USER_CANNOT_BE_EMPTY,
					ExceptionMessages.INVALID_ENTRY));

		if (login.trim().isEmpty())
			validator.add(new ValidationMessage(
					ExceptionMessages.LOGIN_CANNOT_BE_EMPTY,
					ExceptionMessages.INVALID_ENTRY));

		if (email.isEmpty()
				|| !RegisterController.EMAIL_PATTERN.matcher(email).matches())
			validator.add(new ValidationMessage(
					ExceptionMessages.INVALID_EMAIL,
					ExceptionMessages.INVALID_ENTRY));

		if (!iAgree) {
			validator.add(new ValidationMessage(
					ExceptionMessages.USER_MUST_BE_AGREE_TERMS,
					ExceptionMessages.INVALID_ENTRY));
		}

		if (!login.trim().isEmpty()) {
			if (!login.trim().startsWith("oauth")) {
				validator.add(new ValidationMessage(
						ExceptionMessages.FORBIDDEN_LOGIN,
						ExceptionMessages.INVALID_ENTRY));
			}
			if (userDAO.existLogin(provider, login)) {
				validator.add(new ValidationMessage(
						ExceptionMessages.USER_ALREADY_EXIST,
						ExceptionMessages.INVALID_ENTRY));
			}
		}

		if (!email.isEmpty()) {
			if (userDAO.existEmail(provider, email)) {
				validator.add(new ValidationMessage(
						ExceptionMessages.EMAIL_ALREADY_EXIST,
						ExceptionMessages.INVALID_ENTRY));
			}
		}

		if (twitter != null) {
			twitter = twitter.replace("@", "");
		}

		validator.onErrorUse(Results.logic()).redirectTo(LoginController.class)
				.oauthLogin();

		User user = new User(provider, login);
		user.setEmail(email);
		user.setName(name);
		user.setTwitter(twitter);
		userDAO.add(user);
		appData.incRegisteredMembers();

		result.include("okMessage", "Cadastro realizado com sucesso!");
		result.include("gaEventUserRegistered", true).include("provider",
				provider);

		oauthLogin(provider, login);
	}

	// TODO move to logic?
	private void oauthLogin(String service, String login) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Loging OAuth user " + login + " with service " + service);
		}

		User userFromDB = userDAO.retrieveByLogin(service, login);
		
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
		request.getSession().invalidate();
		result.redirectTo(IndexController.class).index();
	}

}
