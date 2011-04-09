package br.usp.ime.cogroo.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
	}

	@Get
	@Path("/recover/{email}/{codeRecover}")
	public void verifyCodeRecover(String email, String codeRecover) {
		email = decode(email);
		if(LOG.isDebugEnabled()) {
			LOG.debug("verifyCodeRecover >>>: " + email);
		}
		getUserIfValidate(email, codeRecover);

		validator.onErrorUse(Results.page())
				.of(RecoverPasswordController.class).recover();

		/*
		 * If all is ok, then... redirect to form to create new password.
		 */
		result.include("codeRecover", codeRecover);
		result.include("email", encode(email));
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("<<< verifyCodeRecover");
		}
	}

	@Post
	@Path("/recover/{email}/{codeRecover}")
	public void changePassword(String password, String passwordRepeat,
			String email, String codeRecover) {
		email = decode(email);
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
				+ encode(userFromDB.getEmail()) + "/" + codeRecover;
		StringBuilder body = new StringBuilder();
		body.append("Olá, " + userFromDB.getName() + "!<br><br>");
		body.append("De acordo com sua solicitação no portal CoGrOO Comunidade, enviamos um link para redefinir sua senha:<br>");
		body.append(url + "<br><br>");
		body.append("Lembrando que seu login é \"" + userFromDB.getLogin() + "\".<br>");
		
		String subject = "Redefinição de senha";
		notificator.sendEmail(body.toString(), subject, userFromDB.getEmail().trim());

	}
	
	private String decode(String email) {
		// looks like it is decoded automatically
		// at Tomcat it is missing the '+1
		return email.replaceAll("\\s", "+");
	}
	
	private String encode(String email) {
		try {
			return URLEncoder.encode(email, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("Should never happen with UTF-8",e);
		}
		return null;
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

		// Validators
		if (email.trim().isEmpty() || codeRecover.trim().isEmpty()) {
			LOG.warn("Password recovering with empty email.");
			validator.add(new ValidationMessage(ExceptionMessages.EMPTY_FIELD,
					ExceptionMessages.ERROR));
		} else {
			userFromDB = userDAO.retrieveByEmail(email);
			if (userFromDB != null) {
				if (!userFromDB.getRecoverCode().equals(codeRecover)) {
					LOG.warn("Bad recovery code for email: " + email);
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
				LOG.info("Wrong recovery email: " + email);
				validator.add(new ValidationMessage(
						ExceptionMessages.INVALID_EMAIL,
						ExceptionMessages.ERROR));
			}
		}
		return userFromDB;
	}

}
