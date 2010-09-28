package br.usp.ime.cogroo.model;

import br.usp.pcs.lta.cogroo.entity.Sentence;

public class ProcessResult {
	
	private String textAnnotatedWithErrors;
	private String syntaxTree;
	private Sentence sentence;

	public String getTextAnnotatedWithErrors() {
		return textAnnotatedWithErrors;
	}

	public void setTextAnnotatedWithErrors(String textAnnotatedWithErrors) {
		this.textAnnotatedWithErrors = textAnnotatedWithErrors;
	}

	public String getSyntaxTree() {
		return syntaxTree;
	}

	public void setSyntaxTree(String syntaxTree) {
		this.syntaxTree = syntaxTree;
	}

	public Sentence getSentence() {
		return sentence;
	}

	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}
	
}
