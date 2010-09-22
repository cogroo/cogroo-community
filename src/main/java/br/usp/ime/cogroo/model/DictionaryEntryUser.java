	package br.usp.ime.cogroo.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Entity
public class DictionaryEntryUser {

	@EmbeddedId
	private IdFK id = new IdFK();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({
		@JoinColumn(name = "lemma_id", nullable = false, insertable = false, updatable = false),
		@JoinColumn(name = "postag_id", nullable = false, insertable = false, updatable = false),
		@JoinColumn(name = "word_id", nullable = false, insertable = false, updatable = false) })
	private DictionaryEntry dictionaryEntry;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, insertable = false, updatable = false)
	private User user;
	
	private boolean deleted;
	
	public DictionaryEntryUser() {
	}
	
	public DictionaryEntryUser(DictionaryEntry dictionaryEntry, User user) {
		setDictionaryEntry(dictionaryEntry);
		setUser(user);
	}
	
	public void setId(IdFK id) {
		this.id = id;
	}

	public IdFK getId() {
		return id;
	}

	public void setDictionaryEntry(DictionaryEntry dictionaryEntry) {
		this.dictionaryEntry = dictionaryEntry;
		id.lemmaId = dictionaryEntry.getLemma().getId();
		id.posTagId = dictionaryEntry.getPosTag().getId();
		id.wordId = dictionaryEntry.getWord().getId();
	}

	public DictionaryEntry getDictionaryEntry() {
		return dictionaryEntry;
	}


	public void setUser(User user) {
		this.user = user;
		id.userId = user.getId();
	}

	public User getUser() {
		return user;
	}


	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeleted() {
		return deleted;
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
		@Column(name = "user_id")
		private Long userId;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((lemmaId == null) ? 0 : lemmaId.hashCode());
			result = prime * result
					+ ((posTagId == null) ? 0 : posTagId.hashCode());
			result = prime * result
					+ ((userId == null) ? 0 : userId.hashCode());
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
			if (lemmaId == null) {
				if (other.lemmaId != null)
					return false;
			} else if (!lemmaId.equals(other.lemmaId))
				return false;
			if (posTagId == null) {
				if (other.posTagId != null)
					return false;
			} else if (!posTagId.equals(other.posTagId))
				return false;
			if (userId == null) {
				if (other.userId != null)
					return false;
			} else if (!userId.equals(other.userId))
				return false;
			if (wordId == null) {
				if (other.wordId != null)
					return false;
			} else if (!wordId.equals(other.wordId))
				return false;
			return true;
		}


		public IdFK() {
		}


	}

}
