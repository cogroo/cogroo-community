package br.usp.ime.cogroo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.usp.pcs.lta.cogroo.tools.dictionary.LexicalDictionary;
import br.usp.pcs.lta.cogroo.tools.dictionary.PairWordPOSTag;

public class DummyBaseDictionary implements LexicalDictionary {
	
	private Map<String, PairWordPOSTag> dictionary;
	
	public DummyBaseDictionary() {
		this.dictionary = new HashMap<String, PairWordPOSTag>();
		dictionary.put("banana", new PairWordPOSTag("aLemma", "aPOSTag"));
	}
	
	public boolean wordExists(String arg0) {
		return this.dictionary.containsKey(arg0);
	}

	public List<PairWordPOSTag> getLemmasAndPosTagsForWord(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getPOSTagsForWord(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PairWordPOSTag> getWordsAndPosTagsForLemma(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}






}
