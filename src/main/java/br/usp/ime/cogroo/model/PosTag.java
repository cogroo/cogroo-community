package br.usp.ime.cogroo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PosTag {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(unique=true)
	private String posTag;
	
	public PosTag() {
	}

	public PosTag(String posTag) {
		this.posTag = posTag;
	}

	public void setPosTag(String posTag) {
		this.posTag = posTag;
	}

	public String getPosTag() {
		return posTag;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((posTag == null) ? 0 : posTag.hashCode());
		return result;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PosTag other = (PosTag) obj;
		if (posTag == null) {
			if (other.posTag != null)
				return false;
		} else if (!posTag.equals(other.posTag))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return posTag;
	}
}
