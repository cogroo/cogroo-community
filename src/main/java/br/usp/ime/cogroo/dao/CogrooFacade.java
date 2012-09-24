package br.usp.ime.cogroo.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import opennlp.tools.util.Cache;
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
import br.usp.pcs.lta.cogroo.tools.checker.RuleDefinitionI;
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
	
	private Set<RuleDefinitionI> ruleDefinitionList = null;
	
	
	/** The Cogroo instance */
	private CogrooI theCogroo = null;
	private String resources = getClass().getResource(GC_PATH).getPath();
	
	private AtomicLong procSentCounter = new AtomicLong();
	private AtomicInteger exceptionsCounter = new AtomicInteger();

	private ErrorReportAccess errorReportAccess; 
	
	
	private Cache cache = new Cache(500);
	
	private void start(){
		if(theCogroo == null) {
			synchronized (this) {
				if(theCogroo == null) {
				  
				    if(LOG.isInfoEnabled()) {
				      LOG.info("Will start grammar checker!");
				    }
				    
				    if(LOG_SENT.isInfoEnabled()) {
				      LOG_SENT.info("Will start grammar checker!");
                    }
					
					LegacyRuntimeConfiguration config = new LegacyRuntimeConfiguration(resources);
					this.theCogroo = new Cogroo(config);
					
					this.ruleDefinitionList = new HashSet<RuleDefinitionI>();
					this.ruleDefinitionList.addAll(config.getChecker().getRulesDefinition());
					
					if(LOG.isDebugEnabled()) {
					  LOG.debug("Loaded " + this.ruleDefinitionList.size() + " rule definitions");
					}

					
					
					this.errorReportAccess = new ErrorReportAccess();
					initCache();
					
					if(LOG.isInfoEnabled()) {
					    LOG.info("Grammar checker started!");
                    }
					
					if(LOG_SENT.isInfoEnabled()) {
					  LOG_SENT.info("Grammar checker started!");
                    }
				}
			}
		}
	}
	
	private static final String[] hardcoded = {
		"Graças à vós, tudo se resolveu a tempo.",
		"Graças a vós, tudo se resolveu a tempo."};
	private void initCache() {
		if(!cache.containsKey(hardcoded)) {
			for (String k : hardcoded) {
				cache.put(k, new ArrayList<ProcessResult>());
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
		
		long count = procSentCounter.incrementAndGet();
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will check text: [" + text + "]");
		}
		if(LOG_SENT.isInfoEnabled()) {
			LOG_SENT.info(count + " [" + text + "]");
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
			int eCount = exceptionsCounter.incrementAndGet();
			LOG.error(eCount + " > Failed to process text: " + text, e);
			LOG_SENT.error(eCount + " > Failed to process text: " + text, e);
			LOG.error("Will restart grammar checker. (TODO: DON'T DO IT!)!");
			LOG_SENT.error("Will restart grammar checker. (TODO: DON'T DO IT!)!");
			restart();
		}

		return mistakes;

	}
	
	public List<ProcessResult> cachedProcessText(String text) {
		
		synchronized (cache) {
			if(cache.containsKey(text)) {
				return (List<ProcessResult>) cache.get(text);
			}
		}

		List<ProcessResult> r = processText(text);
		
		synchronized (cache) {
			cache.put(text, r); // will not use cache for now...
		}
		
		return r;
	}
	
	//TODO: if we return detailed information we can handle it in JSP and add colors
	/**
	 * Process text and returns its structure.
	 * @param text 
	 * @return the structure of the text.
	 */
	public List<ProcessResult> processText(String text) {
		long count = procSentCounter.incrementAndGet();
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will check text: [" + text + "]");
		}
		if(LOG_SENT.isDebugEnabled()) {
			LOG_SENT.debug("{" + count + "} " + memory() + "\n  [" + text + "]");
		}
		
		List<ProcessResult> processResults = new ArrayList<ProcessResult>();
		
		try {
			CheckerResult result = getCogroo().analyseAndCheckText(text);
			
			if(result == null || result.sentences == null) {
				LOG.warn("Cogroo returned null for text: " + text);
				return processResults;
			}
			if(LOG.isDebugEnabled()) {
              LOG.debug("Got " + result.sentences.size()
                  + " sentences with a total of " + result.mistakes.size()
                  + " mistakes.");
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
			int eCount = exceptionsCounter.incrementAndGet();
			procSentCounter.set(0);
			LOG.error(eCount + " > Failed to process text: " + text, e);
			LOG_SENT.error(eCount + " > Failed to process text: " + text, e);
			LOG.error("Will restart grammar checker. (TODO: DON'T DO IT!)!");
			LOG_SENT.error("Will restart grammar checker. (TODO: DON'T DO IT!)!");
			restart();
		}
		
		return processResults;
	}
	
	private static String memory() {
		double free =  Runtime.getRuntime().freeMemory() / 1024d / 1024d;
		double max =  Runtime.getRuntime().maxMemory() / 1024d / 1024d;
		double total =  Runtime.getRuntime().totalMemory() / 1024d / 1024d;

		return String.format("Free: %.2f; Max: %.2f; Total: %.2f", free, max, total);

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
	
  private List<Rule> xmlRules;

  public List<Rule> getXMLRules() {
    if (xmlRules != null) {
      return xmlRules;
    }
    synchronized (this) {
      if (xmlRules == null) {
        String path = getClass().getResource(CogrooFacade.GC_PATH).getPath();
        if (LOG.isDebugEnabled()) {
          LOG.debug("Will load rule list from path: " + path);
        }
        xmlRules = Collections.unmodifiableList(new RulesContainerHelper(path)
            .getContainerForXMLAccess().getComponent(RulesProvider.class)
            .getRules().getRule());

        if (LOG.isDebugEnabled()) {
          LOG.debug("Rule list loaded. #" + xmlRules.size());
        }
      }
    }
    return xmlRules;
  }
	
	
	public Set<RuleDefinitionI> getRuleDefinitionList() {
	  start();
	  return ruleDefinitionList;
	  
	}
	
	public void setResources(String resources) {
		this.resources = resources;
	}
	
  public static String addPrefixIfMissing(String ruleID) {
    if (ruleID != null && !ruleID.contains(":")) {
      ruleID = "xml:" + ruleID;
    }
    return ruleID;
  }
	
}
