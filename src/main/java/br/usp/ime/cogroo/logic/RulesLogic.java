package br.usp.ime.cogroo.logic;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.pcs.lta.cogroo.tools.checker.rules.applier.RulesProvider;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;
import br.usp.pcs.lta.cogroo.tools.checker.rules.util.RulesContainerHelper;

@Component
public class RulesLogic {
	
	private SortedMap<Long, Rule> ruleMap;
	
	private void init() {
		if(ruleMap == null) {
			List<Rule> rules = new RulesContainerHelper(getClass().getResource("/")
					.getPath()).getContainerForXMLAccess()
					.getComponent(RulesProvider.class).getRules().getRule();
			ruleMap = new TreeMap<Long, Rule>();
			for (Rule rule : rules) {
				ruleMap.put(new Long(rule.getId()), rule);
			}
		}
	}

	public Collection<Rule> getRuleList() {
		init();
		return ruleMap.values();
	}
	
	public Rule getRule(Long id) {
		init();
		return ruleMap.get(id);
	}

}
