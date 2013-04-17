package br.usp.ime.cogroo.model;

import org.cogroo.tools.checker.RuleDefinition;


public class RuleStatus extends AbstractRuleStatus {
  
  private RuleDefinition rule;
  private boolean active = true;
  
  /* RulesLogic sets the variable's values */
  public RuleStatus(RuleDefinition definitionI) {
    this.rule = definitionI;
  }
  
  public boolean getActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public RuleDefinition getRule() {
    return rule;
  }

}
