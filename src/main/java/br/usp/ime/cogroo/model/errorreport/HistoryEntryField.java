package br.usp.ime.cogroo.model.errorreport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class HistoryEntryField {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(length=128)
	private String fieldName;
	
	@Column(length=128)
	private String valueBefore;
	
	@Column(length=128)
	private String valueAfter;
	
	@ManyToOne
	private HistoryEntry historyEntry;
	
	private boolean isFormatted;
	
	public HistoryEntryField() {
	}
	
	public HistoryEntryField(HistoryEntry historyEntry, String fieldName, String before, String after, boolean isFormatted) {
		this.historyEntry = historyEntry;
		this.fieldName = fieldName;
		this.valueBefore = before;
		this.valueAfter = after;
		this.isFormatted = isFormatted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getBefore() {
		return valueBefore;
	}

	public void setBefore(String before) {
		this.valueBefore = before;
	}

	public String getAfter() {
		return valueAfter;
	}

	public void setAfter(String after) {
		this.valueAfter = after;
	}

	public HistoryEntry getHistoryEntry() {
		return historyEntry;
	}

	public void setHistoryEntry(HistoryEntry history) {
		this.historyEntry = history;
	}

	public boolean getIsFormatted() {
		return isFormatted;
	}

	public void setFormatted(boolean isFormatted) {
		this.isFormatted = isFormatted;
	}
	
	@Override
	public String toString() {
		return "Field : [ " + getFieldName() + "] before: [" + getBefore() + "] after: [" + getAfter() + "] formatted: " + getIsFormatted();
	}
}
