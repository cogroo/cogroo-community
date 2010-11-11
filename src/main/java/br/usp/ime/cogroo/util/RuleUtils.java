
package br.usp.ime.cogroo.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.usp.pcs.lta.cogroo.entity.Sentence;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.ChunkTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.MorphologicalTag;
import br.usp.pcs.lta.cogroo.entity.impl.runtime.SyntacticTag;
import br.usp.pcs.lta.cogroo.tools.checker.rules.applier.RulesProvider;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Composition;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Element;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Mask;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Operator;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.PatternElement;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Reference;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Reference.Property;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Suggestion;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Suggestion.Replace;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Suggestion.ReplaceMapping;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Suggestion.Swap;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask;
import br.usp.pcs.lta.cogroo.tools.checker.rules.util.RulesContainerHelper;

/**
 * Set of utility methods, mostly to convert a rule element to a human readable string.
 * 
 * @author Marcelo Suzumura
 * @author William Colen
 */
public class RuleUtils {

	public static Map<RuleInfo, String> getRuleAsString(Rule rule) {
		Map<RuleInfo, String> map = new HashMap<RuleInfo, String>();
		map.put(RuleInfo.METHOD, getMethodAsString(rule));
		map.put(RuleInfo.TYPE, getTypeAsString(rule));
		map.put(RuleInfo.GROUP, getGroupAsString(rule));
		map.put(RuleInfo.MESSAGE, getMessageAsString(rule));
		map.put(RuleInfo.SHORTMESSAGE, getShortMessageAsString(rule));
		map.put(RuleInfo.PATTERN, getPatternAsString(rule));
		map.put(RuleInfo.BOUNDARIES, getBoundariesAsString(rule));
		map.put(RuleInfo.SUGGESTIONS, getSuggestionsAsString(rule));
		return map;
	}

	public static String getMethodAsString(Rule rule) {
		return rule.getMethod().value();
	}

	public static String getTypeAsString(Rule rule) {
		return rule.getType();
	}

	public static String getGroupAsString(Rule rule) {
		return rule.getGroup();
	}

	public static String getMessageAsString(Rule rule) {
		return rule.getMessage();
	}
	
	public static String getShortMessageAsString(Rule rule) {
		return rule.getShortMessage();
	}
	
	private static final String ARROW = " &rarr; ";

	public static String getPatternAsString(Rule rule) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (PatternElement patternElement : rule.getPattern()
				.getPatternElement()) {
			sb.append(openSpan(STATE, ""));
			sb.append(getPatternElementAsString(patternElement));
			sb.append(CLOSESPAN + "<sup title=\"índice de referência\">" + count++ + "</sup>" + ARROW);
		}
		
		return sb.substring(0, sb.length() - ARROW.length());
	}

	public static String getPatternElementAsString(PatternElement patternElement) {
		if (patternElement.getElement() != null)
			return getElementAsString(patternElement.getElement());
		else if (patternElement.getComposition() != null)
			return getCompositionAsString(patternElement.getComposition());

		return "NULL";
	}

	private static String getCompositionAsString(Composition composition) {
		if (composition.getAnd() != null)
			return getOperatorAsString(composition.getAnd(), openSpan(OP, "operador lógico e: A e B") + " & " + CLOSESPAN);
		else if (composition.getOr() != null)
			return getOperatorAsString(composition.getOr(), openSpan(OP, "operador lógico ou: A ou B") + " | " + CLOSESPAN);

		return "NULL";
	}

	private static String getOperatorAsString(Operator operator, String op) {
		List<PatternElement> peList = operator.getPatternElement();
		StringBuilder sb = new StringBuilder();
		sb.append(openSpan(ELEMENT, ""));
		sb.append(" ( ");

		int i = 0;
		for (; i < peList.size() - 1; i++) {
			sb.append(getPatternElementAsString(peList.get(i)) + op);
		}

		sb.append(getPatternElementAsString(peList.get(i)) + " ) ");
		sb.append(CLOSESPAN);
		return sb.toString();
	}

	/**
	 * Gets the string representation of an element.
	 * 
	 * @param element
	 *            the element to be planified to a string
	 * @return the element as a string
	 */
	public static String getElementAsString(Element element) {
		StringBuilder sb = new StringBuilder();

		if (element.isNegated() != null && element.isNegated().booleanValue()) {
			sb.append(openSpan(NELEMENT, "não casa elemento") + "&ne;");
//			sb.append(openSpan(OP, "não") + "&ne;" + CLOSESPAN);
		} else {
			sb.append(openSpan(ELEMENT, "casa elemento"));
		}

		int masks = element.getMask().size();
		if (masks > 1) {
			sb.append("(");
		}

		int maskCounter = 0;
		for (Mask mask : element.getMask()) {
			// Encloses lexemes between quotes.
			if (mask.getLexemeMask() != null) {
				sb.append(openSpan(LEXEME, "casar palavra: '"+ mask.getLexemeMask() +"' ") + "\"").append(mask.getLexemeMask()).append("\"" + CLOSESPAN);
			} else if (mask.getPrimitiveMask() != null) {
				// Primitives are enclosed between curly brackets.
				sb.append(openSpan(LEXEME, "casar lema: '"+mask.getPrimitiveMask()+"'") + "{").append(mask.getPrimitiveMask()).append("}" + CLOSESPAN);
			} else if (mask.getTagMask() != null) {
				sb.append(getTagMaskElementAsString(mask.getTagMask()));
			} else if (mask.getTagReference() != null) {
				sb.append(getTagReferenceAsString(mask.getTagReference()));
			}
			if (maskCounter < masks - 1) {
				sb.append(openSpan(OP, "operador lógico ou: A ou B") + " | " + CLOSESPAN);
			}
			maskCounter++;
		}

		
		if (masks > 1) {
			sb.append(")");
		}
		sb.append(CLOSESPAN);
		return sb.toString();
	}
	
	public static String getPropertiesAsString(List<Property> props) {
		StringBuilder sb = new StringBuilder();
		for (Property prop : props) {
			sb.append(prop + " ");
		}
		return sb.toString().trim();
	}

	public static String getTagReferenceAsString(Reference tagRef) {
		StringBuilder sb = new StringBuilder();
		String index = Long.toString(tagRef.getIndex());
		String props = getPropertiesAsString(tagRef.getProperty());
		sb.append(openSpan(REFERENCE, "Obter atributos de flexão morfológica '"+props+"' tomando como base o elemento de índice " + index) + "ref[" + index + "]{");
		sb.append(props);
		sb.append("}" + CLOSESPAN);
		return sb.toString();
	}
	
	public static String getTagMaskAsString(TagMask tagMask) {
		StringBuilder sb = new StringBuilder();
		
		if (tagMask.getSyntacticFunction() != null) {
			sb.append("[" + tagMask.getSyntacticFunction().value() + "]").append(" ");
		}
		if (tagMask.getClazz() != null) {
			sb.append(tagMask.getClazz().value()).append(" ");
		}
		if (tagMask.getGender() != null) {
			sb.append(tagMask.getGender().value()).append(" ");
		}
		if (tagMask.getNumber() != null) {
			sb.append(tagMask.getNumber().value()).append(" ");
		}
		if (tagMask.getCase() != null) {
			sb.append(tagMask.getCase().value()).append(" ");
		}
		if (tagMask.getPerson() != null) {
			sb.append(tagMask.getPerson().value()).append(" ");
		}
		if (tagMask.getTense() != null) {
			sb.append(tagMask.getTense().value()).append(" ");
		}
		if (tagMask.getMood() != null) {
			sb.append(tagMask.getMood().value()).append(" ");
		}
		if (tagMask.getFiniteness() != null) {
			sb.append(tagMask.getFiniteness().value()).append(" ");
		}
		if (tagMask.getPunctuation() != null) {
			sb.append(tagMask.getPunctuation().value()).append(" ");
		}
		return sb.toString().trim();
	}

	public static String getTagMaskElementAsString(TagMask tagMask) {
		StringBuilder sb = new StringBuilder();
		String tm = getTagMaskAsString(tagMask);
		sb.append(openSpan(MASK, "casa parcialmente a flexão morfológica '"+tm+"'") + "<i>");
		sb.append(tm);
		return sb.toString().trim() + "</i>" + CLOSESPAN;
	}

	public static String getBoundariesAsString(Rule rule) {
		return rule.getBoundaries().getLower() + " "
				+ rule.getBoundaries().getUpper();
	}

	public static String getSuggestionsAsString(Rule rule) {
		StringBuilder sb = new StringBuilder();

		if (rule.getSuggestion().isEmpty()) {
			sb.append("none");
		}

		for (Suggestion suggestion : rule.getSuggestion()) {
			// Replaces.
			if (!suggestion.getReplace().isEmpty()) {
				sb.append("<br/>Tipo substituição: <br/>");
			}
			for (Replace replace : suggestion.getReplace()) {
				sb.append(openSpan(ELEMENT, "Substituir elemento A na posição de índice " + replace.getIndex()) + "e[" + replace.getIndex() +"] " + CLOSESPAN);
				sb.append(openSpan(OP, "A &rarr; B &equiv; substituir A por B") + " &rarr; " + CLOSESPAN);
				if (replace.getLexeme() != null) {
					sb.append(openSpan(ELEMENT, "substituir elemento A por " + replace.getLexeme()) + "\"" + replace.getLexeme() + "\"" + CLOSESPAN);
				} else if (replace.getTagReference() != null) {
					String tr = getTagMaskAsString(replace.getTagReference()
							.getTagMask());
					sb.append(openSpan(REFERENCE, "alterar flexão morfológica do element B na posição " + replace.getTagReference().getIndex() + " para '"+tr+"' e substituir A") + "ref[" + replace.getTagReference().getIndex() + "]{");
					sb.append(tr);
					sb.append("}" + CLOSESPAN);
				}
				sb.append("<br />");
			}
//			sb = removeLastVerticalBar(sb);

			// Replace mappings.
			if (!suggestion.getReplaceMapping().isEmpty()) {
				sb.append("<br/>Tipo mapa de substituição: <br/>");
			}
			for (ReplaceMapping replaceMapping : suggestion.getReplaceMapping()) {
				sb.append("(" + openSpan(ELEMENT, "Elemento A na posição de índice " + replaceMapping.getIndex()) + "e[" + replaceMapping.getIndex() +"] " + CLOSESPAN);
				sb.append(openSpan(OP, "se A = B") + " = " + CLOSESPAN);
				sb.append(openSpan(ELEMENT, "Elemento B") + replaceMapping.getKey() + CLOSESPAN + ")");
				sb.append(openSpan(OP, "então substituir por C") + " &rArr; " + CLOSESPAN);
				sb.append(openSpan(ELEMENT, "Elemento C") + replaceMapping.getValue() + CLOSESPAN);
				sb.append("<br />");
			}
//			sb = removeLastVerticalBar(sb);

			// Swaps.
			if (!suggestion.getSwap().isEmpty()) {
				sb.append("<br/>Tipo troca: <br/>");
			}
			for (Swap swap : suggestion.getSwap()) {
				sb.append(openSpan(ELEMENT, "Elemento A na posição de índice " + swap.getA()) + "e[" + swap.getA() +"] " + CLOSESPAN);
				sb.append(openSpan(OP, "trocar A por B") + " &hArr; " + CLOSESPAN);
				sb.append(openSpan(ELEMENT, "Elemento B na posição de índice " + swap.getB()) + "e[" + swap.getB() +"] " + CLOSESPAN);
				sb.append("<br />");
			}
//			sb = removeLastVerticalBar(sb);
			sb.append("\n");
		}
		return sb.toString();
	}

	private static StringBuilder removeLastVerticalBar(StringBuilder sb) {
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '|') {
			return sb.deleteCharAt(sb.length() - 1);
		}
		return sb;
	}

	public enum RuleInfo {
		METHOD, TYPE, GROUP, MESSAGE, SHORTMESSAGE, PATTERN, BOUNDARIES, SUGGESTIONS
	}
	
	public static TagMask createTagMaskFromReference(Reference ref,
			MorphologicalTag mTag, ChunkTag cTag, SyntacticTag sTag) {
		TagMask t = new TagMask();
		for (Property p : ref.getProperty()) {
			switch (p) {
			
			case CLASS:
				if(mTag != null)
				t.setClazz( mTag.getClazzE());
				break;
			case FINITENESS:
				if(mTag != null)
				t.setFiniteness( mTag.getFinitenessE());
				break;
			case GENDER:
				if(mTag != null)
				t.setGender( mTag.getGenderE());
				break;
			
			case NUMBER:
				if(mTag != null)
				t.setNumber( mTag.getNumberE());
				break;
			case PERSON:
				if(mTag != null)
				t.setPerson( mTag.getPersonE());
				break;
			case SYNTACTIC_FUNCTION:
				if(sTag != null)
					t.setSyntacticFunction(TagMask.SyntacticFunction.fromValue(sTag.toVerboseString()));
				break;
			case CHUNK_FUNCTION:
				if(cTag != null)
					t.setChunkFunction(TagMask.ChunkFunction.fromValue(cTag.toVerboseString()));
				break;
			/*case CASE:
			
			case MOOD:
			case PUNCTUATION:
			case TENSE:*/
			default:
				break;
			}
		}

		return t;
	}

	public static TagMask createTagMaskFromReference(Reference ref,
			Sentence sent, int refPos) {
		
int pos = refPos + (int)ref.getIndex();
		if(pos >= 0 && pos < sent.getTokens().size())
		{
			MorphologicalTag mTag = sent.getTokens().get(pos).getMorphologicalTag();
			ChunkTag cTag = sent.getTokens().get(pos).getChunkTag();
			SyntacticTag sTag = sent.getTokens().get(pos).getSyntacticTag();
			return createTagMaskFromReference(ref, mTag, cTag, sTag);
		}
		else
		{
			return new TagMask();
		}
	}
	
	private static final String STATE = "state";
	private static final String REFERENCE = "reference";
	private static final String NELEMENT = "nelement";
	private static final String ELEMENT = "element";
	private static final String LEXEME = "lexeme";
	private static final String OP = "op";
	private static final String MASK = "mask";
	private static final String CLOSESPAN = "</span>";
	
	private static String openSpan(String clazz, String title) {
		return "<span class=\"" + clazz + "\" title=\"" + title + "\">";
	}

	public static void main(String[] args) {
		List<Rule> rules = new RulesContainerHelper(RuleUtils.class.getResource("/")
				.getPath()).getContainerForXMLAccess()
				.getComponent(RulesProvider.class).getRules().getRule();
		// Rule rule = RulesService.getInstance().getRule(69, true);
		// System.out.println(patternAsString(rule.getPattern()));
//		Rules rules = new RulesContainerHelper().getContainerForXMLAccess().getComponent(RulesProvider.class).getRules();
		
		for (Rule rule : rules) {
			System.out.println(rule.getId() + "\t" +
			 getPatternAsString(rule));
			System.out.println(rule.getId() + "\t"
					+ getSuggestionsAsString(rule));
		}
	}

}
