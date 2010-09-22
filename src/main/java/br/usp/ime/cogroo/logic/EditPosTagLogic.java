package br.usp.ime.cogroo.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.FieldOptions;
import br.usp.ime.cogroo.model.TypeField;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Case;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Finiteness;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Mood;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Number;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Person;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Punctuation;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Tense;

@Component
public class EditPosTagLogic {

	private HashMap<Enum<?>, List<String>> typeMap;
	private HashMap<String, List<String>> fieldMap;

	private final String genero = "Genero";
	private final String numero = "Numero";
	private final String pessoa = "Pessoa";
	private final String tempo = "Tempo";
	private final String modo = "Modo";
	private final String tipo = "Tipo";
	private final String caso = "Caso";
	private final String formaNominal = "Forma Nominal";

	public EditPosTagLogic() {
		typeMap = new HashMap<Enum<?>, List<String>>();
		fieldMap = new HashMap<String, List<String>>();

		populateTypeMap();
		populateFieldMap();
	}

	private void populateTypeMap() {
		setClassListFields(TagMask.Class.NOUN, genero, numero);
		setClassListFields(TagMask.Class.PROPER_NOUN, genero, numero);
		setClassListFields(TagMask.Class.ADJECTIVE, genero, numero);
		setClassListFields(TagMask.Class.ADVERB, genero, numero);
		setClassListFields(TagMask.Class.DETERMINER, genero, numero);
		setClassListFields(TagMask.Class.NUMERAL, genero, numero);
		setClassListFields(TagMask.Class.SPECIFIER, genero, numero);
		setClassListFields(TagMask.Class.COORDINATING_CONJUNCTION);
		setClassListFields(TagMask.Class.HYPHEN_SEPARATED_PREFIX);
		setClassListFields(TagMask.Class.INTERJECTION);
		setClassListFields(TagMask.Class.PREPOSITION);
		setClassListFields(TagMask.Class.PERSONAL_PRONOUN, genero, numero, caso);
		setClassListFields(TagMask.Class.PUNCTUATION_MARK, tipo);
		setClassListFields(TagMask.Class.SUBORDINATING_CONJUNCTION);
		setClassListFields(TagMask.Class.UNIT);
		setClassListFields(TagMask.Class.VERB, pessoa, numero, tempo, modo,
				formaNominal);
	}

	private void populateFieldMap() {
		setFieldListOptions(genero, Gender.values());
		setFieldListOptions(numero, Number.values());
		setFieldListOptions(pessoa, Person.values());
		setFieldListOptions(tempo, Tense.values());
		setFieldListOptions(modo, Mood.values());
		setFieldListOptions(formaNominal, Finiteness.values());
		setFieldListOptions(tipo, Punctuation.values());
		setFieldListOptions(caso, Case.values());
	}

	private void setClassListFields(Enum<?> key, String... fields) {
		List<String> lista = new ArrayList<String>();
		for (String field : fields) {
			lista.add(field);
		}
		typeMap.put(key, lista);
	}

	private void setFieldListOptions(String field, Enum<?>[] options) {
		List<String> lista = new ArrayList<String>();
		for (Enum<?> option : options) {
			lista.add(option.toString());
		}
		fieldMap.put(field, lista);
	}

	public List<String> getFields(Enum<?> type) {
		return typeMap.get(type);
	}

	public List<String> getOptions(String field) {
		return fieldMap.get(field);
	}

	public Set<Enum<?>> getTypes() {
		return typeMap.keySet();
	}

	public List<TypeField> listTypeFields() {
		List<TypeField> typeFields = new ArrayList<TypeField>();
		List<FieldOptions> fieldOptions;
		List<String> fields = null;
		List<String> options;
		Set<Enum<?>> enums = null;

		enums = getTypes();
		Iterator<Enum<?>> it = enums.iterator();
		while (it.hasNext()) {
			Object element = it.next();

			fieldOptions = new ArrayList<FieldOptions>();
			fields = getFields((Enum<?>) element);
			Iterator<String> its = fields.iterator();
			while (its.hasNext()) {
				Object elements = its.next();
				options = getOptions((String) elements);
				fieldOptions.add(new FieldOptions((String) elements, options));
			}

			typeFields.add(new TypeField((Enum<?>) element, fieldOptions));
		}
		return typeFields;
	}

	public Enum<?> getTagMaskEnum(String field, String value) {
		value = value.toUpperCase();
		Enum<?> tagMaskEnum = null;
		if (field.equals(genero)) {
			tagMaskEnum = TagMask.Gender.valueOf(value);
		} else if (field.equals(numero)) {
			tagMaskEnum = TagMask.Number.valueOf(value);
		} else if (field.equals(pessoa)) {
			tagMaskEnum = TagMask.Person.valueOf(value);
		} else if (field.equals(tempo)) {
			tagMaskEnum = TagMask.Tense.valueOf(value);
		} else if (field.equals(modo)) {
			tagMaskEnum = TagMask.Mood.valueOf(value);
		} else if (field.equals(formaNominal)) {
			tagMaskEnum = TagMask.Finiteness.valueOf(value);
		} else if (field.equals(tipo)) {
			tagMaskEnum = TagMask.Punctuation.valueOf(value);
		} else if (field.equals(caso)) {
			tagMaskEnum = TagMask.Case.valueOf(value);
		}
		return tagMaskEnum;
	}

	public MorphologicalTag getMorphologicalTag(List<String> listaCampos,
			String classe) {
		List<Enum<?>> tagMaskList = new ArrayList<Enum<?>>();
		tagMaskList.add(TagMask.Class.valueOf(classe));
		for (String field : listaCampos) {
			// Each field contains: class-field-value
			String item[] = field.split("-");
			if (item[0].equals(classe)) {
				tagMaskList.add(getTagMaskEnum(item[1], item[2]));
			}
		}
		MorphologicalTag tag = getMorphologicalTag(tagMaskList);
		return tag;
	}

	private MorphologicalTag getMorphologicalTag(List<Enum<?>> tagMaskList) {
		MorphologicalTag m = new MorphologicalTag();
		for (Enum<?> t : tagMaskList) {
			if (t instanceof Class) {
				m.setClazz((Class) t);
			} else if (t instanceof Gender) {
				m.setGender((Gender) t);
			} else if (t instanceof Number) {
				m.setNumber((Number) t);
			} else if (t instanceof Case) {
				m.setCase((Case) t);
			} else if (t instanceof Person) {
				m.setPerson((Person) t);
			} else if (t instanceof Tense) {
				m.setTense((Tense) t);
			} else if (t instanceof Mood) {
				m.setMood((Mood) t);
			} else if (t instanceof Finiteness) {
				m.setFiniteness((Finiteness) t);
			} else if (t instanceof Punctuation) {
				m.setPunctuation((Punctuation) t);
			}
		}

		return m;
	}

}
