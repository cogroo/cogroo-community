package br.usp.ime.cogroo.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.logic.RulesLogic;
import br.usp.ime.cogroo.model.Pair;
import br.usp.ime.cogroo.model.ProcessResult;
import br.usp.ime.cogroo.util.RuleUtils;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Example;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;

@Resource
public class RuleController {

	private final Result result;
	private Validator validator;
	private CogrooFacade cogroo;
	private RulesLogic rulesLogic;

	public RuleController(Result result, Validator validator, CogrooFacade cogroo, RulesLogic rulesLogic) {
		this.result = result;
		this.validator = validator;
		this.cogroo = cogroo;
		this.rulesLogic = rulesLogic;
	}

	@Get
	@Path("/ruleList")
	public void ruleList() {
		result.include("ruleList", rulesLogic.getRuleList());
	}
	
	@Get
	@Path(value = "/rule/{rule.id}")
	public void rule(Rule rule) {
		if(rule == null) {
			result.redirectTo(getClass()).ruleList();
			return;
		}
		rule = rulesLogic.getRule(rule.getId());
		if (rule == null) {
			validator.add(new ValidationMessage(ExceptionMessages.PAGE_NOT_FOUND,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic())
					.redirectTo(RuleController.class).ruleList();
		}
		
		List<Pair<Pair<String,List<ProcessResult>>,Pair<String,List<ProcessResult>>>> exampleList =
			new ArrayList<Pair<Pair<String,List<ProcessResult>>,Pair<String,List<ProcessResult>>>>();
		
		for (Example example : rule.getExample()) {
			
			List<ProcessResult> incorrect = cogroo.processText(example.getIncorrect());
			List<ProcessResult> correct = cogroo.processText(example.getCorrect());
			
			String incorrectStr = cogroo.getAnnotatedText(example.getIncorrect(), incorrect);
			String correctStr = cogroo.getAnnotatedText(example.getCorrect(), correct);
			
			Pair<String,List<ProcessResult>> incorrectPair = new Pair<String, List<ProcessResult>>(incorrectStr, incorrect);
			Pair<String,List<ProcessResult>> correctPair = new Pair<String, List<ProcessResult>>(correctStr, correct);
			
			Pair<Pair<String,List<ProcessResult>>,Pair<String,List<ProcessResult>>> examplePair = 
				new Pair<Pair<String,List<ProcessResult>>, Pair<String,List<ProcessResult>>>(incorrectPair, correctPair);
			
			exampleList.add(examplePair);
		}
		
		result.include("rule", rule)
			.include("exampleList", exampleList)
			.include("nextRule", rulesLogic.getNextRuleID(rule.getId()))
			.include("previousRule", rulesLogic.getPreviousRuleID(rule.getId()))
			.include("pattern", RuleUtils.getPatternAsString(rule))
			.include("replacePattern", RuleUtils.getSuggestionsAsString(rule));
	}
}