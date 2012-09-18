package br.usp.ime.cogroo.model.errorreport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.google.common.base.Strings;

@Entity
public class GrammarCheckerBadIntervention implements Cloneable {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private BadInterventionClassification classification;
	
	private int rule;
	
	private String ruleID;
	
	@OneToOne
	private ErrorEntry errorEntry;
	
	public GrammarCheckerBadIntervention(){
		
	}
	
	public GrammarCheckerBadIntervention(
        BadInterventionClassification classification, String ruleID, ErrorEntry errorEntry) {
    this.classification = classification;
    this.ruleID = ruleID;
    this.errorEntry = errorEntry;
}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BadInterventionClassification getClassification() {
		return classification;
	}

	public void setClassification(BadInterventionClassification classification) {
		this.classification = classification;
	}

	public String getRule() {
	  
	  if (Strings.isNullOrEmpty(ruleID)) {
	    this.ruleID = "xml:" + rule;
	  }
	  
		return ruleID;
	}

	public void setRule(String ruleID) {
		this.ruleID = ruleID;
	}

	public ErrorEntry getErrorEntry() {
		return errorEntry;
	}

	public void setErrorEntry(ErrorEntry errorEntry) {
		this.errorEntry = errorEntry;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: " + id + "\n");
		sb.append("classification: " + classification + "\n");
		sb.append("rule: " + rule + "\n");
		return sb.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
