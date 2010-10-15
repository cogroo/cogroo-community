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
public class IndexController {

	private final Result result;
	private CogrooFacade cogroo;
	
	//TODO Dependência parece ser necessária. Aqui é o melhor lugar?
	private Stats stats;

	public IndexController(Result result, CogrooFacade cogroo, Stats stats) {
		this.result = result;
		this.cogroo = cogroo;
		this.stats = stats;
	}

	@Get
	@Path("/")
	public void index() {
		result.include("totalMembers", stats.getTotalMembers())
				.include("onlineMembers", stats.getOnlineMembers())
				.include("reportedErrors", stats.getReportedErrors());
	}

	@Post
	@Path("/")
	public void index(String texto) {
		if (texto != null && texto.length() > 0) {
			result.include("processResultList", cogroo.processText(texto))
					.include("texto", texto);
		}
	}

	@Get
	@Path("/tree/{texto}")
	public void tree(String texto) {
		System.out.println(texto);
		result.include("texto", texto);
	}
}