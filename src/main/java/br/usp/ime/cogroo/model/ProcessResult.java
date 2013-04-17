package br.usp.ime.cogroo.model;

import java.util.List;

import org.cogroo.entities.Mistake;
import org.cogroo.text.Sentence;


public class ProcessResult {
	
	private String textAnnotatedWithErrors;
	private String syntaxTree;
	private Sentence sentence;
	private List<Mistake> mistakes;

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

	public List<Mistake> getMistakes() {
		return mistakes;
	}

	public void setMistakes(List<Mistake> mistakes) {
		this.mistakes = mistakes;
	}
	
}
