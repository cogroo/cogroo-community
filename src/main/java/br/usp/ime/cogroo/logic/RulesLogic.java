package br.usp.ime.cogroo.logic;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.pcs.lta.cogroo.tools.checker.RuleDefinitionI;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;

@Component
@ApplicationScoped
public class RulesLogic {
	
	private TreeMap<String, RuleDefinitionI> ruleMap;
	private TreeMap<String, Rule> xmlRuleMap;
	private CogrooFacade cogrooFacade;
	
	public RulesLogic(CogrooFacade cogrooFacade) {
		this.cogrooFacade = cogrooFacade;
	}
	
	private void init() {
		if(ruleMap == null) {
			SortedSet<RuleDefinitionI> rules = cogrooFacade.getRuleDefinitionList();
			ruleMap = new TreeMap<String, RuleDefinitionI>();
			for (RuleDefinitionI rule : rules) {
				ruleMap.put(rule.getId(), rule);
			}
		}
		

        if(xmlRuleMap == null) {
            List<Rule> rules = cogrooFacade.getXMLRules();
            xmlRuleMap = new TreeMap<String, Rule>();
            for (Rule rule : rules) {
              xmlRuleMap.put("xml:" + Long.toString(rule.getId()), rule);
            }
        }
	}

	public Collection<RuleDefinitionI> getRuleList() {
		init();
		return cogrooFacade.getRuleDefinitionList();
	}
	
	public RuleDefinitionI getRule(String id) {
		init();
		return ruleMap.get(CogrooFacade.addPrefixIfMissing(id));
	}
	
  public String getNextRuleID(String currentRuleID) {

    Entry<String, RuleDefinitionI> entry = ruleMap.higherEntry(currentRuleID);

    if (entry != null) {
      return entry.getKey();
    }

    return null;
  }
	
  public String getPreviousRuleID(String currentRuleID) {

    Entry<String, RuleDefinitionI> entry = ruleMap.lowerEntry(currentRuleID);

    if (entry != null) {
      return entry.getKey();
    }

    return null;
  }

  public Rule getXmlRule(RuleDefinitionI rule) {
    return this.xmlRuleMap.get(rule.getId());
  }

}
