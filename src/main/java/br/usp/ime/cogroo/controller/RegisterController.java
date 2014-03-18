package br.usp.ime.cogroo.controller;

import java.util.regex.Pattern;

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
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.servlets.ImageCaptchaServlet;
import br.usp.ime.cogroo.util.CriptoUtils;

@Resource
public class RegisterController {

	private static final String LOGIN_REGEX = "[A-Z0-9.@_%+-]+";
	public static final Pattern LOGIN_PATTERN = Pattern.compile(LOGIN_REGEX, Pattern.CASE_INSENSITIVE);
	private static final String EMAIL_REGEX = "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}";
	public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

	private final Result result;
	private UserDAO userDAO;
	private Validator validator;
	private ApplicationData appData;
	private TextSanitizer sanitizer;
  private HttpServletRequest mRequest;
	private static final Logger LOG = Logger
			.getLogger(RegisterController.class);

	private static final String HEADER_TITLE = "Cadastro";
	private static final String HEADER_DESCRIPTION = "Cadastre-se no CoGrOO Comunidade! É rápido e gratuito!";

	public RegisterController(Result result, HttpServletRequest aRequest, UserDAO userDAO,
			Validator validator, ApplicationData appData,
			TextSanitizer sanitizer) {
		this.result = result;
		this.userDAO = userDAO;
		this.validator = validator;
		this.appData = appData;
		this.sanitizer = sanitizer;
		this.mRequest = aRequest;
	}

	@Get
	@Path("/register")
	public void register() {
		result.include("headerTitle", HEADER_TITLE).include(
				"headerDescription", HEADER_DESCRIPTION);
	}

	@Post
	@Path("/sendNewPass")
	public void register(String email){

	}

	@Post
	@Path("/register")
	public void register(String login, String password, String passwordRepeat,
			String email, String name, String twitter, String captcha, boolean iAgree) {
		login = sanitizer.sanitize(login, false);
		email = sanitizer.sanitize(email, false);
		name = sanitizer.sanitize(name, false);
		twitter = sanitizer.sanitize(twitter, false);
		captcha = sanitizer.sanitize(captcha, false);

		email = email.trim();

		if (captcha == null
		    || captcha.isEmpty()
		    || !ImageCaptchaServlet.validateResponse(mRequest, captcha)) {
		  validator.add(new ValidationMessage(ExceptionMessages.INVALID_CAPTCHA,
              ExceptionMessages.INVALID_ENTRY));
        }

		if (name.trim().isEmpty())
			validator.add(new ValidationMessage(ExceptionMessages.USER_CANNOT_BE_EMPTY,
					ExceptionMessages.INVALID_ENTRY));

		if (login.trim().isEmpty() || !LOGIN_PATTERN.matcher(login).matches())
			validator.add(new ValidationMessage(ExceptionMessages.FORBIDDEN_LOGIN,
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
			if (login.trim().startsWith("oauth")) {
				validator.add(new ValidationMessage(ExceptionMessages.FORBIDDEN_LOGIN,
						ExceptionMessages.INVALID_ENTRY));
			}
			if (userDAO.existLogin("cogroo", login)) {
				validator.add(new ValidationMessage(
						ExceptionMessages.USER_ALREADY_EXIST,
						ExceptionMessages.INVALID_ENTRY));
			}
		}

		if (!email.isEmpty()) {
			if (userDAO.existEmail("cogroo", email)) {
				validator.add(new ValidationMessage(
						ExceptionMessages.EMAIL_ALREADY_EXIST, ExceptionMessages.INVALID_ENTRY));
			}
		}

		if(twitter != null) {
			twitter = twitter.replace("@", "");
		}

		validator.onErrorUse(Results.page()).of(RegisterController.class)
				.register();

		User user = new User("cogroo", login);
		user.setPassword(CriptoUtils.digestMD5(login, password));
		user.setEmail(email);
		user.setName(name);
		user.setTwitter(twitter);
		userDAO.add(user);
		appData.incRegisteredMembers();

		result.include("okMessage", "Cadastro realizado com sucesso!");
		result.include("gaEventUserRegistered", true).include("provider", "cogroo");

		result.forwardTo(LoginController.class).login(login, password);

		//result.redirectTo(this).welcome();
	}

}
