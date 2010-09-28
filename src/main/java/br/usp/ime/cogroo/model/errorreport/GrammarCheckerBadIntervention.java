package br.usp.ime.cogroo.model.errorreport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class GrammarCheckerBadIntervention {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private BadInterventionClassification classification;
	
	private int rule;
	
	@OneToOne
	private ErrorEntry errorEntry;
	
	public GrammarCheckerBadIntervention(){
		
	}
	
	public GrammarCheckerBadIntervention(
			BadInterventionClassification classification, int rule, ErrorEntry errorEntry) {
		this.classification = classification;
		this.rule = rule;
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

	public int getRule() {
		return rule;
	}

	public void setRule(int rule) {
		this.rule = rule;
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
}
