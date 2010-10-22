package br.usp.ime.cogroo.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.logic.RulesLogic;
import br.usp.ime.cogroo.logic.Stats;

/**
 * Today this is the entry point of the web application. It shows a form where a
 * user can enter a text to be analyzed.
 * 
 */
@Resource
public class RuleController {

	private final Result result;
	private CogrooFacade cogroo;
	private RulesLogic rulesLogic;
	
	//TODO Dependência parece ser necessária. Aqui é o melhor lugar?
	private Stats stats;

	public RuleController(Result result, CogrooFacade cogroo, Stats stats, RulesLogic rulesLogic) {
		this.result = result;
		this.cogroo = cogroo;
		this.stats = stats;
		this.rulesLogic = rulesLogic;
	}

	@Get
	@Path("/ruleList")
	public void ruleList() {
		result.include("ruleList", rulesLogic.getRuleList());
	}
}