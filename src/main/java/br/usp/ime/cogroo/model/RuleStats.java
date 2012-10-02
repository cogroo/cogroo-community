package br.usp.ime.cogroo.model;

import java.util.List;

public class RuleStats extends AbstractRuleStatus {
  
  /* The values of the variables are set here */
  public RuleStats(List<RuleStatus> ruleStatus) {
    
    for (RuleStatus rule : ruleStatus) {
      tp += rule.getTp();
      fp += rule.getFp();
      fn += rule.getFn();
    }
  }
  
}
