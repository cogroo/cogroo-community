package br.usp.ime.cogroo.controller;

import java.util.regex.Pattern;

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
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.util.CriptoUtils;

@Resource
public class RegisterController {
	
	private static final String EMAIL_REGEX = "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}";
	private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

	private final Result result;
	private UserDAO userDAO;
	private Validator validator;
	private ApplicationData appData;
	private TextSanitizer sanitizer;
	private static final Logger LOG = Logger
			.getLogger(RegisterController.class);

	public RegisterController(Result result, UserDAO userDAO,
			Validator validator, ApplicationData appData,
			TextSanitizer sanitizer) {
		this.result = result;
		this.userDAO = userDAO;
		this.validator = validator;
		this.appData = appData;
		this.sanitizer = sanitizer;
	}

	@Get
	@Path("/register")
	public void register() {
	}
	
	@Get
	@Path("/welcome")
	public void welcome() {
	}
	
	@Post
	@Path("/sendNewPass")
	public void register(String email){
		
	}

	@Post
	@Path("/register")
	public void register(String login, String password, String passwordRepeat,
			String email, String name, boolean iAgree) {
		login = sanitizer.sanitize(login, false);
		email = sanitizer.sanitize(email, false);
		name = sanitizer.sanitize(name, false);
		
		email = email.trim();
		
		if (name.trim().isEmpty())
			validator.add(new ValidationMessage(ExceptionMessages.USER_CANNOT_BE_EMPTY,
					ExceptionMessages.INVALID_ENTRY));
		
		if (login.trim().isEmpty())
			validator.add(new ValidationMessage(ExceptionMessages.LOGIN_CANNOT_BE_EMPTY,
					ExceptionMessages.INVALID_ENTRY));

		if (email.isEmpty() || !EMAIL_PATTERN.matcher(email).matches())
			validator.add(new ValidationMessage(ExceptionMessages.INVALID_EMAIL,
					ExceptionMessages.INVALID_ENTRY));
		
		if (password.trim().isEmpty())
			validator.add(new ValidationMessage(ExceptionMessages.PASSWORD_CANNOT_BE_EMPTY,
					ExceptionMessages.INVALID_ENTRY));

		if (!password.equals(passwordRepeat)) {
			validator.add(new ValidationMessage(ExceptionMessages.USER_REPEAT_PASSWORD_WRONG,
					ExceptionMessages.INVALID_ENTRY));
		}

		if (!iAgree) {
			validator.add(new ValidationMessage(
					ExceptionMessages.USER_MUST_BE_AGREE_TERMS, ExceptionMessages.INVALID_ENTRY));
		}

		if (!login.trim().isEmpty()) {
			User userFromDB = userDAO.retrieveByLogin(login);
			if (userFromDB != null) {
				validator.add(new ValidationMessage(
						ExceptionMessages.USER_ALREADY_EXIST, ExceptionMessages.INVALID_ENTRY));
			}
		}
		
		if (!email.isEmpty()) {
			User userFromDB = userDAO.retrieveByEmail(email);
			if (userFromDB != null) {
				validator.add(new ValidationMessage(
						ExceptionMessages.EMAIL_ALREADY_EXIST, ExceptionMessages.INVALID_ENTRY));
			}
		}

		validator.onErrorUse(Results.page()).of(RegisterController.class)
				.register();

		User user = new User(login);
		user.setPassword(CriptoUtils.digestMD5(login, password));
		user.setEmail(email);
		user.setName(name);
		userDAO.add(user);
		appData.incRegisteredMembers();
		
		result.include("justRegistered", true).include("login", login);
		
		result.forwardTo(LoginController.class).login(login, password);
		
		//result.redirectTo(this).welcome();
	}
	
}
