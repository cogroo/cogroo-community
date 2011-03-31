package br.usp.ime.cogroo.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * Today this is the entry point of the web application. It shows a form where a
 * user can enter a text to be analyzed.
 * 
 */
@Resource
public class IndexController {

	private final Result result;

	public IndexController(Result result) {
		this.result = result;
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
		result.include("headerTitle", "Sobre");
	}

}