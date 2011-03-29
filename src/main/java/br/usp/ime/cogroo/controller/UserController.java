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
import br.usp.ime.cogroo.logic.TextSanitizer;
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
	private TextSanitizer sanitizer;
	private static final Logger LOG = Logger.getLogger(UserController.class);

	public UserController(Result result, UserDAO userDAO,
			LoggedUser loggedUser, Validator validator, HttpServletRequest request,
			TextSanitizer sanitizer) {
		this.result = result;
		this.userDAO = userDAO;
		this.loggedUser = loggedUser;
		this.validator = validator;
		this.request = request;
		this.sanitizer = sanitizer;
	}
	
	@Get
	@Path("/userList")
	public void userList() {
		if(loggedUser.isLogged()) {
			result.include("userList", userDAO.listAll());
		} else {
			validator.add(new ValidationMessage(
					ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS, ExceptionMessages.ERROR));
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
				validator.add(new ValidationMessage(ExceptionMessages.PAGE_NOT_FOUND,
						ExceptionMessages.ERROR));
				validator.onErrorUse(Results.logic())
						.redirectTo(UserController.class).userList();
			}
			
			result.include("user", user);
			result.include("roleList", RoleProvider.getInstance().getRoles());
		} else {
			validator.add(new ValidationMessage(
					ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS, ExceptionMessages.ERROR));
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
					ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS, ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(IndexController.class)
				.index();
		}
	}
	
	@Post
	@Path("/editUser")
	public void editUser(User user, String name, String email, boolean isReceiveEmail) {
		
		user = userDAO.retrieve(user.getId());
		
		if( user.equals(loggedUser.getUser()) || (loggedUser.getUser().getRole().getCanEditSensitiveUserDetails())
			) {
			
			
			// sanitizer 
			name = sanitizer.sanitize(name, false);
			email = sanitizer.sanitize(email, false);
			
			// validate email
			if (!email.isEmpty()) {
				User userFromDB = userDAO.retrieveByEmail(email);
				if (userFromDB != null && !userFromDB.getEmail().equals(email)) {
					validator.add(new ValidationMessage(
							ExceptionMessages.EMAIL_ALREADY_EXIST, ExceptionMessages.INVALID_ENTRY));
				}
			}
			
			if (email.isEmpty() || !RegisterController.EMAIL_PATTERN.matcher(email).matches())
				validator.add(new ValidationMessage(ExceptionMessages.INVALID_EMAIL,
						ExceptionMessages.INVALID_ENTRY));
			
			
			// validate name
			if (name.trim().isEmpty())
				validator.add(new ValidationMessage(ExceptionMessages.USER_CANNOT_BE_EMPTY,
						ExceptionMessages.INVALID_ENTRY));
			
			validator.onErrorUse(Results.page()).of(UserController.class)
				.user(user);
			
			boolean changed = false;
			if(!user.getEmail().equals(email)) {
				changed = true;
				user.setEmail(email);
			}
			
			if(!user.getName().equals(name)) {
				changed = true;
				user.setName(name);
			}
			
			if(!user.getName().equals(name)) {
				changed = true;
				user.setName(name);
			}
			
			if(isReceiveEmail != user.getIsReceiveEmail()) {
				changed = true;
				user.setIsReceiveEmail(isReceiveEmail);
			}
			
			if(changed)
				userDAO.update(user);
			
			result.redirectTo(getClass()).user(user);
			
		} else {
			validator.add(new ValidationMessage(
					ExceptionMessages.USER_UNAUTHORIZED, ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(IndexController.class)
				.index();
		}
	}

}
