package br.usp.ime.cogroo.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.logic.Stats;

/**
 * Today this is the entry point of the web application. It shows a form where a
 * user can enter a text to be analyzed.
 * 
 */
@Resource
public class GrammarController {

	private final Result result;
	private CogrooFacade cogroo;
	
	//TODO Dependência parece ser necessária. Aqui é o melhor lugar?
	private Stats stats;

	public GrammarController(Result result, CogrooFacade cogroo, Stats stats) {
		this.result = result;
		this.cogroo = cogroo;
		this.stats = stats;
	}

	@Get
	@Path("/grammar")
	public void grammar() {
		result.include("totalMembers", stats.getTotalMembers())
				.include("onlineMembers", stats.getOnlineMembers())
				.include("reportedErrors", stats.getReportedErrors());
	}

	@Post
	@Path("/grammar")
	public void grammar(String text) {
		if (text != null && text.length() > 0) {
			if(text.length() > 1024) {
				text = text.substring(0, 1024);
			}
			result.include("processResultList", cogroo.processText(text))
					.include("text", text);
		}
	}

}