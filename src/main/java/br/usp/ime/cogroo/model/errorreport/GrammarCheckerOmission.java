package br.usp.ime.cogroo.model.errorreport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class GrammarCheckerOmission implements Cloneable{
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String category;
	
	private String customCategory;
	
	private String replaceBy;
	
	@OneToOne
	private ErrorEntry errorEntry;
	
	public GrammarCheckerOmission() {
		
	}

	public GrammarCheckerOmission(String category,
			String customCategory, String replaceBy, ErrorEntry errorEntry) {
		this.category = category;
		this.customCategory = customCategory;
		this.replaceBy = replaceBy;
		this.errorEntry = errorEntry;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCustomCategory() {
		return customCategory;
	}

	public void setCustomCategory(String customCategory) {
		this.customCategory = customCategory;
	}

	public String getReplaceBy() {
		return replaceBy;
	}

	public void setReplaceBy(String replaceBy) {
		this.replaceBy = replaceBy;
	}
	
	public void setErrorEntry(ErrorEntry errorEntry) {
		this.errorEntry = errorEntry;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: " + id + "\n");
		sb.append("category: " + category + "\n");
		sb.append("custom category: " + customCategory + "\n");
		sb.append("replaceBy: " + replaceBy + "\n");
		return sb.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
