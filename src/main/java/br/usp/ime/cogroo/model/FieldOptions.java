package br.usp.ime.cogroo.model;

import java.util.List;

public class FieldOptions {
	private String field;
	private List<String> options;
	
	public FieldOptions(String aField, List<String> options) {
		this.field = aField;
		this.options = options;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}
}
