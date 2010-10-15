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
import br.usp.ime.cogroo.Util.CriptoUtils;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.model.User;

@Resource
public class RegisterController {

	private final Result result;
	private UserDAO userDAO;
	private Validator validator;
	private static final Logger LOG = Logger
			.getLogger(RegisterController.class);

	public RegisterController(Result result, UserDAO userDAO,
			Validator validator) {
		this.result = result;
		this.userDAO = userDAO;
		this.validator = validator;
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
		// XXX Embora name não possa ser vazio, não há um asterisco de
		//     campo obrigatório no jsp.
		if (password.trim().isEmpty() || email.trim().isEmpty()
				|| name.trim().isEmpty()) {
			validator.add(new ValidationMessage(Messages.USER_CANNOT_BE_EMPTY,
					Messages.INVALID_ENTRY));
		}

		if (!password.equals(passwordRepeat)) {
			validator.add(new ValidationMessage(Messages.USER_REPEAT_PASSWORD_WRONG,
					Messages.INVALID_ENTRY));
		}

		if (!iAgree) {
			validator.add(new ValidationMessage(
					Messages.USER_MUST_BE_AGREE_TERMS, Messages.INVALID_ENTRY));
		}

		if (!login.trim().isEmpty()) {
			User userFromDB = userDAO.retrieveByLogin(login);
			if (userFromDB != null) {
				validator.add(new ValidationMessage(
						Messages.USER_ALREADY_EXIST, Messages.INVALID_ENTRY));
			}
		}

		validator.onErrorUse(Results.page()).of(RegisterController.class)
				.register();

		// TODO CRIAR USUARIO E Redirecionar.
		User user = new User(login);
		user.setPassword(CriptoUtils.digestMD5(login, password));
		user.setEmail(email);
		user.setName(name);
		userDAO.add(user);
		
		result.redirectTo(this).welcome();

	}
}
