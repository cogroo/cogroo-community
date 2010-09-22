package br.usp.ime.cogroo.logic;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Gender;

public class EditPosTagLogicTest {
	EditPosTagLogic edit;

	@Before
	public void setUp() {
		edit = new EditPosTagLogic();
	}

	@Test
	public void shouldThereBeAllTypes() {
		Set<Enum<?>> types = edit.getTypes();
		Assert.assertTrue(types.contains(Class.NOUN));
		Assert.assertTrue(types.contains(Class.ADJECTIVE));
		Assert.assertTrue(types.contains(Class.ADVERB));
		Assert.assertTrue(types.contains(Class.COORDINATING_CONJUNCTION));
		Assert.assertTrue(types.contains(Class.DETERMINER));
		Assert.assertTrue(types.contains(Class.HYPHEN_SEPARATED_PREFIX));
		Assert.assertTrue(types.contains(Class.INTERJECTION));
		Assert.assertTrue(types.contains(Class.NUMERAL));
		Assert.assertTrue(types.contains(Class.PERSONAL_PRONOUN));
		Assert.assertTrue(types.contains(Class.PREPOSITION));
		Assert.assertTrue(types.contains(Class.PROPER_NOUN));
		Assert.assertTrue(types.contains(Class.PUNCTUATION_MARK));
		Assert.assertTrue(types.contains(Class.SUBORDINATING_CONJUNCTION));
		Assert.assertTrue(types.contains(Class.UNIT));
		Assert.assertTrue(types.contains(Class.VERB));
		Assert.assertTrue(types.contains(Class.SPECIFIER));
	}

	@Test
	public void shouldReturnCorrectFieldsFromClass() {
		List<String> fields = edit.getFields(Class.NOUN);
		Assert.assertTrue(fields.contains("Genero"));
		Assert.assertTrue(fields.contains("Numero"));
	}

	@Test
	public void shouldReturnCorrectOptionsFromField() {
		String field = "Genero";
		List<String> options = edit.getOptions(field);
		Assert.assertTrue(options.contains(Gender.FEMALE.toString()));
		Assert.assertTrue(options.contains(Gender.MALE.toString()));
	}

	@Test
	public void shouldReturnCorrectGenderFromAStringAndAValue() {
		String genero = "Genero";
		Enum<?> female = edit.getTagMaskEnum(genero, "female");
		Enum<?> male = edit.getTagMaskEnum(genero, "male");
		Assert.assertEquals(Gender.FEMALE, female);
		Assert.assertEquals(Gender.MALE, male);
	}

	@Test
	public void shouldReturnCorrectNumberFromAStringAndAValue() {
		String numero = "Numero";
		Enum<?> singular = edit.getTagMaskEnum(numero, "singular");
		Enum<?> plural = edit.getTagMaskEnum(numero, "plural");
		Assert.assertEquals(TagMask.Number.SINGULAR, singular);
		Assert.assertEquals(TagMask.Number.PLURAL, plural);
	}

	@Test
	public void shouldReturnCorrectPersonFromAStringAndAValue() {
		String numero = "Pessoa";
		Enum<?> first = edit.getTagMaskEnum(numero, "first");
		Enum<?> third = edit.getTagMaskEnum(numero, "third");
		Assert.assertEquals(TagMask.Person.FIRST, first);
		Assert.assertEquals(TagMask.Person.THIRD, third);
	}

	@Test
	public void shouldReturnCorrectTenseFromAStringAndAValue() {
		String tempo = "Tempo";
		Enum<?> future = edit.getTagMaskEnum(tempo, "future");
		Enum<?> present = edit.getTagMaskEnum(tempo, "present");
		Assert.assertEquals(TagMask.Tense.FUTURE, future);
		Assert.assertEquals(TagMask.Tense.PRESENT, present);
	}

	@Test
	public void shouldReturnCorrectMoodFromAStringAndAValue() {
		String mood = "Modo";
		Enum<?> imperative = edit.getTagMaskEnum(mood, "imperative");
		Enum<?> indicative = edit.getTagMaskEnum(mood, "indicative");
		Assert.assertEquals(TagMask.Mood.IMPERATIVE, imperative);
		Assert.assertEquals(TagMask.Mood.INDICATIVE, indicative);
	}

	@Test
	public void shouldReturnCorrectFinitenessFromAStringAndAValue() {
		String formaNominal = "Forma Nominal";
		Enum<?> finite = edit.getTagMaskEnum(formaNominal, "finite");
		Enum<?> participle = edit.getTagMaskEnum(formaNominal, "participle");
		Assert.assertEquals(TagMask.Finiteness.FINITE, finite);
		Assert.assertEquals(TagMask.Finiteness.PARTICIPLE, participle);
	}

	@Test
	public void shouldReturnCorrectPunctuationFromAStringAndAValue() {
		String tipo = "Tipo";
		Enum<?> nsep = edit.getTagMaskEnum(tipo, "nsep");
		Enum<?> bin = edit.getTagMaskEnum(tipo, "bin");
		Assert.assertEquals(TagMask.Punctuation.NSEP, nsep);
		Assert.assertEquals(TagMask.Punctuation.BIN, bin);
	}

	@Test
	public void shouldReturnCorrectCaseFromAStringAndAValue() {
		String caso = "Caso";
		Enum<?> nominative = edit.getTagMaskEnum(caso, "NOMINATIVE");
		Enum<?> prepositive = edit.getTagMaskEnum(caso, "prepositive");
		Enum<?> accusative = edit.getTagMaskEnum(caso, "accusative");
		Enum<?> accusativeDative = edit.getTagMaskEnum(caso, "accusative_dative");
		
		Assert.assertEquals(TagMask.Case.NOMINATIVE, nominative);
		Assert.assertEquals(TagMask.Case.PREPOSITIVE, prepositive);
		Assert.assertEquals(TagMask.Case.ACCUSATIVE, accusative);
		Assert.assertEquals(TagMask.Case.ACCUSATIVE_DATIVE, accusativeDative);
	}

}
