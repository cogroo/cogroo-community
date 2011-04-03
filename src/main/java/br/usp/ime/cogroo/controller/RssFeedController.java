package br.usp.ime.cogroo.controller;

import java.io.File;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.notifiers.Notificator;

@Resource
public class RssFeedController {
	
	private final Result result;
	private Validator validator;
	private Notificator notificator;
	private LoggedUser loggedUser;
	
	public RssFeedController(LoggedUser loggedUser, Result result, Validator validator, Notificator feed) {
		this.result = result;
		this.validator = validator;
		this.notificator = feed;
		this.loggedUser = loggedUser;
	}
    
	@Get
	@Path("/rss.xml")
	public File rss() {
        return this.notificator.getRssFeed();
    }
	
	@Get
	@Path("/rssManager")
	public void rssManager() {
		if(!loggedUser.isLogged() || !loggedUser.getUser().getRole().getCanManageRSS()) {
			validator.add(new ValidationMessage(
					ExceptionMessages.USER_UNAUTHORIZED, ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(IndexController.class)
				.index();
		}
    }
	
	@Post
	@Path("/rss/delete")
	public void rssDelete() {
		if(loggedUser.isLogged() && loggedUser.getUser().getRole().getCanManageRSS()) {
			this.notificator.cleanRssFeed();
			result.redirectTo(getClass()).rssManager();
		} else {
			validator.add(new ValidationMessage(
					ExceptionMessages.USER_UNAUTHORIZED, ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(IndexController.class)
				.index();
		}
    }
}
