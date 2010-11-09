package br.usp.ime.cogroo.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.model.LoggedUser;

/**
 * Today this is the entry point of the web application. It shows a form where a
 * user can enter a text to be analyzed.
 * 
 */
@Resource
public class GrammarController {

	private final Result result;
	private CogrooFacade cogroo;
	private LoggedUser loggedUser;
	
	public GrammarController(Result result, CogrooFacade cogroo, LoggedUser loggedUser) {
		this.result = result;
		this.cogroo = cogroo;
		this.loggedUser = loggedUser;
	}

	@Get
	@Path("/grammar")
	public void grammar() {
	}

	@Post
	@Path("/grammar")
	public void grammar(String text) {
		if (text != null && text.length() > 0) {
			if(text.length() > 255) {
				text = text.substring(0, 255);
			}
			result.include("justAnalyzed", true).include(
					"login",
					loggedUser.isLogged() ? loggedUser.getUser().getLogin()
							: "anonymous");
			
			result.include("processResultList", cogroo.processText(text))
					.include("text", text);
		}
	}

}