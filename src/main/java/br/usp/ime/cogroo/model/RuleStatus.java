package br.usp.ime.cogroo.model;

import br.usp.pcs.lta.cogroo.tools.checker.RuleDefinitionI;

public class RuleStatus extends AbstractRuleStatus {
  
  private RuleDefinitionI rule;
  private boolean active = true;
  
  /* RulesLogic sets the variable's values */
  public RuleStatus(RuleDefinitionI definitionI) {
    this.rule = definitionI;
  }
  
  public boolean getActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public RuleDefinitionI getRule() {
    return rule;
  }

}
