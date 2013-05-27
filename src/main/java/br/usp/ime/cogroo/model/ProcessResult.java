package br.usp.ime.cogroo.model;

import java.util.List;
import static br.usp.ime.cogroo.util.Tagset.*;
import org.cogroo.entities.Mistake;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;


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
	
	public String[][] getSentenceAsTable() {
	  
	  String[][] table = new String[sentence.getTokens().size()][6];

      for (int i = 0; i < sentence.getTokens().size(); i++) {
        Token token = sentence.getTokens().get(i);
        table[i][0] = token.getLexeme();
        table[i][1] = comma(token.getLemmas());
        table[i][2] = getPOS(token.getPOSTag(), token.getLexeme());
        table[i][3] = getFeatures(token.getFeatures());
        table[i][4] = getChunk(token.getChunkTag());
        table[i][5] = getClause(token.getSyntacticTag());
      }
      
      return table;
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
