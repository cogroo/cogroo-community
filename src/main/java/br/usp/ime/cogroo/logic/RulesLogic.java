package br.usp.ime.cogroo.logic;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.pcs.lta.cogroo.tools.checker.RuleDefinitionI;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;

@Component
@ApplicationScoped
public class RulesLogic {

  private TreeMap<String, RuleDefinitionI> ruleMap;
  private TreeMap<String, Rule> xmlRuleMap;
  private CogrooFacade cogrooFacade;

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

}
