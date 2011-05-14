package br.usp.ime.cogroo.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.logic.TextSanitizer;
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
	private TextSanitizer sanitizer;
	
	private static final ResourceBundle messages =
	      ResourceBundle.getBundle("messages", new Locale("pt_BR"));
	
	public GrammarController(Result result, CogrooFacade cogroo, LoggedUser loggedUser, TextSanitizer sanitizer) {
		this.result = result;
		this.cogroo = cogroo;
		this.loggedUser = loggedUser;
		this.sanitizer = sanitizer;
	}

	@Get
	@Path("/grammar")
	public void grammar() {
		if (!result.included().containsKey("text"))
			result.include("text", "Isso são um exemplo de erro gramaticais.");
		result.include("headerTitle", messages.getString("GRAMMAR_HEADER"))
				.include("headerDescription",
						messages.getString("GRAMMAR_DESCRIPTION"));
	}
	
	@Get
	@Path("/grammar/{text}")
	public void grammarGET(String text) {
		if (text != null) {
			grammar(text);
			result.redirectTo(getClass()).grammar();
			return;
		}
	}

	@Post
	@Path("/grammar")
	public void grammar(String text) {
		text = sanitizer.sanitize(text, false, true);
		if (text != null && text.length() > 0) {
			if(text.length() > 255) {
				text = text.substring(0, 255);
			}
			if (loggedUser.isLogged())
				result.include("justAnalyzed", true)
						.include("service", loggedUser.getUser().getService())
						.include("login", loggedUser.getUser().getLogin());
			else
				result.include("justAnalyzed", true)
						.include("service", "no service")
						.include("login", "anonymous");
			
			
			result.include("processResultList", cogroo.processText(text))
					.include("text", text);
		}
		result.include("headerTitle", messages.getString("GRAMMAR_HEADER"))
				.include("headerDescription",
						messages.getString("GRAMMAR_DESCRIPTION"));
	}

}