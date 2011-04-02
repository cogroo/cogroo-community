package br.usp.ime.cogroo.controller;

import java.io.File;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.logic.RssFeed;
import br.usp.ime.cogroo.logic.errorreport.ErrorEntryLogic;
import br.usp.ime.cogroo.model.LoggedUser;

@Resource
public class RssFeedController {
	
	private final Result result;
	private Validator validator;
	private RssFeed feed;
	private LoggedUser loggedUser;
	
	private static final Logger LOG = Logger.getLogger(RssFeedController.class);

	public RssFeedController(LoggedUser loggedUser, Result result, Validator validator, RssFeed feed) {
		this.result = result;
		this.validator = validator;
		this.feed = feed;
		this.loggedUser = loggedUser;
	}
    
	@Get
	@Path("/rss.xml")
	public File rss() {
        return this.feed.getFeedFile();
    }
	
	@Get
	@Path("/twitter.xml")
	public File twitter() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Requested twitter.xml");
		}
        return this.feed.getTwitterFile();
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
			this.feed.clean();
			result.redirectTo(getClass()).rssManager();
		} else {
			validator.add(new ValidationMessage(
					ExceptionMessages.USER_UNAUTHORIZED, ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(IndexController.class)
				.index();
		}
    }
}
