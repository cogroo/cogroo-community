	package br.usp.ime.cogroo.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class DictionaryEntry {

	@EmbeddedId
	private IdFK id = new IdFK();

	@ManyToOne
	@JoinColumn(name = "lemma_id", insertable = false, updatable = false)
	private Word lemma;

	@ManyToOne
	@JoinColumn(name = "postag_id", insertable = false, updatable = false)
	private PosTag posTag;

	@ManyToOne
	@JoinColumn(name = "word_id", insertable = false, updatable = false)
	private Word word;
	
	@Column
	private boolean global = false;
	
	public DictionaryEntry() {
	}
	
	public DictionaryEntry(Word word, Word lemma, PosTag posTag, boolean global) {
		setLemma(lemma);
		setPosTag(posTag);
		setWord(word);
		setGlobal(global);
	}
	
	public DictionaryEntry(long wordId, long lemmaId, long posTagId, boolean global) {
		this.id.lemmaId = lemmaId;
		this.id.posTagId = posTagId;
		this.id.wordId = wordId;
		setGlobal(global);
	}
	
	public DictionaryEntry(Word word, Word lemma, PosTag posTag) {
		this(word,lemma,posTag,false);
	}
	
	public DictionaryEntry(long wordId, long lemmaId, long posTagId) {
		this(wordId,lemmaId,posTagId,false);
	}
	

	public void setGlobal(boolean global) {
		this.global = global;
	}

	public boolean isGlobal() {
		return global;
	}

	public Word getLemma() {
		return lemma;
	}

	public void setLemma(Word primitive) {
		this.lemma = primitive;
		id.lemmaId = primitive.getId();
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
		id.wordId = word.getId();
	}

	public PosTag getPosTag() {
		return posTag;
	}

	public void setPosTag(PosTag posTag) {
		this.posTag = posTag;
		id.posTagId = posTag.getId();
	}

	public void setId(IdFK id) {
		this.id = id;
	}

	public IdFK getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hresult = 1;
		hresult = prime * hresult + ((posTag == null) ? 0 : posTag.hashCode());
		hresult = prime * hresult
				+ ((lemma == null) ? 0 : lemma.hashCode());
		hresult = prime * hresult
				+ ((word == null) ? 0 : word.hashCode());
		return hresult;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DictionaryEntry other = (DictionaryEntry) obj;
		if (posTag == null) {
			if (other.posTag != null)
				return false;
		} else if (!posTag.equals(other.posTag))
			return false;
		if (lemma == null) {
			if (other.lemma != null)
				return false;
		} else if (!lemma.equals(other.lemma))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}


	@Embeddable
	public static class IdFK implements Serializable {
		private static final long serialVersionUID = 1L;
		@Column(name = "lemma_id")
		private Long lemmaId;
		@Column(name = "postag_id")
		private Long posTagId;
		@Column(name = "word_id")
		private Long wordId;

		public IdFK() {
		}

		public IdFK(Long wordId, Long lemmaId, Long posTagId) {
			this.posTagId = posTagId;
			this.lemmaId = lemmaId;
			this.wordId = wordId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((posTagId == null) ? 0 : posTagId.hashCode());
			result = prime * result
					+ ((lemmaId == null) ? 0 : lemmaId.hashCode());
			result = prime * result
					+ ((wordId == null) ? 0 : wordId.hashCode());
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
			IdFK other = (IdFK) obj;
			if (posTagId == null) {
				if (other.posTagId != null)
					return false;
			} else if (!posTagId.equals(other.posTagId))
				return false;
			if (lemmaId == null) {
				if (other.lemmaId != null)
					return false;
			} else if (!lemmaId.equals(other.lemmaId))
				return false;
			if (wordId == null) {
				if (other.wordId != null)
					return false;
			} else if (!wordId.equals(other.wordId))
				return false;
			return true;
		}
	}
	
	@Override
	public String toString() {
		String sWord = "";
		String sLemma = "";
		String sPostag = "";
		if(getWord() != null) {
			sWord = getWord().getWord();
		}
		if(getLemma() != null) {
			sLemma = getLemma().getWord();
		}
		if(getPosTag() != null) {
			sPostag = getPosTag().getPosTag();
		}
		return "[Word: " + sWord +"; Lemma: "+ sLemma + "; PosTag: " + sPostag + "; global="+ global + "]";
	}

	public boolean isValid() {
		return !(word.isEmpty() || lemma.isEmpty());
	}

}
