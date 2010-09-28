package br.usp.ime.cogroo.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import opennlp.tools.util.Span;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.ProcessResult;
import br.usp.pcs.lta.cogroo.configuration.CachedConfigurationFactory;
import br.usp.pcs.lta.cogroo.entity.Mistake;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MistakeImpl;
import br.usp.pcs.lta.cogroo.errorreport.ErrorReportAccess;
import br.usp.pcs.lta.cogroo.grammarchecker.CheckerResult;
import br.usp.pcs.lta.cogroo.grammarchecker.Cogroo;
import br.usp.pcs.lta.cogroo.grammarchecker.CogrooI;
import br.usp.pcs.lta.cogroo.tools.dictionary.LexicalDictionary;

/**
 * Access point to CoGrOO. Each session will instantiate one {@link CogrooFacade}, so each user will
 * have one.
 */
@Component
public class CogrooFacade {
	private static final Logger LOG = Logger.getLogger(CogrooFacade.class);
	
	/** The Cogroo instance */
	private CogrooI theCogroo = null;
	private LexicalDictionary lexicalDictionary;
	private String resources = getClass().getResource("/").getPath();

	private ErrorReportAccess errorReportAccess; 
	
	private void start(){
		CachedConfigurationFactory configFactory = new CachedConfigurationFactory(resources);
		this.theCogroo = new Cogroo(configFactory.getNewRuntimeConfiguration(lexicalDictionary));
		this.errorReportAccess = new ErrorReportAccess();
	}
	
	private CogrooI getCogroo(){
		if(theCogroo == null) 
			start();
		
		return theCogroo;
	}
	
	public ErrorReportAccess getErrorReportAccess() {
		if(theCogroo == null) 
			start();
		
		return this.errorReportAccess;
	}
	
	/**
	 * Creates a new {@link CogrooFacade}. The instance of {@link CogrooI} will 
	 * use the dictionary of the user.
	 * @param lexicalDictionary that {@link CogrooI} will use.
	 */
	public CogrooFacade(LexicalDictionary lexicalDictionary) {
		LOG.debug("Creating CoGrOO from: " + resources);
		this.lexicalDictionary = lexicalDictionary;
	}
	
//	/**
//	 * Creates a new {@link CogrooFacade}. The instance of {@link CogrooI} will 
//	 * use the dictionary of the user.
//	 * @param lexicalDictionary that {@link CogrooI} will use.
//	 */
//	public CogrooFacade(LexicalDictionary lexicalDictionary, String aResources) {
//		LOG.debug("Creating CoGrOO from: " + aResources);
//		this.setResources(aResources);
//		this.lexicalDictionary = lexicalDictionary;
//	}
	
	/**
	 * Set CoGrOO instance. For testing purpose.
	 * @param cogroo a Cogroo instance.
	 */
	void setCogroo(CogrooI cogroo) {
		this.theCogroo = cogroo;
	}

	
	//TODO: should return Mistakes and format it in JSP.
	/**
	 * Get mistakes for a text. 
	 * @param text text to check
	 * @return list of errors
	 */
	public List<String> getMistakes(String text) {

		List<Mistake> errors = getCogroo().checkText(text);

		List<String> mistakes = new ArrayList<String>();
		for (Mistake mistake : errors) {			
			StringBuilder str = new StringBuilder();
			str.append(mistake);
			mistakes.add(str.toString());
		}

		return mistakes;

	}
	
	//TODO: if we return detailed information we can handle it in JSP and add colors
	/**
	 * Process text and returns its structure.
	 * @param text 
	 * @return the structure of the text.
	 */
	public List<ProcessResult> processText(String text) {
		CheckerResult result = getCogroo().analyseAndCheckText(text);
		List<ProcessResult> processResults = new ArrayList<ProcessResult>();
		for (Sentence sentence : result.sentences) {
			ProcessResult pr = new ProcessResult();
			pr.setSyntaxTree(sentence.getSyntaxTree());
			pr.setTextAnnotatedWithErrors(annotateText(sentence, result.mistakes));
			pr.setSentence(sentence);
			processResults.add(pr);
		}
		
		return processResults;
	}
	
	private String annotateText(Sentence sentence, List<Mistake> mistakes) {
		Span sentSpan = new Span(sentence.getOffset(), sentence.getOffset() + sentence.getSentence().length());
		SortedMap<Span, Mistake> sortedMistakes = new TreeMap<Span, Mistake>();
		for (Mistake mistake : mistakes) {
			Span mSpan = new Span(mistake.getStart(), mistake.getEnd());
			if(sentSpan.contains(mSpan)) {
				sortedMistakes.put(mSpan, mistake);
			}
		}
		StringBuilder text = new StringBuilder(sentence.getSentence());
		Span[] spans = sortedMistakes.keySet().toArray(new Span[sortedMistakes.size()]);
		for(int i = spans.length - 1; i >= 0; i--)  {
			text.insert(spans[i].getEnd() - sentence.getOffset(), "</span>");
			text.insert(spans[i].getStart() - sentence.getOffset(), "<span class=\"grammarerror\">");
		}
		return text.toString();
	}

	private String prettyPrint(List<Sentence> sentences, List<Mistake> mistakes) {
		// print the sentence structure, and after the mistakes in it
		StringBuilder sb = new StringBuilder();
		int sentCounter = 1;
		for (Sentence sentence : sentences) {
			sb.append("Sentence " + sentCounter++ + ": ");
			sb.append(prettyPrint(sentence));
			int mistakeCounter = 1;
			for (Mistake mistake : mistakes) {
				if(mistake.getStart() >= sentence.getOffset() && mistake.getEnd() <= sentence.getOffset() + sentence.getSentence().length() ) {
					sb.append("   Mistake " + mistakeCounter++ + ": ");
					sb.append(prettyPrint(mistake));
				}

			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	private String prettyPrint(Mistake mistake) {
		return "rule["
		+ ((MistakeImpl) mistake).getId()
		+ "], span["
		+ ((MistakeImpl) mistake).getStart() + ", "
		+ ((MistakeImpl) mistake).getEnd() + "]\n";
	}
	
	private String prettyPrint(Sentence sentence) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\"" + sentence.getSentence()
				+ "\"\n");
		sb.append(sentence.getTree());
		
		sb.append("\n");
		sb.append(sentence.getSyntaxTree());
		
		return sb.toString();
	}

	public void setResources(String resources) {
		this.resources = resources;
	}

}
