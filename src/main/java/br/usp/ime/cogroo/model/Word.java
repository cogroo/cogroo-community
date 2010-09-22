package br.usp.ime.cogroo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Word {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String word;

	// @Column

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "word")
	private List<WordUser> wordUserList = new ArrayList<WordUser>();

	public Word() {
	}

	public Word(String word) {
		// this(word,false);
		this.word = word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		if (word != null)
			return word.hashCode();
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		Word other = (Word) obj;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;

		return true;
	}

	public void setWordUserList(List<WordUser> wordUserList) {
		this.wordUserList = wordUserList;
	}

	public List<WordUser> getWordUserList() {
		return wordUserList;
	}

	@Override
	public String toString() {
		return this.word;
	}

	public boolean isEmpty() {
		return word.length() == 0;
	}

}
