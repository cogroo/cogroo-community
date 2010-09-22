package br.usp.ime.cogroo.controller;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.Messages;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;

@Resource
public class LoginController {

	private final Result result;
	private UserDAO userDAO;
	private LoggedUser loggedUser;
	private Validator validator;
	private static final Logger LOG = Logger.getLogger(LoginController.class);

	public LoginController(Result result, UserDAO userDAO,
			LoggedUser loggedUser, Validator validator) {
		this.result = result;
		this.userDAO = userDAO;
		this.loggedUser = loggedUser;
		this.validator = validator;
	}

	@Get
	@Path("/login")
	public void login() {
	}

	@Post
	@Path("/login")
	public void login(User user) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("User trying to loging " + user.getName());
		}

		if (user.getName().trim().isEmpty()) {
			validator.add(new ValidationMessage(Messages.USER_CANNOT_BE_EMPTY,
					Messages.ERROR));
		}
		
		validator.onErrorUse(Results.page()).of(LoginController.class).login();

		User userFromDB = userDAO.retrieve(user.getName());
		if (userFromDB == null) {
			LOG.info("User unknown. Will create new " + user.getName());
			userFromDB = new User(user.getName());
			userDAO.add(userFromDB);
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("User exists.");
			}
		}
		loggedUser.setUser(userFromDB);

		result.redirectTo(IndexController.class).index();
	}

	@Get
	@Path("/logout")
	public void logout() {
		loggedUser.logout();
		result.redirectTo(IndexController.class).index();
	}

}
