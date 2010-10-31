package br.usp.ime.cogroo.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.logic.RulesLogic;
import br.usp.ime.cogroo.model.Pair;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Example;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;

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

	public RuleController(Result result, CogrooFacade cogroo, RulesLogic rulesLogic) {
		this.result = result;
		this.cogroo = cogroo;
		this.rulesLogic = rulesLogic;
	}

	@Get
	@Path("/ruleList")
	public void ruleList() {
		result.include("ruleList", rulesLogic.getRuleList());
	}
	
	@Get
	@Path("/rule")
	public void rule(Rule rule) {
		if(rule == null) {
			result.redirectTo(getClass()).ruleList();
			return;
		}
		rule = rulesLogic.getRule(rule.getId());
		List<Pair<String,String>> exampleList = new ArrayList<Pair<String,String>>();
		for (Example example : rule.getExample()) {
			
			exampleList.add(new Pair<String,String>(
					cogroo.getAnnotatedText(
							example.getCorrect(), 
							cogroo.processText(example.getCorrect())), 
					cogroo.getAnnotatedText(
							example.getIncorrect(), 
							cogroo.processText(example.getIncorrect())	
					)));
		}
		result.include("rule", rule)
			.include("exampleList", exampleList);
	}
}