package br.usp.ime.cogroo.controller;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.usp.ime.cogroo.Messages;
import br.usp.ime.cogroo.Util.RestUtil;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.logic.ErrorReportLogic;
import br.usp.ime.cogroo.logic.SecurityUtil;

@Resource
public class SecurityController {

	private static final Logger LOG = Logger
			.getLogger(SecurityController.class);

	private SecurityUtil securityUtil; 
	private Result result;
	private Validator validator;

	private UserDAO userDAO;
	
	public SecurityController(
			ErrorReportLogic errorReportLogic,
			Result result,
			Validator validator,
			SecurityUtil securityUtil,
			UserDAO userDAO) {
		this.result = result;
		this.validator = validator;
		this.securityUtil = securityUtil;
		this.userDAO = userDAO;
	}
	
	@Post
	@Path("/saveClientSecurityKey")
	public void saveClientSecurityKey(String user, String pubKey) {
		try {
			if(this.userDAO.existe(user)) {
				String key = this.securityUtil.genSecretKeyForUser(this.userDAO.retrieve(user), this.securityUtil.decodeURLSafe(pubKey));
				result.include("encryptedSecretKey", RestUtil.prepareResponse("encryptedSecretKey", key));
			} else {
				LOG.error("Unknown user trying to save security key");
				validator.add(new ValidationMessage("INVALID_USER", Messages.ERROR));
			}
			
		} catch (Throwable e) {
			LOG.error("Got an invalid key from user: " + user, e);
		}

	}
	
	@Post
	@Path("/generateAuthenticationForUser")
	public void generateAuthenticationForUser(String username, String encryptedPassword) {
		try {
			if(this.userDAO.existe(username)) {
				LOG.debug("Will generate token for " + username);
				String token = this.securityUtil.generateAuthenticationTokenForUser(this.userDAO.retrieve(username), securityUtil.decodeURLSafe(encryptedPassword));
				result.include("token", RestUtil.prepareResponse("token",token));
			} else {
				LOG.error("Unknown user trying to authenticate.");
				validator.add(new ValidationMessage("INVALID_USER", Messages.ERROR));
			}
			
		} catch (Exception e) {
			LOG.error("Got an invalid key from user: " + username, e);
		}

	}
	
}
