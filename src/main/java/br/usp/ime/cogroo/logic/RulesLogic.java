package br.usp.ime.cogroo.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.model.ProcessResult;
import br.usp.ime.cogroo.model.RuleStatus;
import br.usp.pcs.lta.cogroo.entity.Mistake;
import br.usp.pcs.lta.cogroo.tools.checker.RuleDefinitionI;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Example;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;

@Component
@ApplicationScoped
public class RulesLogic {

  private TreeMap<String, RuleDefinitionI> ruleMap;
  private TreeMap<String, Rule> xmlRuleMap;
  private CogrooFacade cogrooFacade;
  private List<RuleStatus> ruleStatus;


  public RulesLogic(CogrooFacade cogrooFacade) {
    this.cogrooFacade = cogrooFacade;
  }

  private void init() {
    if (ruleMap == null) {
      Set<RuleDefinitionI> rules = cogrooFacade.getRuleDefinitionList();
      ruleMap = new TreeMap<String, RuleDefinitionI>(
          new RuleDefinitionComparator());
      for (RuleDefinitionI rule : rules) {
        ruleMap.put(rule.getId(), rule);
      }
    }

    if (xmlRuleMap == null) {
      List<Rule> rules = cogrooFacade.getXMLRules();
      xmlRuleMap = new TreeMap<String, Rule>();
      for (Rule rule : rules) {
        xmlRuleMap.put("xml:" + Long.toString(rule.getId()), rule);
      }
    }
  }

  public Collection<RuleDefinitionI> getRuleList() {
    init();
    return cogrooFacade.getRuleDefinitionList();
  }

  public RuleDefinitionI getRule(String id) {
    init();
    return ruleMap.get(CogrooFacade.addPrefixIfMissing(id));
  }

  public String getNextRuleID(String currentRuleID) {

    Entry<String, RuleDefinitionI> entry = ruleMap.higherEntry(currentRuleID);

    if (entry != null) {
      return entry.getKey();
    }

    return null;
  }

  public String getPreviousRuleID(String currentRuleID) {

    Entry<String, RuleDefinitionI> entry = ruleMap.lowerEntry(currentRuleID);

    if (entry != null) {
      return entry.getKey();
    }

    return null;
  }

  public Rule getXmlRule(RuleDefinitionI rule) {
    return this.xmlRuleMap.get(rule.getId());
  }

  private static class RuleDefinitionComparator implements Comparator<String> {

    private static final String XML_PREFIX = "xml:";

    @Override
    public int compare(String o1, String o2) {
      boolean o1IsXml = o1.startsWith(XML_PREFIX);
      boolean o2IsXml = o2.startsWith(XML_PREFIX);
      if (o1IsXml && o2IsXml) {
        Integer i1 = new Integer(o1.substring(4));
        Integer i2 = new Integer(o2.substring(4));
        return i1.compareTo(i2);
      } else if (o1IsXml) {
        return -1;
      } else if (o2IsXml) {
        return 1;
      }
      return o1.compareTo(o2);
    }
  }
   
  
  public void refreshRuleStatus() {
    Collection<RuleDefinitionI> rules = this.getRuleList();
    
    this.ruleStatus = new ArrayList<RuleStatus>(rules.size());
    
    for (RuleDefinitionI rule : rules) {
      ruleStatus.add(status(rule));
    }
  }

  private RuleStatus status(RuleDefinitionI rule) {
    RuleStatus status = new RuleStatus(rule);
    
    if (rule.isXMLBased()) {
      Rule ruleXML = this.getXmlRule(rule);
      if (ruleXML.isActive()) {
        status.setActive(true);
      }
    }
    
    int tp = 0, fp = 0, fn = 0;
    List<Example> examples = rule.getExamples();
    
    for (Example example : examples) {
      boolean vpLocal = false;
      
      List<ProcessResult> results = cogrooFacade.processText(example.getIncorrect());
      for (ProcessResult result : results) {
        
        List<Mistake> mistakes = result.getMistakes();
        for (Mistake mistake : mistakes) {
          if (mistake.getRuleIdentifier().equals(rule.getId())) {
            vpLocal = true;
            tp++;
          }
          else {
            fp++;
          }
        }
        
      }
      if (vpLocal == false) {
        fn++;
      }
      
      
      results = cogrooFacade.processText(example.getCorrect());
      for (ProcessResult result : results) {
        List<Mistake> mistakes = result.getMistakes();
        fp += mistakes.size();
      }
    }
    
    status.setFn(fn);
    status.setFp(fp);
    status.setTp(tp);  
  
    return status;
  }

  public List<RuleStatus> getRuleStatus() {
    if (ruleStatus == null) {
      refreshRuleStatus();
    }
    return ruleStatus;
  }
  
}
