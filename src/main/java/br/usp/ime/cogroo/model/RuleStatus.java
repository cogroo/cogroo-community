package br.usp.ime.cogroo.model;

import br.usp.pcs.lta.cogroo.tools.checker.RuleDefinitionI;

public class RuleStatus {
  
  private RuleDefinitionI rule;
  private int tp = 0, fp = 0, fn = 0;
  private boolean active;
  
  public RuleStatus(RuleDefinitionI definitionI) {
    this.rule = definitionI;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public double getPrecision() {
    return tp / (tp + fp);
  }
  
  public double getRecall() {
    return tp / (tp + fn);
  }
  
  public double getFMeasure() {
    return 2 * getPrecision() * getRecall() / (getPrecision() + getRecall());
  }
  
  public RuleDefinitionI getRule() {
    return rule;
  }

  public int getFn() {
    return fn;
  }

  public void setFn(int fn) {
    this.fn = fn;
  }

  public int getTp() {
    return tp;
  }

  public void setTp(int tp) {
    this.tp = tp;
  }

  public int getFp() {
    return fp;
  }

  public void setFp(int fp) {
    this.fp = fp;
  }

}
