package br.usp.ime.cogroo.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.logic.RulesLogic;
import br.usp.ime.cogroo.model.Pair;
import br.usp.ime.cogroo.model.ProcessResult;
import br.usp.ime.cogroo.util.RuleUtils;
import br.usp.pcs.lta.cogroo.tools.checker.RuleDefinitionI;
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
	
	@Deprecated
	@Get
	@Path(value = "/ruleList")
	public void deprecatedRuleList() {
		result.use(Results.status()).movedPermanentlyTo(RuleController.class).ruleList();
	}

	@Get
	@Path("/rules")
	public void ruleList() {
		result.include("ruleList", rulesLogic.getRuleList());
		result.include("headerTitle", "Regras").include(
				"headerDescription", "Exibe as regras utilizadas pelo corretor gramatical CoGrOO para identificar erros.");
	}
	
	@Deprecated
	@Get
	@Path(value = "/rule/{rule.id}")
	public void deprecatedRule(RuleDefinitionI rule) {
		result.use(Results.status()).movedPermanentlyTo(RuleController.class).rule(rule);
	}
	
	@Get
    @Path(value = "/rules/{rule.id}")
    public void rule(RuleDefinitionI rule) {
        
	  if(rule == null) {
            result.redirectTo(getClass()).ruleList();
            return;
        }
	  
	    // this will handle cases where we don't have the prefix
        rule = rulesLogic.getRule(rule.getId());
        if (rule == null) {
            result.notFound();
            return;
        }
        
        List<Pair<Pair<String,List<ProcessResult>>,Pair<String,List<ProcessResult>>>> exampleList =
            new ArrayList<Pair<Pair<String,List<ProcessResult>>,Pair<String,List<ProcessResult>>>>();
        
        for (Example example : rule.getExamples()) {
            
            List<ProcessResult> incorrect = cogroo.cachedProcessText(example.getIncorrect());
            List<ProcessResult> correct = cogroo.cachedProcessText(example.getCorrect());
            
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
            .include("previousRule", rulesLogic.getPreviousRuleID(rule.getId()));
        
        if(rule.isXMLBased()) {
          result.include("pattern", RuleUtils.getPatternAsString(rulesLogic.getXmlRule(rule)))
          .include("replacePattern", RuleUtils.getSuggestionsAsString(rulesLogic.getXmlRule(rule)));
        }
        
        String title = "Regra " + rule.getId() + ": "
                + rule.getShortMessage();
        String description = rule.getMessage();
        result.include("headerTitle", StringEscapeUtils.escapeHtml(title))
                .include("headerDescription",
                        StringEscapeUtils.escapeHtml(description));
    }
}