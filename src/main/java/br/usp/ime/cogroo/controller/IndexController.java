package br.usp.ime.cogroo.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.logic.RulesLogic;
import br.usp.ime.cogroo.logic.errorreport.ErrorEntryLogic;
import br.usp.ime.cogroo.model.ApplicationData;

/**
 * Today this is the entry point of the web application. It shows a form where a
 * user can enter a text to be analyzed.
 * 
 */
@Resource
public class IndexController {

	private final Result result;
	private ErrorEntryLogic errorEntryLogic;
	private RulesLogic rulesLogic;
	private ApplicationData appData;

	public IndexController(Result result, ErrorEntryLogic errorEntryLogic,
			RulesLogic rulesLogic, ApplicationData appData) {
		this.result = result;
		this.errorEntryLogic = errorEntryLogic;
		this.rulesLogic = rulesLogic;
		this.appData = appData;
	}

	@Get
	@Path("/")
	public void index() {
	}

	@Post
	@Path("/")
	public void index(String text) {
		result.include("text", text).
				redirectTo(GrammarController.class).grammar(text);
	}
	
	@Get
	@Path("/about")
	public void about() {
		result.include("appData", appData);
		
		result.include("headerTitle", "Sobre");
	}
	
	@Get
	@Path("/development")
	public void development() {
		result.include("appData", appData);
		
		result.include("headerTitle", "Desenvolvimento");
	}
	
	@Get
	@Path("/sitemap")
	public void sitemap() {
		result.include("errorEntryList", errorEntryLogic.getAllReports());
		result.include("ruleList", rulesLogic.getRuleList());
		
		result.include("headerTitle", "Mapa do site").include("headerDescription",
						"Acesse o mapa do site para encontrar rapidamente a página que você procura.");
	}

}