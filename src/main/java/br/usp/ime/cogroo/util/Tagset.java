package br.usp.ime.cogroo.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Joiner;

public class Tagset {

  private static final Map<String, String> POS;
  private static final Map<String, String> FEAT;
  private static final Map<String, String> GROUP;
  private static final Map<String, String> CLAUSE;

  static {
    Map<String, String> _pos = new HashMap<String, String>();

    _pos.put("n", "substantivo");
    _pos.put("prop", "nome próprio");
    _pos.put("art", "artigo");
    _pos.put("pron-pers", "pronome pessoal");
    _pos.put("pron-det", "pronome determinativo");
    _pos.put("pron-indp", "substantivo");
    _pos.put("adj", "adjetivo");
    _pos.put("n-adj", "substantivo/adjetivo");
    _pos.put("v-fin", "verbo finitivo");
    _pos.put("v-inf", "verbo infinitivo");
    _pos.put("v-pcp", "verbo particípio");
    _pos.put("v-ger", "verbo gerúndio");
    _pos.put("num", "numeral");
    _pos.put("prp", "preposição");
    _pos.put("adj", "adjetivo");
    _pos.put("conj-s", "conjunção subordinativa");
    _pos.put("conj-c", "conjunção coordenativa");
    _pos.put("intj", "interjeição");
    _pos.put("adv", "adverbio");

    POS = Collections.unmodifiableMap(_pos);

    Map<String, String> _feat = new HashMap<String, String>();

    _feat.put("M", "masculino");
    _feat.put("F", "feminino");

    _feat.put("S", "singular");
    _feat.put("P", "plural");

    _feat.put("NOM", "nominativo");
    _feat.put("ACC", "acusativo");
    _feat.put("DAT", "dativo");
    _feat.put("PIV", "prepositivo");

    _feat.put("1", "primeira pessoa");
    _feat.put("2", "segunda pessoa");
    _feat.put("3", "terceira pessoa");
    
    _feat.put("1S", "primeira pessoa singular");
    _feat.put("3S", "terceira pessoa singular");
    _feat.put("3P", "terceira pessoa plural");

    _feat.put("PR", "presente");
    _feat.put("IMPF", "imperfeito");
    _feat.put("PS", "perfeito simples");
    _feat.put("MQP", "mais-que-perfeito");
    _feat.put("FUT", "futuro");
    _feat.put("COND", "condicional");

    _feat.put("IND", "indicativo");
    _feat.put("SUBJ", "subjuntivo");
    _feat.put("IMP", "imperativo");

    FEAT = Collections.unmodifiableMap(_feat);

    Map<String, String> _group = new HashMap<String, String>();

    _group.put("NP", "nominal");
    _group.put("VP", "verbal");
    _group.put("PP", "preposicional");
    _group.put("ADVP", "adverbial");

    GROUP = Collections.unmodifiableMap(_group);

    Map<String, String> _clause = new HashMap<String, String>();

    _clause.put("ACC", "objeto direto");
    _clause.put("ADVL", "adjunto adverbial");
    _clause.put("APP", "aposição");
    _clause.put("DAT", "objeto indireto pronominal");
    _clause.put("OC", "predicativo do objeto");
    _clause.put("P", "predicado");
    _clause.put("PIV", "objeto preposicional");
    _clause.put("SA", "complemento adverbial");
    _clause.put("SC", "predicativo do sujeito");
    _clause.put("SUBJ", "sujeito");

    CLAUSE = Collections.unmodifiableMap(_clause);
  }

  private static String unknown(String tag) {
    return "#" + tag + "#";
  }

  private static final Joiner COMMA_JOINER = Joiner.on(", ");

  public static String comma(String[] tags) {
    return COMMA_JOINER.join(tags);
  }

  public static String getPOS(String tag, String lexeme) {
    if(tag.equals(lexeme)) {
      return tag;
    }
    if (POS.containsKey(tag)) {
      return POS.get(tag);
    } else {
      return unknown(tag);
    }
  }

  public static String getFeatures(String tag) {
    if(tag.equals("-")) {
      return "";
    }
    String[] tags = tag.split("=");

    String[] out = new String[tags.length];

    for (int i = 0; i < out.length; i++) {
      if (FEAT.containsKey(tags[i])) {
        out[i] = FEAT.get(tags[i]);
      } else {
        out[i] = unknown(tags[i]);
      }
    }

    return comma(out);
  }

  public static String getChunk(String tag, String next) {
    if(tag.startsWith("I-")) {
      if(next != null && next.startsWith("I-")) {
        return "&#9474;";
      } else {
        return "&#9492;";
      }
    } else if (tag.startsWith("B-")) {
      tag = tag.substring(2);
    } else {
      return "";
    }
    if (GROUP.containsKey(tag)) {
      tag = GROUP.get(tag);
    } else {
      tag = unknown(tag);
    }
    
    String dash = null;
    if(next != null && next.startsWith("I-")) {
      dash = "&#9484; ";
    } else {
      dash = "&ndash; ";
    }
    
    return dash + tag;
  }

  public static String getClause(String tag, String next) {
    if(tag.startsWith("I-")) {
      if(next != null && next.startsWith("I-")) {
        return "&#9474;";
      } else {
        return "&#9492;";
      }
    } else if (tag.startsWith("B-")) {
      tag = tag.substring(2);
    } else {
      return "";
    }
    if (CLAUSE.containsKey(tag)) {
      tag = CLAUSE.get(tag);
    } else {
      tag = unknown(tag);
    }
    
    String dash = null;
    if(next != null && next.startsWith("I-")) {
      dash = "&#9484; ";
    } else {
      dash = "&ndash; ";
    }
    
    return dash + tag;
  }

}
