package br.usp.ime.cogroo.model;

public class AbstractRuleStatus {
  
  protected int tp = 0, fp = 0, fn = 0;
  
  public double getPrecision() {
    double sum = tp + fp;
    if (sum > 0)
      return tp / sum;
    return 0;
  }
  
  public double getRecall() {
    double sum = tp + fn;
    if (sum > 0)
      return tp / sum;
    return 0;
  }
  
  public double getFMeasure() {
    double sum = getPrecision() + getRecall();
    if (sum > 0)
      return 2 * getPrecision() * getRecall() / sum;
    return 0;
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
