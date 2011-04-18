package br.usp.ime.cogroo.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import opennlp.tools.util.Span;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.ProcessResult;
import br.usp.ime.cogroo.model.SingleGrammarError;
import br.usp.pcs.lta.cogroo.configuration.LegacyRuntimeConfiguration;
import br.usp.pcs.lta.cogroo.entity.Mistake;
import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.errorreport.ErrorReportAccess;
import br.usp.pcs.lta.cogroo.grammarchecker.CheckerResult;
import br.usp.pcs.lta.cogroo.grammarchecker.Cogroo;
import br.usp.pcs.lta.cogroo.grammarchecker.CogrooI;
import br.usp.pcs.lta.cogroo.tools.checker.rules.applier.RulesProvider;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;
import br.usp.pcs.lta.cogroo.tools.checker.rules.util.RulesContainerHelper;
import br.usp.pcs.lta.cogroo.tools.dictionary.LexicalDictionary;

/**
 * Access point to CoGrOO. Each session will instantiate one {@link CogrooFacade}, so each user will
 * have one.
 */
@Component
@ApplicationScoped
public class CogrooFacade {
	
	private static final Logger LOG = Logger.getLogger(CogrooFacade.class);
	private static final Logger LOG_SENT = Logger.getLogger("sentences");
	public static final String GC_PATH = "/gc/";
	
	/** The Cogroo instance */
	private CogrooI theCogroo = null;
	private String resources = getClass().getResource(GC_PATH).getPath();

	private ErrorReportAccess errorReportAccess; 
	
	private void start(){
		if(theCogroo == null) {
			synchronized (this) {
				if(theCogroo == null) {
					LOG.warn("Will start grammar checker!");
					LOG_SENT.warn("Will start grammar checker!");
					this.theCogroo = new Cogroo(new LegacyRuntimeConfiguration(resources));
					this.errorReportAccess = new ErrorReportAccess();
					LOG.warn("Grammar checker started!");
					LOG_SENT.warn("Grammar checker started!");
				}
			}
		}
	}
	
	private void restart() {
		synchronized (this) {
			LOG.warn("Restarting grammar checker!");
			LOG_SENT.warn("Restarting grammar checker!");
			theCogroo = null;
			start();
		}
	}
	
	private CogrooI getCogroo(){
		start();
		return theCogroo;
	}
	
	public ErrorReportAccess getErrorReportAccess() {
		start(); // force initialization
		return this.errorReportAccess;
	}
	
	public CogrooFacade() {
	}
	
	/**
	 * Creates a new {@link CogrooFacade}. The instance of {@link CogrooI} will 
	 * use the dictionary of the user.
	 * @param lexicalDictionary that {@link CogrooI} will use.
	 */
	public CogrooFacade(LexicalDictionary lexicalDictionary) {
		LOG.info("Loading CoGrOO from: " + resources);
	}
	
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
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will check text: [" + text + "]");
		}
		if(LOG_SENT.isInfoEnabled()) {
			LOG_SENT.info("[" + text + "]");
		}
		List<String> mistakes = new ArrayList<String>();
		
		try {
			
			List<Mistake> errors = getCogroo().checkText(text);

			for (Mistake mistake : errors) {			
				StringBuilder str = new StringBuilder();
				str.append(mistake);
				mistakes.add(str.toString());
			}
			
			
			if(LOG.isDebugEnabled()) {
				LOG.debug("Found errors: \n" + errors);
			}
		} catch (Exception e) {
			LOG.error("Failed to process text: " + text, e);
			LOG_SENT.error("Failed to process text: " + text, e);
			LOG.error("Will restart grammar checker. (TODO: DON'T DO IT!)!");
			LOG_SENT.error("Will restart grammar checker. (TODO: DON'T DO IT!)!");
			restart();
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
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will check text: [" + text + "]");
		}
		if(LOG_SENT.isInfoEnabled()) {
			LOG_SENT.info("[" + text + "]");
		}
		
		List<ProcessResult> processResults = new ArrayList<ProcessResult>();
		
		try {
			CheckerResult result = getCogroo().analyseAndCheckText(text);
			
			if(result == null || result.sentences == null) {
				LOG.warn("Cogroo returned null for text: " + text);
				return processResults;
			}
			for (Sentence sentence : result.sentences) {
				List<Mistake> filteredMistakes = filterMistakes(sentence, result.mistakes);
				
				ProcessResult pr = new ProcessResult();
				pr.setSyntaxTree(sentence.getSyntaxTree());
				pr.setTextAnnotatedWithErrors(annotateText(sentence, filteredMistakes));
				pr.setSentence(sentence);
				pr.setMistakes(filterMistakes(sentence, filteredMistakes));
				processResults.add(pr);
			}
			
			if(LOG.isDebugEnabled()) {
				LOG.debug("Finished.");
			}
		} catch (Exception e) {
			LOG.error("Failed to process text: " + text, e);
			LOG_SENT.error("Failed to process text: " + text, e);
			LOG.error("Will restart grammar checker. (TODO: DON'T DO IT!)!");
			LOG_SENT.error("Will restart grammar checker. (TODO: DON'T DO IT!)!");
			restart();
		}
		
		return processResults;
	}
	
	public String getAnnotatedText(String text, List<ProcessResult> processResult) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will annotate text.");
		}
		SortedMap<Span, Mistake> sortedMistakes = new TreeMap<Span, Mistake>();
		
		for (ProcessResult pr : processResult) {
			for (Mistake mistake : pr.getMistakes()) {
				Span mSpan = new Span(mistake.getStart(), mistake.getEnd());
				sortedMistakes.put(mSpan, mistake);
			}
		}
		StringBuilder sb = new StringBuilder(text);
		Span[] spans = sortedMistakes.keySet().toArray(new Span[sortedMistakes.size()]);
		for(int i = spans.length - 1; i >= 0; i--)  {
			sb.insert(spans[i].getEnd(), "</span>");
			sb.insert(spans[i].getStart(), "<span class=\"grammarerror\" title=\"" + sortedMistakes.get(spans[i]).getShortMessage() + "\">");
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("Finished annotating text.");
		}
		return sb.toString();
	}
	
	public List<SingleGrammarError> asSingleGrammarErrorList(String text, List<ProcessResult> processResult) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will prepare as single grammar error.");
		}
		List<SingleGrammarError> singleGEList = new ArrayList<SingleGrammarError>();
		
		SortedMap<Span, Mistake> sortedMistakes = new TreeMap<Span, Mistake>();
		
		for (ProcessResult pr : processResult) {
			for (Mistake mistake : pr.getMistakes()) {
				Span mSpan = new Span(mistake.getStart(), mistake.getEnd());
				sortedMistakes.put(mSpan, mistake);
			}
		}
		
		Span[] spans = sortedMistakes.keySet().toArray(new Span[sortedMistakes.size()]);
		for(int i = 0; i < spans.length; i++)  {
			StringBuilder sb = new StringBuilder(text);
			sb.insert(spans[i].getEnd(), "</span>");
			sb.insert(spans[i].getStart(), "<span class=\"grammarerror\" title=\"" + sortedMistakes.get(spans[i]).getShortMessage() + "\">");
			
			singleGEList.add(new SingleGrammarError(sb.toString(), sortedMistakes.get(spans[i])));
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("Finished preparing as single grammar error.");
		}
		return singleGEList;
	}
	
	private String annotateText(Sentence sentence, List<Mistake> filteredMistakes) {
		SortedMap<Span, Mistake> sortedMistakes = new TreeMap<Span, Mistake>();
		
		for (Mistake mistake : filteredMistakes) {
			Span mSpan = new Span(mistake.getStart(), mistake.getEnd());
			sortedMistakes.put(mSpan, mistake);
		}
		StringBuilder text = new StringBuilder(sentence.getSentence());
		Span[] spans = sortedMistakes.keySet().toArray(new Span[sortedMistakes.size()]);
		for(int i = spans.length - 1; i >= 0; i--)  {
			text.insert(spans[i].getEnd() - sentence.getOffset(), "</span>");
			text.insert(spans[i].getStart() - sentence.getOffset(), "<span class=\"grammarerror\" title=\"" + sortedMistakes.get(spans[i]).getShortMessage() + "\">");
		}
		return text.toString();
	}
	
	private List<Mistake> filterMistakes(Sentence sentence, List<Mistake> mistakes) {
		Span sentSpan = new Span(sentence.getOffset(), sentence.getOffset() + sentence.getSentence().length());
		List<Mistake> filterdMistakes = new ArrayList<Mistake>();
		for (Mistake mistake : mistakes) {
			
			Span mSpan = new Span(mistake.getStart(), mistake.getEnd());
			if(sentSpan.contains(mSpan)) {
				filterdMistakes.add(mistake);
			}
		}
		return filterdMistakes;
	}
	
	private List<Rule> rules;
	
	public List<Rule> getRules() {
		if (rules != null) {
			return rules;
		}
		synchronized (this) {
			if (rules == null) {
				String path = getClass().getResource(CogrooFacade.GC_PATH).getPath();
				LOG.info("Will load rule list from path: " + path);
				rules = Collections
						.unmodifiableList(new RulesContainerHelper(path)
								.getContainerForXMLAccess()
								.getComponent(RulesProvider.class).getRules()
								.getRule());

				LOG.info("Rule list loaded. #" + rules.size());
			}
		}
		return rules;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}
}
