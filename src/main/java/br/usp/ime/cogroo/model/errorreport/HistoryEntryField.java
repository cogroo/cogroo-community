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
	
	@Column(length=700)
	private String valueBefore;
	
	@Column(length=700)
	private String valueAfter;
	
  	 @Column(length=700)
  	 private String longValueBefore;
  	    
  	 @Column(length=700)
     private String longValueAfter;
	
	@ManyToOne
	private HistoryEntry historyEntry;
	
	private boolean isFormatted;
	
	public HistoryEntryField() {
	}
	
	public HistoryEntryField(HistoryEntry historyEntry, String fieldName, String before, String after, boolean isFormatted) {
		this.historyEntry = historyEntry;
		this.fieldName = fieldName;
		this.longValueBefore = before;
		this.longValueAfter = after;
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
	  if(longValueBefore == null) {
	    longValueBefore = valueBefore;
	  }
	  return longValueBefore;
	}

	public void setBefore(String before) {
		this.longValueBefore = before;
	}

	public String getAfter() {
      if(longValueAfter == null) {
        longValueAfter = valueAfter;
      }
      return longValueAfter;
	}

	public void setAfter(String after) {
		this.longValueAfter = after;
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
