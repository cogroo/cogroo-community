package br.usp.ime.cogroo.model;

import java.util.List;

public class TypeField {

	private Enum<?> type;
	private List<FieldOptions> fieldOptions;

	public TypeField(Enum<?> aType, List<FieldOptions> fieldOptions) {
		this.type = aType;
		this.fieldOptions = fieldOptions;
	}

	public Enum<?> getType() {
		return type;
	}

	public void setType(Enum<?> type) {
		this.type = type;
	}

	public List<FieldOptions> getFieldOptions() {
		return fieldOptions;
	}

	public void setFieldOptions(List<FieldOptions> fieldOptions) {
		this.fieldOptions = fieldOptions;
	}
}
