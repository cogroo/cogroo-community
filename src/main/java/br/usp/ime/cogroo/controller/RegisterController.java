package br.usp.ime.cogroo.controller;

import org.apache.commons.mail.EmailException;
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
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.util.CriptoUtils;
import br.usp.ime.cogroo.util.EmailSender;

@Resource
public class RegisterController {

	private final Result result;
	private UserDAO userDAO;
	private Validator validator;
	private ApplicationData appData;
	private static final Logger LOG = Logger
			.getLogger(RegisterController.class);

	public RegisterController(Result result, UserDAO userDAO,
			Validator validator, ApplicationData appData) {
		this.result = result;
		this.userDAO = userDAO;
		this.validator = validator;
		this.appData = appData;
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

		// TODO Fazer e refatorar as Validações.
		if (password.trim().isEmpty() || email.trim().isEmpty()
				|| name.trim().isEmpty()) {
			validator.add(new ValidationMessage(ExceptionMessages.USER_CANNOT_BE_EMPTY,
					ExceptionMessages.INVALID_ENTRY));
		}

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
	
	@Get
	@Path("/sendEmail")
	public void send() throws EmailException {
		EmailSender.sendEmail("Algum texto", "Foi !!");
		result.redirectTo(IndexController.class).index("Email enviado !");		
	}
}
