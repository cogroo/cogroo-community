package br.usp.ime.cogroo.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.rules.model.Example;
import org.cogroo.tools.checker.rules.model.Rule;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.logic.RulesLogic;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.Pair;
import br.usp.ime.cogroo.model.ProcessResult;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.security.annotations.LoggedIn;
import br.usp.ime.cogroo.util.RuleUtils;

@Resource
public class RuleController {

  private static final Logger LOG = Logger
      .getLogger(RuleController.class);
  
    private LoggedUser loggedUser;
	private final Result result;
	private Validator validator;
	private CogrooFacade cogroo;
	private RulesLogic rulesLogic;

	public RuleController(Result result, Validator validator, CogrooFacade cogroo, RulesLogic rulesLogic, LoggedUser loggedUser) {
		this.result = result;
		this.validator = validator;
		this.cogroo = cogroo;
		this.rulesLogic = rulesLogic;
		this.loggedUser = loggedUser;
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
		result.include("ruleStatusList", rulesLogic.getRuleStatus());
		result.include("headerTitle", "Regras").include(
				"headerDescription", "Exibe as regras utilizadas pelo corretor gramatical CoGrOO para identificar erros.")
				.include("stats", rulesLogic.getStats());
	}
	
	@Get
	@Path("/rulesRefresh")
	@LoggedIn
	public void ruleStatus() {
	  User user = loggedUser.getUser();
	  
	  
	  if (user != null) {
	    if (user.getRole().getCanRefreshStatus()) {
	      result.include("refresh", "R")
	      .include("user", user.getRole().getCanRefreshStatus());
	      LOG.warn("Pode atualizar status das regras.");
	    }
	  }
	  
	  result.redirectTo(this).ruleList();
	  
	}
		
	@Deprecated
	@Get
	@Path(value = "/rule/{ruleID}")
	public void deprecatedRule(String rule) {
		result.use(Results.status()).movedPermanentlyTo(RuleController.class).rule(rule);
	}
	
    @Get
    @Path(value = "/rules/{id}")
    public void rule(String id) {
        
      if(id == null) {
            result.redirectTo(getClass()).ruleList();
            return;
        }
      
        String ruleID = CogrooFacade.addPrefixIfMissing(id);
        
        if(!id.equals(ruleID)) {
          result.use(Results.status()).movedPermanentlyTo(RuleController.class).rule(ruleID);
          return;
        }
	  
        RuleDefinition rule = rulesLogic.getRule(ruleID);
        
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
          
          Rule ruleXML = rulesLogic.getXmlRule(rule);
          
          result.include("pattern", RuleUtils.getPatternAsHTML(ruleXML))
          .include("replacePattern", RuleUtils.getSuggestionsAsHTML(ruleXML))
          .include("active", ruleXML.isActive())
          .include("method", ruleXML.getMethod());
        }
        
        String title = "Regra " + rule.getId() + ": "
                + rule.getShortMessage();
        String description = rule.getMessage();
        result.include("headerTitle", StringEscapeUtils.escapeHtml(title))
                .include("headerDescription",
                        StringEscapeUtils.escapeHtml(description));
    }
}