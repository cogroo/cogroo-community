package br.usp.ime.cogroo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class GrammarCheckerVersion {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String version;
	
	public GrammarCheckerVersion() {
	}
	
	public GrammarCheckerVersion(String version) {
		super();
		this.version = version;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return "version: " + getVersion();
	}
	
}
