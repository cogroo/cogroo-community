package br.usp.ime.cogroo.controller;

import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

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
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.util.CriptoUtils;
import br.usp.ime.cogroo.util.EmailSender;

@Resource
public class RecoverPasswordController {
	
	private static final Logger LOG = Logger.getLogger(RecoverPasswordController.class);

	private final Result result;
	private UserDAO userDAO;
	private Validator validator;
	// private static final Logger LOG = Logger
	// .getLogger(RecoverPasswordController.class);
	Random random = new Random(System.currentTimeMillis());
	private final HttpServletRequest request;

	public RecoverPasswordController(Result result, UserDAO userDAO,
			Validator validator, HttpServletRequest request) {
		this.result = result;
		this.userDAO = userDAO;
		this.validator = validator;
		this.request = request;
	}

	@Get
	@Path("/recover")
	public void recover() {
	}

	@Get
	@Path("/recover/{email}/{codeRecover}")
	public void verifyCodeRecover(String email, String codeRecover) {
		getUserIfValidate(email, codeRecover);

		validator.onErrorUse(Results.page())
				.of(RecoverPasswordController.class).recover();

		/*
		 * If all is ok, then... redirect to form to create new password.
		 */
		result.include("codeRecover", codeRecover);
		result.include("email", email);
	}

	@Post
	@Path("/recover/{email}/{codeRecover}")
	public void changePassword(String password, String passwordRepeat,
			String email, String codeRecover) {
		User userFromDB = getUserIfValidate(email, codeRecover);

		if (email.trim().isEmpty() || email.trim().isEmpty()) {
			validator.add(new ValidationMessage(ExceptionMessages.EMPTY_FIELD,
					ExceptionMessages.ERROR));
		}
		if (!password.equals(passwordRepeat)) {
			validator.add(new ValidationMessage(
					ExceptionMessages.USER_REPEAT_PASSWORD_WRONG,
					ExceptionMessages.ERROR));
		}

		validator.onErrorUse(Results.page())
				.of(RecoverPasswordController.class).verifyCodeRecover(email,
						codeRecover);

		/*
		 * If all is ok, then... redirect to form COMPLETED
		 */
		userFromDB.setPassword(CriptoUtils.digestMD5(userFromDB.getLogin(),
				password));
		userDAO.update(userFromDB);

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
			userFromDB = userDAO.retrieveByEmail(email);
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
				+ userFromDB.getEmail() + "/" + codeRecover;
		String body = "Clicar no link " + url;
		String subject = "Recuperação de senha do CoGrOO Comunidade.";
		try {
			EmailSender.sendEmail(body, subject, userFromDB.getEmail().trim());
		} catch (EmailException e) {
			LOG.fatal("Error recovering password.", e);
		}

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

	private User getUserIfValidate(String email, String codeRecover) {
		User userFromDB = new User();
		/*
		 * Validators
		 */
		if (email.trim().isEmpty() || codeRecover.trim().isEmpty()) {
			validator.add(new ValidationMessage(ExceptionMessages.EMPTY_FIELD,
					ExceptionMessages.ERROR));
		} else {
			userFromDB = userDAO.retrieveByEmail(email);
			if (userFromDB != null) {
				if (!userFromDB.getRecoverCode().equals(codeRecover)) {
					validator.add(new ValidationMessage(
							"Codigo não confere, link inexistente !",
							ExceptionMessages.ERROR));
				} else {
					/*
					 * Código Hash igual, tem q testar a diferenca de tempo pelo
					 * getDateRecoveryCode()
					 */
				}
			} else {
				validator.add(new ValidationMessage(
						ExceptionMessages.INVALID_EMAIL,
						ExceptionMessages.ERROR));
			}
		}
		return userFromDB;
	}

}
