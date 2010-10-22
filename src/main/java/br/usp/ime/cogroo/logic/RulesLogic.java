package br.usp.ime.cogroo.logic;

import java.util.List;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.pcs.lta.cogroo.tools.checker.rules.applier.RulesProvider;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;
import br.usp.pcs.lta.cogroo.tools.checker.rules.util.RulesContainerHelper;

@Component
public class RulesLogic {

	public List<Rule> getRuleList() {


		List<Rule> rules = new RulesContainerHelper(getClass().getResource("/")
				.getPath()).getContainerForXMLAccess()
				.getComponent(RulesProvider.class).getRules().getRule();

		return rules;

	}

}
