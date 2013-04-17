package br.usp.ime.cogroo.dao;

import java.util.ArrayList;
import java.util.List;

import org.cogroo.checker.CheckAnalyzer;
import org.cogroo.checker.CheckDocument;
import org.cogroo.entities.Mistake;
import org.cogroo.entities.impl.MistakeImpl;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.text.impl.SentenceImpl;
import org.cogroo.text.impl.TokenImpl;


public class DummyCogroo implements CheckAnalyzer {
	
  
  public void analyze(CheckDocument document) {
        
		List<Sentence> sentences = new ArrayList<Sentence>();
		Sentence s0 = new SentenceImpl(0, 10, document);
		List<Token> tokens = new ArrayList<Token>();
		
		String[] lemma0 = {"um"};
		Token t0 = new TokenImpl(0, 5, "Uma", lemma0, "det", "f s");
		tokens.add(t0);

		String[] lemma1 = {"sentença"};
		Token t1 = new TokenImpl(5, 13, "Sentença", lemma1, "n", "f s");
		tokens.add(t1);
		
		String[] lemma2 = {"."};
        Token t2 = new TokenImpl(13, 15, ".", lemma2, "punc", "f s");
        tokens.add(t2);
		
		s0.setTokens(tokens);
		
		sentences.add(s0);
		
		List<Mistake> mistakes = new ArrayList<Mistake>();
        Mistake m0 = new MistakeImpl("xml:2", 0, "arg1", "arg2", new String[] {"arg3"}, 0, 0, null, "");
        mistakes.add(m0);
        
        document.setMistakes(mistakes);
	}

}
