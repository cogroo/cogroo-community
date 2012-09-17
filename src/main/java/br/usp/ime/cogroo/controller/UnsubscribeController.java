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
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.model.User;

@Resource
public class UnsubscribeController {

  private static final Logger LOG = Logger
      .getLogger(UnsubscribeController.class);

  private UserDAO userDAO;
  private final Result result;
  private final Validator validator;

  public UnsubscribeController(Result result, Validator validator,
      UserDAO userDAO) {
    this.result = result;
    this.validator = validator;
    this.userDAO = userDAO;
  }

  @Get
  @Path("/unsubscribe/{user.id}/{codeRecover}")
  public void unsubscribe(User user, String codeRecover) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("verifyCodeRecover for user.id>>>: " + user.getId());
    }
    User userFromDB = validateUser(user, codeRecover);

    validator.onErrorUse(Results.page()).of(LoginController.class)
        .login();

    /*
     * If all is ok, then... redirect to form to create new password.
     */
    result.include("codeRecover", codeRecover);
    result.include("user", userFromDB);

    if (LOG.isDebugEnabled()) {
      LOG.debug("<<< verifyCodeRecover");
    }
  }

  @Post
  @Path("/doUnsubscription")
  public void doUnsubscription(User user, String codeRecover, boolean isReceiveTransactionalMail, boolean isReceiveNewsMail) {
    
    if (LOG.isDebugEnabled()) {
      LOG.warn("Unsubscribing user");
      }
    
    User userFromBD = validateUser(user, codeRecover);
    userFromBD.setIsReceiveEmail(isReceiveTransactionalMail);
    userFromBD.setIsReceiveNewsMail(isReceiveNewsMail);
    
    userDAO.update(userFromBD);
    
    result.include("okMessage", "Preferências de e-mail alteradas com sucesso!");     
    result.include("gaEventMailPreferences", true);
    
    result.redirectTo(IndexController.class).index();
  }
  
  private User validateUser(User user, String codeRecover) {
    User userFromDB = new User();

    // Validators
    if (user.getId() <= 0 || codeRecover.trim().isEmpty()) {
      LOG.warn("Code Recover is empty.");
      validator.add(new ValidationMessage(ExceptionMessages.EMPTY_FIELD,
          ExceptionMessages.ERROR));
    } else {
      userFromDB = userDAO.retrieve(user.getId());
      if (userFromDB != null) {
          LOG.warn(userFromDB.getEmailOptOutCode());
        if (!userFromDB.getEmailOptOutCode().equals(codeRecover)) {
          LOG.warn("Bad recovery code for user: " + user.toString());
          validator.add(new ValidationMessage(
              ExceptionMessages.BAD_OPT_OUT_CODE, ExceptionMessages.ERROR));
        }
      } else {
        LOG.info("Wrong user.id: " + user.getId());
        validator.add(new ValidationMessage(ExceptionMessages.USER_DONT_EXISTS,
            ExceptionMessages.ERROR));
      }
    }
    return userFromDB;
  }

}
