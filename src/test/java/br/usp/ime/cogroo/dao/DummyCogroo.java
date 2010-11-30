package br.usp.ime.cogroo.dao;

import java.util.ArrayList;
import java.util.List;

import br.usp.pcs.lta.cogroo.entity.Mistake;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.Token;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MistakeImpl;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.TokenCogroo;
import br.usp.pcs.lta.cogroo.grammarchecker.CheckerResult;
import br.usp.pcs.lta.cogroo.grammarchecker.CogrooI;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.dictionary.CogrooTagDictionary;

public class DummyCogroo implements CogrooI {
	
	private CogrooTagDictionary dic = new DummyTagDictionary(); 

	public CheckerResult analyseAndCheckText(String arg0) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		Sentence s0 = new Sentence();
		s0.setSentence("Uma sentença.");
		List<Token> tokens = new ArrayList<Token>();
		
		Token t0 = new TokenCogroo("Uma", 0);
		t0.setPrimitive("um");
		t0.setMorphologicalTag(new MorphologicalTag());
		t0.getMorphologicalTag().setClazz(Class.DETERMINER);
		tokens.add(t0);

		Token t1 = new TokenCogroo("sentença", 0);
		t1.setPrimitive("sentença");
		t1.setMorphologicalTag(new MorphologicalTag());
		t1.getMorphologicalTag().setClazz(Class.NOUN);
		tokens.add(t1);
		
		Token t2 = new TokenCogroo(".", 0);
		t2.setPrimitive(".");
		t2.setMorphologicalTag(new MorphologicalTag());
		t2.getMorphologicalTag().setClazz(Class.PUNCTUATION_MARK);
		tokens.add(t2);
		
		s0.setTokens(tokens);
		
		sentences.add(s0);
		
		
		List<Mistake> mistakes = checkText(arg0);
		
		return new CheckerResult(sentences, mistakes);
	}

	public List<Mistake> checkText(String arg0) {
		List<Mistake> mistakes = new ArrayList<Mistake>();
		
		Mistake m0 = new MistakeImpl(0, "arg1", "arg2", new String[] {"arg3"}, 0, 0, null);
		mistakes.add(m0);
		return mistakes;
	}

	public CogrooTagDictionary getTagDictionary() {
		return dic;
	}

	@Override
	public int checkFirstSentence(String arg0, List<Mistake> arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

}
