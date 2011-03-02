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
import br.usp.ime.cogroo.exceptions.Messages;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.security.RoleProvider;

@Resource
public class UserController {

	private final Result result;
	private UserDAO userDAO;
	private LoggedUser loggedUser;
	private Validator validator;
	private HttpServletRequest request;
	private static final Logger LOG = Logger.getLogger(UserController.class);

	public UserController(Result result, UserDAO userDAO,
			LoggedUser loggedUser, Validator validator, HttpServletRequest request) {
		this.result = result;
		this.userDAO = userDAO;
		this.loggedUser = loggedUser;
		this.validator = validator;
		this.request = request;
	}
	
	@Get
	@Path("/userList")
	public void userList() {
		if(loggedUser.isLogged()) {
			result.include("userList", userDAO.listAll());
		} else {
			validator.add(new ValidationMessage(
					Messages.ONLY_LOGGED_USER_CAN_DO_THIS, Messages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(IndexController.class)
				.index();
		}
	}
	
	@Get
	@Path(value = "/user/{user.id}")
	public void user(User user) {
		if(loggedUser.isLogged()) {
			if(user == null) {
				result.redirectTo(getClass()).userList();
				return;
			}
			user = userDAO.retrieve(user.getId());
			if (user == null) {
				validator.add(new ValidationMessage(Messages.PAGE_NOT_FOUND,
						Messages.ERROR));
				validator.onErrorUse(Results.logic())
						.redirectTo(UserController.class).userList();
			}
			
			result.include("user", user);
			result.include("roleList", RoleProvider.getInstance().getRoles());
		} else {
			validator.add(new ValidationMessage(
					Messages.ONLY_LOGGED_USER_CAN_DO_THIS, Messages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(IndexController.class)
				.index();
		}
	}
	
	@Post
	@Path("/userRole")
	public void userRole(User user, String role) {
		if(loggedUser.isLogged() 
				&& (loggedUser.getUser().getRole().getCanSetUserRole()
						|| loggedUser.getUser().getLogin().equals("admin"))
			) {
			user = userDAO.retrieve(user.getId());
			user.setRole(RoleProvider.getInstance().getRoleForName(role));
			
			result.redirectTo(getClass()).user(user);
			
		} else {
			validator.add(new ValidationMessage(
					Messages.ONLY_LOGGED_USER_CAN_DO_THIS, Messages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(IndexController.class)
				.index();
		}
	}

}
