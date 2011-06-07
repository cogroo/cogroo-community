package br.usp.ime.cogroo.controller;

import java.util.Date;
import java.util.Random;

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
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.notifiers.Notificator;
import br.usp.ime.cogroo.util.CriptoUtils;

@Resource
public class RecoverPasswordController {
	
	private static final Logger LOG = Logger
			.getLogger(RecoverPasswordController.class);
	
	private final Result result;
	private UserDAO userDAO;
	private Validator validator;
	private Notificator notificator;
	Random random = new Random(System.currentTimeMillis());
	private final HttpServletRequest request;

	public RecoverPasswordController(Result result, UserDAO userDAO,
			Validator validator, Notificator notificator, HttpServletRequest request) {
		this.result = result;
		this.userDAO = userDAO;
		this.validator = validator;
		this.request = request;
		this.notificator = notificator;
	}

	@Get
	@Path("/recover")
	public void recover() {
		result.include("headerTitle", "Recuperação de senha")
				.include("headerDescription",
						"Recupere o acesso ao CoGrOO Comunidade através da criação de uma nova senha.");
	}

	@Get
	@Path("/recover/{user.id}/{codeRecover}")
	public void verifyCodeRecover(User user, String codeRecover) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("verifyCodeRecover for user.id>>>: " + user.getId());
		}
		User userFromDB = getUserIfValidate(user, codeRecover);

		validator.onErrorUse(Results.page())
				.of(RecoverPasswordController.class).recover();

		/*
		 * If all is ok, then... redirect to form to create new password.
		 */
		result.include("codeRecover", codeRecover);
		result.include("user", userFromDB);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("<<< verifyCodeRecover");
		}
	}

	@Post
	@Path("/recover/{user.id}/{codeRecover}")
	public void changePassword(String password, String passwordRepeat,
			User user, String codeRecover) {
		User userFromDB = getUserIfValidate(user, codeRecover);
		if (!password.equals(passwordRepeat)) {
			validator.add(new ValidationMessage(
					ExceptionMessages.USER_REPEAT_PASSWORD_WRONG,
					ExceptionMessages.ERROR));
		}

		validator.onErrorUse(Results.page())
				.of(RecoverPasswordController.class).verifyCodeRecover(user,
						codeRecover);

		/*
		 * If all is ok, then... redirect to form COMPLETED
		 */
		userFromDB.setPassword(CriptoUtils.digestMD5(userFromDB.getLogin(),
				password));
		userDAO.update(userFromDB);
		
		result.include("gaEventPasswordRecovered", true).include("provider", userFromDB.getService());

	}

	@Post
	@Path("/recover")
	public void sendMailRecover(String email) {
		User userFromDB = new User();
		/*
		 * Validators
		 */
		if (email.trim().isEmpty()) {
			validator.add(new ValidationMessage(ExceptionMessages.EMPTY_FIELD,
					ExceptionMessages.ERROR));
		} else {
			userFromDB = userDAO.retrieveByEmail("cogroo", email);
			if (userFromDB == null) {
				validator.add(new ValidationMessage(
						ExceptionMessages.INVALID_EMAIL,
						ExceptionMessages.ERROR));
			}
		}

		result.include("email", email);
		validator.onErrorUse(Results.page())
				.of(RecoverPasswordController.class).recover();
		/*
		 * Logic create HashCode ... Need send email...
		 */

		String codeRecover = String.valueOf(System.currentTimeMillis())
				+ getRandomField(userFromDB);
		codeRecover = CriptoUtils.digestMD5(codeRecover);

		userFromDB.setDateRecoverCode(new Date());
		userFromDB.setRecoverCode(codeRecover);
		userDAO.update(userFromDB);

		result.include("email", userFromDB.getEmail());
		result.include("codeRecover", codeRecover);
		/*
		 * Envio de email... Necessita refatoração
		 */
		// TODO: Refatorar !!

		String url = request.getRequestURL().toString() + "/"
				+ userFromDB.getId() + "/" + codeRecover;
		StringBuilder body = new StringBuilder();
		body.append("Olá, " + userFromDB.getName() + "!<br><br>");
		body.append("De acordo com sua solicitação no portal CoGrOO Comunidade, enviamos um link para redefinir sua senha:<br>");
		body.append("<a href=\"" + url + "\">" + url + "</a><br><br>");
		body.append("Lembrando que seu login é \"" + userFromDB.getLogin() + "\".<br>");
		
		String subject = "Redefinição de senha";
		notificator.sendEmail(body.toString(), subject, userFromDB.getEmail().trim());

	}

	private String getRandomField(User userFromDB) {
		String value = "";
		Integer cmp = random.nextInt(100);
		if (cmp >= 0 && cmp <= 33) {
			value = userFromDB.getName();
		} else if (cmp > 33 && cmp <= 66) {
			value = userFromDB.getEmail();
		} else if (cmp > 66) {
			value = userFromDB.getLogin();
		}
		return value;
	}

	private User getUserIfValidate(User user, String codeRecover) {
		User userFromDB = new User();

		// Validators
		if (user.getId() <= 0 || codeRecover.trim().isEmpty()) {
			LOG.warn("Password recovering with empty email.");
			validator.add(new ValidationMessage(ExceptionMessages.EMPTY_FIELD,
					ExceptionMessages.ERROR));
		} else {
			userFromDB = userDAO.retrieve(user.getId());
			if (userFromDB != null) {
				if (!userFromDB.getRecoverCode().equals(codeRecover)) {
					LOG.warn("Bad recovery code for user: " + user.toString());
					validator.add(new ValidationMessage(
							ExceptionMessages.BAD_RECOVERY_CODE, 
							ExceptionMessages.ERROR));
				} else {
					/*
					 * Código Hash igual, tem q testar a diferenca de tempo pelo
					 * getDateRecoveryCode()
					 */
				}
			} else {
				LOG.info("Wrong recovery to user.id: " + user.getId());
				validator.add(new ValidationMessage(
						ExceptionMessages.USER_DONT_EXISTS,
						ExceptionMessages.ERROR));
			}
		}
		return userFromDB;
	}

}
