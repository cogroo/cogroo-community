package br.usp.ime.cogroo.model;

import java.util.List;

public class NicePrintDictionaryEntry {
	
	private DictionaryEntry dictionaryEntry;
	private List<String> tagParts;
	
	public NicePrintDictionaryEntry(DictionaryEntry de, List<String> tagParts) {
		dictionaryEntry = de;
		this.tagParts = tagParts;
	}
	
	public DictionaryEntry getDictionaryEntry() {
		return dictionaryEntry;
	}
	
	public Word getLemma() {
		return dictionaryEntry.getLemma();
	}
	
	public PosTag getPosTag() {
		return dictionaryEntry.getPosTag();
	}
	
	public Word getWord() {
		return dictionaryEntry.getWord();
	}
	
	public List<String> getTagParts() {
		return tagParts;
	}
	
}
