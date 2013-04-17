package br.usp.ime.cogroo.model;

import org.cogroo.entities.Mistake;


public class SingleGrammarError {
	
	private String annotatedText;
	private Mistake mistake;
	
	public SingleGrammarError(String annotatedText, Mistake mistake) {
		this.annotatedText = annotatedText;
		this.mistake = mistake;
	}

	public String getAnnotatedText() {
		return annotatedText;
	}

	public void setAnnotatedText(String annotatedText) {
		this.annotatedText = annotatedText;
	}

	public Mistake getMistake() {
		return mistake;
	}

	public void setMistake(Mistake mistake) {
		this.mistake = mistake;
	}

	
}
