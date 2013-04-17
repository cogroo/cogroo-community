package br.usp.ime.cogroo.dao;

import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.interpreters.TagInterpreter;
import org.cogroo.tools.checker.rules.dictionary.CogrooTagDictionary;
import org.cogroo.tools.checker.rules.model.TagMask;
import org.cogroo.tools.checker.rules.model.TagMask.Case;
import org.cogroo.tools.checker.rules.model.TagMask.Gender;
import org.cogroo.tools.checker.rules.model.TagMask.Mood;
import org.cogroo.tools.checker.rules.model.TagMask.Person;
import org.cogroo.tools.checker.rules.model.TagMask.Punctuation;
import org.cogroo.tools.checker.rules.model.TagMask.Tense;
import org.cogroo.tools.checker.rules.model.TagMask.Number;
import org.cogroo.tools.checker.rules.model.TagMask.Class;


public class DummyTagDictionary implements CogrooTagDictionary {

	public boolean exists(String word, boolean cs) {
		return true;
	}

	public String[] getInflectedPrimitive(String primitive, TagMask tagMask,
			boolean cs) {

		return null;
	}

	public String[] getPrimitive(String lexeme, TagMask tagMask, boolean cs) {
		String[] res = {"primitiveA","primitiveB"};
		return res;
	}

	public String[] getPrimitive(String lexeme,
			MorphologicalTag morphologicalTag, boolean cs) {
		String[] res = {"primitiveA","primitiveB"};
		return res;
	}

	public MorphologicalTag[] getTags(String word) {
		MorphologicalTag[] t = {getMophtagA(),getMophtagB()};
		return t;
	}

	public MorphologicalTag[] getTags(String word, boolean cs) {
		MorphologicalTag[] t = {getMophtagA(),getMophtagB()};
		return t;
	}

	public boolean match(String lexeme, TagMask tagMask, boolean cs) {
	  return true;
	}
	
	private MorphologicalTag getMophtagA() {
		MorphologicalTag aa = new MorphologicalTag();
		aa.setCase(Case.ACCUSATIVE);
		aa.setClazz(Class.ADVERB);
		aa.setGender(Gender.MALE);
		aa.setMood(Mood.IMPERATIVE);
		aa.setNumber(Number.SINGULAR);
		aa.setPerson(Person.NONE_FIRST_THIRD);
		aa.setPunctuation(Punctuation.ABS);
		aa.setTense(Tense.CONDITIONAL);
		return aa;
	}
	
	private MorphologicalTag getMophtagB() {
		MorphologicalTag aa = new MorphologicalTag();
		aa.setCase(Case.DATIVE);

		aa.setNumber(Number.PLURAL);
		aa.setPerson(Person.THIRD);
		aa.setTense(Tense.FUTURE);
		return aa;
	}

  @Override
  public TagInterpreter getTagInterpreter() {
    // TODO Auto-generated method stub
    return null;
  }

}
