package br.usp.ime.cogroo.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.interpreters.FlorestaTagInterpreter;
import org.cogroo.interpreters.TagInterpreter;
import org.cogroo.tools.checker.rules.dictionary.LexicalDictionary;
import org.cogroo.util.PairWordPOSTag;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.dao.DictionaryEntryDAO;
import br.usp.ime.cogroo.dao.DictionaryEntryUserDAO;
import br.usp.ime.cogroo.dao.PosTagDAO;
import br.usp.ime.cogroo.dao.WordDAO;
import br.usp.ime.cogroo.model.DictionaryEntry;
import br.usp.ime.cogroo.model.DictionaryEntryUser;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.NicePrintDictionaryEntry;
import br.usp.ime.cogroo.model.PosTag;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.Word;

/**
 * Class to manage dictionary logics, like user restrictions to dictionaries.
 */
@Component
public class DictionaryManager implements LexicalDictionary {

	private static final Logger LOG = Logger.getLogger(DictionaryManager.class);

	private DictionaryEntryDAO dictionaryEntryDAO;
	private DictionaryEntryUserDAO dictionaryEntryUserDAO;
	private User user;
	private WordDAO wordDAO;
	private PosTagDAO postagDAO;
	
	private TagInterpreter ti;

	public DictionaryManager(LoggedUser loggedUser,
			DictionaryEntryDAO dictionaryEntryDAO,
			DictionaryEntryUserDAO dictionaryEntryUserDAO, WordDAO wordDAO,
			PosTagDAO postagDAO) {
		this.dictionaryEntryDAO = dictionaryEntryDAO;
		this.dictionaryEntryUserDAO = dictionaryEntryUserDAO;
		this.user = loggedUser.getUser();
		this.wordDAO = wordDAO;
		this.postagDAO = postagDAO;
		
		ti = new FlorestaTagInterpreter(); //TODO:arrumar
	}

	// ********************************************************
	// Methods related to Web interface
	// ********************************************************
	
	public List<NicePrintDictionaryEntry> listDictionaryEntries() {
		List<NicePrintDictionaryEntry> ret = new ArrayList<NicePrintDictionaryEntry>();

		if (this.user != null) {
			LOG.debug("Will get list of entries for user: " + user);
			dictionaryEntryUserDAO.find(user);
			
			for (DictionaryEntryUser dictionaryEntryUser : dictionaryEntryUserDAO.find(user)) {
				boolean shouldAdd = true;
				if (dictionaryEntryUser.isDeleted()) {
					LOG.debug("User has deleted the entry: " + dictionaryEntryUser);
					shouldAdd = false;
				}
				if (shouldAdd) {
					ret.add(generateNicePrint(dictionaryEntryUser.getDictionaryEntry()));
				}
			}
		}

		return ret;
	}
	
	public List<NicePrintDictionaryEntry> listDictionaryEntriesForUser() {
		List<NicePrintDictionaryEntry> ret = new ArrayList<NicePrintDictionaryEntry>();

		if (this.user != null) {
			LOG.debug("Will get list of entries for user: " + user);
			for (DictionaryEntryUser deUser : dictionaryEntryUserDAO.find(user)) {
				DictionaryEntry dictionaryEntry = deUser.getDictionaryEntry();
				boolean shouldAdd = true;
				if (deUser != null && deUser.isDeleted()) {
					LOG.debug("User has deleted the entry: " + dictionaryEntry);
					shouldAdd = false;
				}
				if (shouldAdd) {
					ret.add(generateNicePrint(dictionaryEntry));
				}
			}
		} 

		return ret;
	}

	public void add(DictionaryEntry dictionaryEntry) throws Exception {
		// TODO: check if exists for user
		if (this.user == null) {
			LOG.info("Only logged users can create entry. Will create dummy user");

			// TODO: throw error
			throw new Exception("Only logged user can create an entry.");
			//return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Add new dictionary entry: " + dictionaryEntry);
		}

		// get the word, if it exists, use it. If not, should create it
		Word word = this.wordDAO.retrieve(dictionaryEntry.getWord().getWord());

		if (word == null) {
			word = new Word(dictionaryEntry.getWord().getWord());
			LOG.info("Will add new word: " + word);
			// add the new word
			this.wordDAO.add(word);
		}

		// get primitive from DAO. It is mandatory.
		Word lemma = this.wordDAO
				.retrieve(dictionaryEntry.getLemma().getWord());
		if (lemma == null) {
			LOG.warn("The primitive should exist: "
					+ dictionaryEntry.getLemma().getWord());
			throw new Exception("O lema deveria existir: "
				+ dictionaryEntry.getLemma().getWord());
		}

		// get the pos tag
		PosTag posTag = this.postagDAO.retrieve(dictionaryEntry.getPosTag()
				.getPosTag());
		if (posTag == null) {
			LOG.warn("The posTag should exist: "
					+ dictionaryEntry.getLemma().getWord());
			throw new Exception("A etiqueta morfológica é inválida: "
				+ dictionaryEntry.getPosTag().getPosTag());
		}

		DictionaryEntry dictionaryEntryToPersist = this.dictionaryEntryDAO
				.retrieve(word, lemma, posTag);
		if (dictionaryEntryToPersist == null) {
			dictionaryEntryToPersist = new DictionaryEntry(
					word, lemma, posTag);
			dictionaryEntryToPersist.setGlobal(false); // only user can adds
			LOG.info("Will add new dictionary entry: " + dictionaryEntryToPersist);
			dictionaryEntryDAO.add(dictionaryEntryToPersist);
		}
		
		DictionaryEntryUser dictionaryEntryUser = this.dictionaryEntryUserDAO
			.find(dictionaryEntryToPersist, user);
		
		if(dictionaryEntryUser == null) {
			dictionaryEntryUser = new DictionaryEntryUser(
					dictionaryEntryToPersist, user);
			this.dictionaryEntryUserDAO.add(dictionaryEntryUser);
			LOG.info("New dictionaryEntryUser added.");
		} else {
			if(dictionaryEntryUser.isDeleted()) {
				if(dictionaryEntryToPersist.isGlobal()) {
					// delete dictionaryEntryUser
					this.dictionaryEntryUserDAO.delete(dictionaryEntryUser);
					
				} else {
					// the user added the entry, should set it global
					dictionaryEntryUser.setDeleted(false);
				}
			}
		}
		
	}
	
	public List<NicePrintDictionaryEntry> searchWordAndLemma(String word) {
		List<NicePrintDictionaryEntry> listOfEntries = new ArrayList<NicePrintDictionaryEntry>();

		Set<DictionaryEntry> unfilteredEntries = new HashSet<DictionaryEntry>(dictionaryEntryDAO.listByWord(word));
		unfilteredEntries.addAll(dictionaryEntryDAO.listByLemma(word));
		if (this.user != null) {
			
			for (DictionaryEntry dictionaryEntry : unfilteredEntries) {
				DictionaryEntryUser deUser = dictionaryEntryUserDAO.find(
						dictionaryEntry, this.user);
				boolean shouldAdd = true;
				if (deUser != null && deUser.isDeleted()) {
					shouldAdd = false;
				}
				if (deUser == null && !dictionaryEntry.isGlobal()) {
					shouldAdd = false;
				}
				if (shouldAdd) {
					listOfEntries.add(generateNicePrint(dictionaryEntry));
				}
			}
		} else {
			// should only get data marked as global
			for (DictionaryEntry dictionaryEntry : unfilteredEntries) {
				if (dictionaryEntry.isGlobal()) {
					listOfEntries.add(generateNicePrint(dictionaryEntry));
				}
			}
		}
		return listOfEntries;
	}

	public void delete(DictionaryEntry dictionaryEntry)
			throws Exception {
		
		Word word = wordDAO.retrieve(dictionaryEntry.getWord().getWord());
		Word lemma = wordDAO.retrieve(dictionaryEntry.getLemma().getWord());
		PosTag tag = postagDAO.retrieve(dictionaryEntry.getPosTag().getPosTag());
		DictionaryEntry persistedDictionaryEntry = dictionaryEntryDAO.retrieve(
				word, 
				lemma, 
				tag);

		
		if(this.user != null) {
			DictionaryEntryUser dictionaryEntryUser = dictionaryEntryUserDAO.find(persistedDictionaryEntry, this.user);
			if (dictionaryEntryUser == null) {
				dictionaryEntryUser = new DictionaryEntryUser(persistedDictionaryEntry, this.user);
				dictionaryEntryUser.setDeleted(true);
				dictionaryEntryUserDAO.add(dictionaryEntryUser);
			} else {
				dictionaryEntryUserDAO.delete(dictionaryEntryUser);
				if(dictionaryEntryUserDAO.findUsers(persistedDictionaryEntry).size() == 0) {
					dictionaryEntryDAO.delete(dictionaryEntryUser.getDictionaryEntry());
				}
				
			}
		} else {
			throw new Exception("Apenas usuário logado pode deletar.");
		}
		
		
		
//		if (dictionaryEntryUser.getDictionaryEntry().isGlobal()) {
//			dictionaryEntryUser.setDeleted(true);
//		} 
//		else {
//			if (dictionaryEntryUserDAO.findUsers(dictionaryEntryUser.getDictionaryEntry()).size() == 1) {
//				dictionaryEntryDAO.delete(dictionaryEntryUser.getDictionaryEntry());
//			}
//			dictionaryEntryUserDAO.delete(dictionaryEntryUser);
//		}
	}

	// ********************************************************
	// Methods related to LexicalDictionary
	// ********************************************************
	public boolean wordExists(String word) {

		if (this.user != null) {
			for (DictionaryEntry dictionaryEntry : dictionaryEntryDAO
					.listByWord(word)) {
				DictionaryEntryUser deUser = dictionaryEntryUserDAO.find(
						dictionaryEntry, this.user);
				boolean shouldCount = true;
				if (deUser != null && deUser.isDeleted()) {
					shouldCount = false;
				} else if (deUser == null && !dictionaryEntry.isGlobal()) {
					shouldCount = false;
				}
				if (shouldCount) {
					return true;
				}
				return false;
			}
		}
		// should only get data marked as global
		for (DictionaryEntry dictionaryEntry : dictionaryEntryDAO
				.listByWord(word)) {
			if (dictionaryEntry.isGlobal()) {
				return true;
			}
		}

		return false;
	}

	public List<PairWordPOSTag> getLemmasAndPosTagsForWord(String word) {
		List<PairWordPOSTag> listPair = new ArrayList<PairWordPOSTag>();

		if (this.user != null) {
			for (DictionaryEntry dictionaryEntry : dictionaryEntryDAO
					.listByWord(word)) {
				DictionaryEntryUser deUser = dictionaryEntryUserDAO.find(
						dictionaryEntry, this.user);
				boolean shouldAdd = true;
				if (deUser != null && deUser.isDeleted()) {
					shouldAdd = false;
				}
				if (deUser == null && !dictionaryEntry.isGlobal()) {
					shouldAdd = false;
				}
				if (shouldAdd) {
					listPair
							.add(new PairWordPOSTag(dictionaryEntry.getLemma()
									.getWord(), dictionaryEntry.getPosTag()
									.getPosTag()));
				}
			}
		} else {
			// should only get data marked as global
			for (DictionaryEntry dictionaryEntry : dictionaryEntryDAO
					.listByWord(word)) {
				if (dictionaryEntry.isGlobal()) {
					listPair
							.add(new PairWordPOSTag(dictionaryEntry.getLemma()
									.getWord(), dictionaryEntry.getPosTag()
									.getPosTag()));
				}
			}
		}
		return listPair;
	}

	public List<String> getPOSTagsForWord(String word) {
		Set<String> posTags = new HashSet<String>();
		for (PairWordPOSTag pair : getLemmasAndPosTagsForWord(word)) {
			posTags.add(pair.getPosTag());
		}

		return new ArrayList<String>(posTags);
	}

	public List<PairWordPOSTag> getWordsAndPosTagsForLemma(String lemma) {
		List<PairWordPOSTag> listPair = new ArrayList<PairWordPOSTag>();

		if (this.user != null) {
			for (DictionaryEntry dictionaryEntry : dictionaryEntryDAO
					.listByLemma(lemma)) {
				DictionaryEntryUser deUser = dictionaryEntryUserDAO.find(
						dictionaryEntry, this.user);
				boolean shouldAdd = true;
				if (deUser != null && deUser.isDeleted()) {
					shouldAdd = false;
				}
				if (deUser == null && !dictionaryEntry.isGlobal()) {
					shouldAdd = false;
				}
				if (shouldAdd) {
					listPair
							.add(new PairWordPOSTag(dictionaryEntry.getWord()
									.getWord(), dictionaryEntry.getPosTag()
									.getPosTag()));
				}
			}
		} else {
			// should only get data marked as global
			for (DictionaryEntry dictionaryEntry : dictionaryEntryDAO
					.listByLemma(lemma)) {
				if (dictionaryEntry.isGlobal()) {
					listPair
							.add(new PairWordPOSTag(dictionaryEntry.getWord()
									.getWord(), dictionaryEntry.getPosTag()
									.getPosTag()));
				}
			}
		}
		return listPair;
	}

	public void loadDictionary(InputStream file) {
		try {
			LOG.info("Loading dictionary to DB...");
			
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(file, "ISO8859_1"));
			int count = 0;
			String line = reader.readLine();
			while(line != null) {
				String[] parts = line.split("\\s+");
				
				if(parts == null || parts.length != 3) {
					throw new Exception("Invalid format");
				}
				
//				LOG.debug("Will add " + line);
				
				// get the word, if it exists, use it. If not, should create it
				Word word = this.wordDAO.retrieve(parts[0]);

				if (word == null) {
					word = new Word(parts[0]);
					// add the new word
//					LOG.debug("Will add new word: " + word);
					this.wordDAO.add(word);
				}

				// get primitive from DAO.
				Word lemma = this.wordDAO
						.retrieve(parts[1]);
				if (lemma == null) {
					lemma = new Word(parts[1]);
					// add the new word
//					LOG.debug("Will add new word as lemma: " + lemma);
					this.wordDAO.add(lemma);
				}

				// get the pos tag
				PosTag posTag = this.postagDAO.retrieve(parts[2]);
				if (posTag == null) {
					posTag = new PosTag(parts[2]);
//					LOG.debug("Will add new PosTag: " + posTag);
					this.postagDAO.add(posTag);
				}

				DictionaryEntry dictionaryEntryToPersist = this.dictionaryEntryDAO
						.retrieve(word, lemma, posTag);
				if (dictionaryEntryToPersist == null) {
					dictionaryEntryToPersist = new DictionaryEntry(
							word, lemma, posTag);
					dictionaryEntryToPersist.setGlobal(true);
//					LOG.debug("Will add new DictionaryEntry: " + dictionaryEntryToPersist);
					dictionaryEntryDAO.add(dictionaryEntryToPersist);
				}
				
				
				if(count++ % 1000 == 0) {
					LOG.info("Added " + count + " entries.");
				}
				
				line = reader.readLine();
				
			}
			
			LOG.info("Added " + count + " entries. Finished.");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public NicePrintDictionaryEntry generateNicePrint(DictionaryEntry dictionaryEntry) {
		MorphologicalTag mt = ti.parseMorphologicalTag(dictionaryEntry.getPosTag().getPosTag());
		NicePrintDictionaryEntry np = new NicePrintDictionaryEntry(dictionaryEntry, mt.getAsTagList());
		return np;
	}

}
