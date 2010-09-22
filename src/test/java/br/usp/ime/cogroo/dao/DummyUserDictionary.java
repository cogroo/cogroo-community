package br.usp.ime.cogroo.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.usp.pcs.lta.cogroo.tools.dictionary.LexicalDictionary;
import br.usp.pcs.lta.cogroo.tools.dictionary.PairWordPOSTag;

public class DummyUserDictionary implements LexicalDictionary {
	
	private Map<String, List<PairWordPOSTag>> dictionary;
	
	public DummyUserDictionary() {
		this.dictionary = new HashMap<String, List<PairWordPOSTag>>();
		
		List<PairWordPOSTag> apple = new ArrayList<PairWordPOSTag>();
		apple.add(new PairWordPOSTag("aLemma", "aPOSTag"));
		dictionary.put("apple", apple);
		
		List<PairWordPOSTag> casas = new ArrayList<PairWordPOSTag>();
		casas.add(new PairWordPOSTag("casa", "noun,female,plural"));
		casas.add(new PairWordPOSTag("casar", "verb,singular,second,present,indicative,finite"));
		
		dictionary.put("casas", casas);
		
		

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
