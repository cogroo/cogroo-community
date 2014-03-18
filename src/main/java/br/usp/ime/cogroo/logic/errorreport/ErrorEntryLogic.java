package br.usp.ime.cogroo.logic.errorreport;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.cogroo.entities.Mistake;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.errorreport.model.BadIntervention;
import org.cogroo.tools.errorreport.model.ErrorReport;
import org.cogroo.tools.errorreport.model.Omission;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.Messages;
import br.usp.ime.cogroo.dao.CogrooFacade;
import br.usp.ime.cogroo.dao.GrammarCheckerVersionDAO;
import br.usp.ime.cogroo.dao.HistoryEntryDAO;
import br.usp.ime.cogroo.dao.HistoryEntryFieldDAO;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.dao.errorreport.CommentDAO;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.dao.errorreport.GrammarCheckerBadInterventionDAO;
import br.usp.ime.cogroo.dao.errorreport.GrammarCheckerOmissionDAO;
import br.usp.ime.cogroo.exceptions.CommunityException;
import br.usp.ime.cogroo.exceptions.CommunityExceptionMessages;
import br.usp.ime.cogroo.logic.RulesLogic;
import br.usp.ime.cogroo.logic.StringTemplateUtil;
import br.usp.ime.cogroo.logic.TextSanitizer;
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.GrammarCheckerVersion;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.ProcessResult;
import br.usp.ime.cogroo.model.ReportStats;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.model.errorreport.BadInterventionClassification;
import br.usp.ime.cogroo.model.errorreport.Comment;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerBadIntervention;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerOmission;
import br.usp.ime.cogroo.model.errorreport.HistoryEntry;
import br.usp.ime.cogroo.model.errorreport.HistoryEntryField;
import br.usp.ime.cogroo.model.errorreport.Priority;
import br.usp.ime.cogroo.model.errorreport.State;
import br.usp.ime.cogroo.notifiers.Notificator;
import br.usp.ime.cogroo.util.BuildUtil;

import com.google.common.base.Objects;

@Component
public class ErrorEntryLogic {

	private static final Logger LOG = Logger.getLogger(ErrorEntryLogic.class);

	public static final String CUSTOM = "custom";

	private static final String REPORTS = "reports/";

	public static final String STATUS_OK = "OK";
	public static final String STATUS_NOT = "NOT";
	public static final String STATUS_WARN = "WARN";
	public static final String STATUS_INVALID = "INVALID";

	private ErrorEntryDAO errorEntryDAO;
	private UserDAO userDAO;
	private CommentDAO commentDAO;
	private User user;
	private CogrooFacade cogrooFacade;
	private GrammarCheckerVersionDAO versionDAO;
	private GrammarCheckerOmissionDAO omissionDAO;
	private GrammarCheckerBadInterventionDAO badInterventionDAO;
	private HistoryEntryDAO historyEntryDAO;
	private HistoryEntryFieldDAO historyEntryFieldDAO;
	private ApplicationData appData;
	private StringTemplateUtil templateUtil;
	private Notificator notificator;

	private TextSanitizer sanitizer;

	private RulesLogic rulesLogic;

	private static final Map<String, String> CATEGORIES;

	static {
		Map<String, String> lc = new HashMap<String, String>();
		lc.put("emprego do mim e ti", "pro"); // , cop|pro,
	    lc.put("uso do verbo haver", "sem");
	    lc.put("regência verbal", "reg");
	    lc.put("verbo preferir", "ali"); // "cov"
	    lc.put("emprego de vírgulas", "ptn");
	    lc.put("gerundismo", "ger");
	    lc.put("concordância determinante-substantivo", "con");
	    lc.put("em anexo", "ali"); // ali
	    lc.put("concordância artigo-substantivo", "con");
	    lc.put("erros mecânicos", "esp");
	    lc.put("concordância do sujeito com o adjetivo predicativo", "con"); // con/cov
	    lc.put("verbo haver", "aha");
	    lc.put("à medida em que/à medida que", "det"); // det
	    lc.put("crase", "cra");
	    lc.put("concordância sujeito-verbo", "cov"); // ver
	    lc.put("se eu ver", "cmt");
	    lc.put("emprego de eu e mim", "ren");
	    lc.put("colocação pronominal", "cop"); // pro
	    lc.put("emprego de mau e mal", "mal");
	    lc.put("regência nominal", "ren");
	    lc.put("verbo fazer", "cov");//cov|reg|ver
	    lc.put("concordância adjetivo-substantivo", "con"); //adv
	    lc.put("concordância numeral-substantivo", "con");
	    lc.put("concordância do sujeito com o predicativo", "con"); //cov
	    lc.put("uso de meio", "adv"); // con
	    lc.put("concordância do sujeito com o verbo do predicado", "ver");
	    lc.put("expressões redundantes", "sem");
	    lc.put("uso de mas/mais", "lex");
	    lc.put("uso de advérbios", "adv");
	    lc.put("concordância de modos e tempos verbais", "ver");
	    lc.put("uso de porque e variantes", "ort");
	    CATEGORIES = Collections.unmodifiableMap(lc);
	}

	public ErrorEntryLogic(LoggedUser loggedUser, ErrorEntryDAO errorEntryDAO,
			UserDAO userDAO, CommentDAO commentDAO, CogrooFacade cogrooFacade,
			GrammarCheckerVersionDAO versionDAO, GrammarCheckerOmissionDAO omissionDAO,
			GrammarCheckerBadInterventionDAO badInterventionDAO, HistoryEntryDAO historyEntryDAO, HistoryEntryFieldDAO historyEntryFieldDAO, ApplicationData appData, Notificator notificator, StringTemplateUtil templateUtil, TextSanitizer sanitizer, RulesLogic rulesLogic) {
		this.userDAO = userDAO;
		this.commentDAO = commentDAO;
		this.errorEntryDAO = errorEntryDAO;
		this.user = loggedUser.getUser();
		this.cogrooFacade = cogrooFacade;
		this.versionDAO = versionDAO;
		this.omissionDAO = omissionDAO;
		this.badInterventionDAO = badInterventionDAO;
		this.historyEntryDAO = historyEntryDAO;
		this.historyEntryFieldDAO = historyEntryFieldDAO;
		this.appData = appData;
		this.notificator = notificator;
		this.templateUtil = templateUtil;
		this.sanitizer = sanitizer;
		this.rulesLogic = rulesLogic;
	}

	public List<ErrorEntry> getAllReports() {
		// set isNew property!
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, -7);
		Date oneWeekAgo = now.getTime();
		List<ErrorEntry> errors = errorEntryDAO.listAll();

		for (ErrorEntry errorEntry : errors) {

			if(this.user == null && oneWeekAgo.before(errorEntry.getModified()) ) {

				errorEntry.setIsNew(true);
			} else if(this.user != null && this.user.getPreviousLogin().before(errorEntry.getModified())) {
				// if comments is empty, check only the submitter
				if(errorEntry.getComments().size() == 0) {
					if(!errorEntry.getSubmitter().getLogin().equals(this.user.getLogin()) ||
							!errorEntry.getSubmitter().getService().equals(this.user.getService())) {
						errorEntry.setIsNew(true);
					}
				}

				else {
					// we check the comments. we skip if the newest is of this user
					Date newest = this.user.getPreviousLogin();
					boolean isNew = false;
					for (Comment comment : errorEntry.getComments()) {

						for(Comment answer : comment.getAnswers()) {
							if(answer.getDate().after(newest)) {
								newest = answer.getDate();
								if(!answer.getUser().getLogin().equals(this.user.getLogin()) ||
										!answer.getUser().getService().equals(this.user.getService())) {
									isNew = true;
								} else {
									isNew = false;
								}
							}
						}
						if(comment.getDate().after(newest)) {
							newest = comment.getDate();
							if(!comment.getUser().getLogin().equals(this.user.getLogin()) ||
									!comment.getUser().getService().equals(this.user.getService())) {
								isNew = true;
							} else {
								isNew = false;
							}
						}

					}
					if(isNew) {
						errorEntry.setIsNew(true);
					}
				}

			}
		}
		return errors;
	}

	public SortedSet<String> getErrorCategoriesForUser(String userName) {
		 // TODO implement for user
		 return getErrorCategoriesForUser();
	}

	public SortedSet<String> getErrorCategoriesForUser() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Will get error categories for user.");
		}

		SortedSet<String> uniqueRules = new TreeSet<String>();

		Set<RuleDefinition> rules = cogrooFacade.getRuleDefinitionList();
		for (RuleDefinition rule : rules) {
			uniqueRules.add(rule.getCategory());
		}

		uniqueRules.add("Uso de pontuação");
        uniqueRules.add("Uso de mas/mais");
        uniqueRules.add("Uso de advérbios");
        uniqueRules.add("Concordância de modos e tempos verbais");
        uniqueRules.add("Uso de porque e variantes");


		if (LOG.isDebugEnabled()) {
			LOG.debug("Finished getting error categories for user.");
		}
		return uniqueRules;
	}

	@Deprecated
	public List<ErrorEntry> addErrorEntry(String username, String error)
			throws CommunityException {
		return addErrorEntry("cogroo", username, error);
	}

	public String getCorpus() {
		StringBuilder text = new StringBuilder();
		List<ErrorEntry> reports = this.getAllReports();

		for (ErrorEntry errorEntry : reports) {
			text.append(getFormattedText(errorEntry));
		}

		return text.toString();
	}

	private String getFormattedText(ErrorEntry e) {
		StringBuilder t = new StringBuilder();

		if ( isAddToCorpus(e) ) {
			t.append("<ext id=\"");
			t.append(e.getId());
			t.append("\" url=\"comunidade.org");
			t.append(">\"\n<p id=\"1\">\n<s id=\"1\">\n");
			t.append("\nSOURCE: CM t=").append(e.getId());
			t.append(" p=1 s=1");

			String catId = "";

			if (e.getOmission() != null) {
				GrammarCheckerOmission checker = e.getOmission();
				String cat;
				t.append(" c=");
				if(checker.getCategory() == null) {
				  cat = "NULL";
				} else if(CATEGORIES.containsKey(checker.getCategory().toLowerCase())) {
				  cat = CATEGORIES.get(checker.getCategory().toLowerCase());
				} else {
				  cat = "*** " + checker.getCategory() + " ***";
				}

				t.append(cat);

				t.append(" err=\"");
				t.append(e.getText().substring(e.getSpanStart(), e.getSpanEnd()));
				t.append("\" rep=\"");

				t.append(StringEscapeUtils.unescapeHtml(checker.getReplaceBy()));
				t.append("\"");

				//catId = "_" + cat + "_";
			}


			t.append("\nCM").append(catId).append(e.getId()).append("-1 ");
			t.append(e.getText());
			t.append("\n\n</s>\n</p>\n</ext>\n\n");
		}

		return t.toString();
	}

	private boolean isAddToCorpus(ErrorEntry e) {
      if (e.getState() == State.REJECTED || e.getState() == State.OPEN
          || e.getState() == State.FEEDBACK) {

        return false;
      }
      if (e.getBadIntervention() != null
          && e.getBadIntervention().getClassification() != BadInterventionClassification.FALSE_ERROR) {
        return false;
      }

	  return true;
    }

  /**
	 * Add a new error entry via OpenOffice plugin.
	 * @param service
	 * @param username
	 * @param error
	 * @return
	 * @throws CommunityException
	 */
	public List<ErrorEntry> addErrorEntry(String service, String username, String error)
			throws CommunityException {
		List<ErrorEntry> list = new ArrayList<ErrorEntry>();

		// try to get user, or create it
		User cogrooUser;
		if (userDAO.existLogin("cogroo", username)) {
			cogrooUser = userDAO.retrieveByLogin(service, username);
		} else {
			LOG.error("Invalid user in addErrorEntry:" + username);
			throw new CommunityException(
					CommunityExceptionMessages.INVALID_USER,
					new Object[] { username });
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("Got user:" + cogrooUser);
		}
		ErrorReport er = cogrooFacade.getErrorReportAccess().getErrorReport(
				new StringReader(error));
		GrammarCheckerVersion version = versionDAO.retrieve(er.getVersion());

		Date time = new Date();

		// we split the report in several new entries...
		// sanitizer.sanitize(username, false)
		if(er.getOmissions() != null) {
			List<Omission> omissions = er.getOmissions().getOmission();
			if(omissions != null) {
				for (Omission omission : omissions) {
					ErrorEntry errorEntry = new ErrorEntry(
							sanitizer.sanitize(er.getText(), false, true),
							omission.getSpan().getStart(),
							omission.getSpan().getEnd(),
							new ArrayList<Comment>(),
							version,
							cogrooUser,
							time,
							time,
							null,
							null,
							State.OPEN,
							Priority.NORMAL);

					if(omission.getComment() != null && omission.getComment().length() > 0) {
						List<Comment> comments = new ArrayList<Comment>();
						Comment c = new Comment(cogrooUser, time, sanitizer.sanitize(omission.getComment(), false), errorEntry, null);
						commentDAO.add(c);
						comments.add(c);
						errorEntry.setComments(comments);
					}

					GrammarCheckerOmission gcOmission = new GrammarCheckerOmission(
							omission.getCategory(),
							sanitizer.sanitize(omission.getCustomCategory(), false),
							sanitizer.sanitize(omission.getReplaceBy(), false),
							errorEntry);
					omissionDAO.add(gcOmission);
					errorEntry.setOmissions(gcOmission);
					setStatus(errorEntry);
					errorEntryDAO.add(errorEntry);
					notificationForReport(errorEntry);
					appData.incReportedErrors();
					list.add(errorEntry);
				}
			}
		}

		if(er.getBadInterventions() != null) {
			List<BadIntervention> badInterventions = er.getBadInterventions().getBadIntervention();
			if(badInterventions != null) {
				for (BadIntervention badIntervention : badInterventions) {

					ErrorEntry errorEntry = new ErrorEntry(
							sanitizer.sanitize(er.getText(), false, true),
							badIntervention.getSpan().getStart(),
							badIntervention.getSpan().getEnd(),
							null,
							version,
							cogrooUser,
							time,
							time,
							null,
							null,
							State.OPEN,
							Priority.NORMAL);

					if(badIntervention.getComment() != null && badIntervention.getComment().length() > 0) {
						List<Comment> comments = new ArrayList<Comment>();
						Comment c = new Comment(cogrooUser, time, sanitizer.sanitize(badIntervention.getComment(), false), errorEntry, null);
						commentDAO.add(c);
						comments.add(c);
						errorEntry.setComments(comments);
					}

					BadInterventionClassification classification = null;
					switch (badIntervention.getClassification()) {
						case FALSE_ERROR:
							classification = BadInterventionClassification.FALSE_ERROR;
							break;
						case INAPPROPRIATE_DESCRIPTION:
							classification = BadInterventionClassification.INAPPROPRIATE_DESCRIPTION;
							break;
						case INAPPROPRIATE_SUGGESTION:
							classification = BadInterventionClassification.INAPPROPRIATE_SUGGESTION;
							break;
						default:
							break;
					}
					GrammarCheckerBadIntervention gcBadIntervention = new GrammarCheckerBadIntervention(
							classification,
							// we had to do it here because the rule might be without prefix if coming from a old version of cogroo
							// it was not handled before because it was inside the XML.
							CogrooFacade.addPrefixIfMissing(badIntervention.getRule()),
							errorEntry);

					badInterventionDAO.add(gcBadIntervention);
					errorEntry.setBadIntervention(gcBadIntervention);

					setStatus(errorEntry);
					errorEntryDAO.add(errorEntry);
					notificationForReport(errorEntry);
					appData.incReportedErrors();
					list.add(errorEntry);
				}
			}
		}

		return list;
	}

  public void addErrorEntry(
			User cogrooUser,
			String text,
			List<String> badint,
			List<String> badintComments,
			List<String> badintStart,
			List<String> badintEnd,
			List<String> badintRule,
			List<String> omissionClassification,
			List<String> customOmissionText,
			List<String> omissionComment,
			List<String> omissionReplaceBy,
			List<String> omissionStart,
			List<String> omissionEnd) {

		GrammarCheckerVersion version = versionDAO.retrieve("c" + BuildUtil.POM_VERSION);

		Date time = new Date();

		// we split the report in several new entries...

		if(omissionClassification != null) {
			for (int i = 0; i < omissionClassification.size(); i++) {
					ErrorEntry errorEntry = new ErrorEntry(
							text,
							Integer.parseInt(omissionStart.get(i)),
							Integer.parseInt(omissionEnd.get(i)),
							new ArrayList<Comment>(),
							version,
							cogrooUser,
							time,
							time,
							null,
							null,
							State.OPEN,
							Priority.NORMAL);

					if(omissionComment.get(i) != null && omissionComment.get(i).length() > 0) {
						List<Comment> comments = new ArrayList<Comment>();
						Comment c = new Comment(cogrooUser, time, omissionComment.get(i), errorEntry, null);
						commentDAO.add(c);
						comments.add(c);
						errorEntry.setComments(comments);
					}

					String classification = null;
					String customClass = null;

					if(omissionClassification.get(i).equals(CUSTOM)) {
						classification = null;
						customClass = customOmissionText.get(i);
					} else {
						classification = omissionClassification.get(i);
						customClass = null;
					}

					GrammarCheckerOmission gcOmission = new GrammarCheckerOmission(
							classification,
							customClass,
							omissionReplaceBy.get(i),
							errorEntry);
					omissionDAO.add(gcOmission);
					errorEntry.setOmissions(gcOmission);

					setStatus(errorEntry);
					errorEntryDAO.add(errorEntry);
					notificationForReport(errorEntry);
					appData.incReportedErrors();
					if(LOG.isDebugEnabled()) {
						LOG.debug("Added errorEntry:" + errorEntry);
					}
				}
		}

		if(badint != null) {
			for (int i = 0; i < badint.size(); i++) {
				if( !badint.get(i).equals("ok") ) {
					ErrorEntry errorEntry = new ErrorEntry(
							text,
							Integer.parseInt(badintStart.get(i)),
							Integer.parseInt(badintEnd.get(i)),
							null,
							version,
							cogrooUser,
							time,
							time,
							null,
							null,
							State.OPEN,
							Priority.NORMAL);

					if(badintComments.get(i) != null && badintComments.get(i).length() > 0) {
						List<Comment> comments = new ArrayList<Comment>();
						Comment c = new Comment(cogrooUser, time, badintComments.get(i), errorEntry, null);
						commentDAO.add(c);
						comments.add(c);
						errorEntry.setComments(comments);
					}

					BadInterventionClassification classification = BadInterventionClassification.fromValue(badint.get(i));

					GrammarCheckerBadIntervention gcBadIntervention = new GrammarCheckerBadIntervention(
							classification,
							badintRule.get(i),
							errorEntry);

					badInterventionDAO.add(gcBadIntervention);
					errorEntry.setBadIntervention(gcBadIntervention);

					setStatus(errorEntry);
					errorEntryDAO.add(errorEntry);
					notificationForReport(errorEntry);
					appData.incReportedErrors();
					if(LOG.isDebugEnabled()) {
						LOG.debug("Added errorEntry:" + errorEntry);
					}
				}
			}
		}


	}

	public List<HistoryEntryField> generateHistory(GrammarCheckerOmission before, GrammarCheckerOmission after) {

		List<HistoryEntryField> h = new ArrayList<HistoryEntryField>();

		if(before == null && after == null) {
			return h;
		} else if(before != null && after == null) {
			if( before.getCategory() != null ) {
				HistoryEntryField category = new HistoryEntryField();
				category.setFieldName(Messages.OMISSION_CATEGORY);
				category.setBefore(before.getCategory());
				h.add(category);
			}

			if( before.getCustomCategory() != null ) {
				HistoryEntryField custom = new HistoryEntryField();
				custom.setFieldName(Messages.OMISSION_CUSTOM_CATEGORY);
				custom.setBefore(before.getCustomCategory());
				h.add(custom);
			}

			if( before.getReplaceBy() != null ) {
				HistoryEntryField replace = new HistoryEntryField();
				replace.setFieldName(Messages.OMISSION_REPLACE_BY);
				replace.setBefore(before.getReplaceBy());
				h.add(replace);
			}
		} else if(before == null && after != null) {
			if( after.getCategory() != null ) {
				HistoryEntryField category = new HistoryEntryField();
				category.setFieldName(Messages.OMISSION_CATEGORY);
				category.setAfter(after.getCategory());
				h.add(category);
			}

			if( after.getCustomCategory() != null ) {
				HistoryEntryField custom = new HistoryEntryField();
				custom.setFieldName(Messages.OMISSION_CUSTOM_CATEGORY);
				custom.setAfter(after.getCustomCategory());
				h.add(custom);
			}

			if( after.getReplaceBy() != null ) {
				HistoryEntryField replace = new HistoryEntryField();
				replace.setFieldName(Messages.OMISSION_REPLACE_BY);
				replace.setAfter(after.getReplaceBy());
				h.add(replace);
			}
		} else {

			if( isDifferentAndNotNull(before.getCategory(), after.getCategory()) ) {
				HistoryEntryField category = new HistoryEntryField();
				category.setFieldName(Messages.OMISSION_CATEGORY);
				if(before != null) {
					category.setBefore(before.getCategory());
				}
				if(after != null) {
					category.setAfter(after.getCategory());
				}

				h.add(category);
			}

			if( isDifferentAndNotNull(before.getCustomCategory(), after.getCustomCategory()) ) {
				HistoryEntryField custom = new HistoryEntryField();
				custom.setFieldName(Messages.OMISSION_CUSTOM_CATEGORY);
				if(before != null) {
					custom.setBefore(before.getCustomCategory());
				}
				if(after != null) {
					custom.setAfter(after.getCustomCategory());
				}

				h.add(custom);
			}

			if( isDifferentAndNotNull(before.getReplaceBy(), after.getReplaceBy()) ) {
				HistoryEntryField replace = new HistoryEntryField();
				replace.setFieldName(Messages.OMISSION_REPLACE_BY);
				if(before != null) {
					replace.setBefore(before.getReplaceBy());
				}
				if(after != null) {
					replace.setAfter(after.getReplaceBy());
				}
				h.add(replace);
			}

		}

		return h;
	}

	public List<HistoryEntryField> generateHistory(GrammarCheckerBadIntervention before, GrammarCheckerBadIntervention after) {

		List<HistoryEntryField> h = new ArrayList<HistoryEntryField>();

		if(before == null && after == null) {
			return h;
		} else if(before != null && after == null) {

			HistoryEntryField rule = new HistoryEntryField();
			rule.setFieldName(Messages.BADINT_RULE);
			rule.setBefore(""+before.getRule());
			h.add(rule);

			if( before.getClassification() != null ) {

				HistoryEntryField classification = new HistoryEntryField();
				classification.setFieldName(Messages.BADINT_CLASSIFICATION);
				classification.setFormatted(true);

				classification.setBefore(""+before.getClassification());

				h.add(classification);
			}
		} else if(before == null && after != null) {

			HistoryEntryField rule = new HistoryEntryField();
			rule.setFieldName(Messages.BADINT_RULE);
			rule.setAfter(""+after.getRule());
			h.add(rule);

			if( after.getClassification() != null ) {

				HistoryEntryField classification = new HistoryEntryField();
				classification.setFieldName(Messages.BADINT_CLASSIFICATION);
				classification.setFormatted(true);

				classification.setAfter(""+after.getClassification());

				h.add(classification);
			}
		} else {
			if( isDifferentAndNotNull(before.getRule(), after.getRule()) ) {

				HistoryEntryField rule = new HistoryEntryField();
				rule.setFieldName(Messages.BADINT_RULE);
				if(before != null) {
					rule.setBefore(""+before.getRule());
				}
				if(after != null) {
					rule.setAfter(""+after.getRule());
				}
				h.add(rule);
			}

			if( isDifferentAndNotNull(before.getClassification(), after.getClassification()) ) {

				HistoryEntryField classification = new HistoryEntryField();
				classification.setFieldName(Messages.BADINT_CLASSIFICATION);
				classification.setFormatted(true);

				if(before != null) {
					classification.setBefore(""+before.getClassification());
				}
				if(after != null) {
					classification.setAfter(""+after.getClassification());
				}

				h.add(classification);
			}
		}

		return h;
	}

	private boolean isDifferentAndNotNull(Object a, Object b) {
		if(	a != null && !a.equals(b) || b != null && !b.equals(a)) {
			return true;
		}
		return false;
	}

	public List<HistoryEntryField> generateHistory(ErrorEntry before, ErrorEntry after) {
		List<HistoryEntryField> h = new ArrayList<HistoryEntryField>();

		String beforeMarkedText = before.getMarkedText();
		String afterMarkedText = after.getMarkedText();
		if(isDifferentAndNotNull(beforeMarkedText, afterMarkedText)) {

			HistoryEntryField markedText = new HistoryEntryField();
			markedText.setFormatted(false);

			markedText.setFieldName(Messages.ERRORENTRY_SELECTEDTEXT);

			if(beforeMarkedText != null) {
				markedText.setBefore(beforeMarkedText);
			}
			if(afterMarkedText != null) {
				markedText.setAfter(afterMarkedText);
			}

			h.add(markedText);
		}

		h.addAll(generateHistory(before.getBadIntervention(), after.getBadIntervention()));
		h.addAll(generateHistory(before.getOmission(), after.getOmission()));

		return h;
	}

	public void updateBadIntervention(ErrorEntry er, ErrorEntry original) {

		if(er.getOmission() != null) {
			this.omissionDAO.delete(er.getOmission());
			er.setOmission(null);
		}
		if(er.getBadIntervention().getId() == null) {
			this.badInterventionDAO.add(er.getBadIntervention());
		} else {
			this.badInterventionDAO.update(er.getBadIntervention());
		}

		this.updateModified(er);
		List<HistoryEntryField> h = generateHistory(original, er);
		HistoryEntry he = this.addHistory(er, h);

		this.errorEntryDAO.update(er);

		notificationForChange(er, he);
	}

	public void updateOmission(ErrorEntry er, ErrorEntry original) {

		if(er.getBadIntervention() != null) {
			this.badInterventionDAO.delete(er.getBadIntervention());
			er.setBadIntervention(null);
		}
		if(er.getOmission().getId() == null) {
			this.omissionDAO.add(er.getOmission());
		} else {
			this.omissionDAO.update(er.getOmission());
		}

		this.updateModified(er);
		List<HistoryEntryField> h = generateHistory(original, er);
		HistoryEntry he = this.addHistory(er, h);
		this.errorEntryDAO.update(er);
		notificationForChange(er, he);
	}

	public Long addCommentToErrorEntry(Long errorEntryID, Long userID, String comment, boolean tweet) {
		ErrorEntry errorEntry = errorEntryDAO.retrieve(errorEntryID);
		User user = userDAO.retrieve(userID);
		Comment c = new Comment(user, new Date(), comment, errorEntry, new ArrayList<Comment>());
		commentDAO.add(c);
		errorEntry.getComments().add(c);
		updateModified(errorEntry);
		errorEntryDAO.update(errorEntry);
		notificationForNewComment(errorEntry, c, tweet);
		return c.getId();
	}

	public void addAnswerToComment(Long commentID, Long userID, String comment) {
		Comment c = commentDAO.retrieve(commentID);
		User user = userDAO.retrieve(userID);

		Comment answer = new Comment(user, new Date(), comment, c, new ArrayList<Comment>());

		commentDAO.add(answer);
		c.getAnswers().add(answer);
		commentDAO.update(c);
		updateModified(c.getErrorEntry());
		errorEntryDAO.update(c.getErrorEntry());
		notificationForNewComment(c.getErrorEntry(), answer, true);
	}

	public void removeAnswer(Comment answer, Comment comment) {

		comment.getAnswers().remove(answer);

		commentDAO.delete(answer);
	}

	public void removeComment(Comment comment) {
		commentDAO.delete(comment);
	}

	public void remove(ErrorEntry errorEntry) {

		LOG.info("Will delete: " + errorEntry);
		if(errorEntry.getBadIntervention() != null) {
			badInterventionDAO.delete(errorEntry.getBadIntervention());
		}
		if(errorEntry.getOmission() != null) {
			omissionDAO.delete(errorEntry.getOmission());
		}
		errorEntryDAO.delete(errorEntry);
		appData.decReportedErrors();
	}

	public void setPriority(ErrorEntry errorEntry, Priority priority) {
		errorEntry = errorEntryDAO.retrieve(errorEntry.getId());
		if(priority.equals(errorEntry.getPriority())) {
			return;
		}
		String before = errorEntry.getPriority().name();
		errorEntry.setPriority(priority);
		HistoryEntry he = addHistory(errorEntry, Messages.ERROR_ENTRY_FIELD_PRIORITY, before, priority.name(), true);
		updateModified(errorEntry);
		errorEntryDAO.update(errorEntry);

		notificationForChange(errorEntry, he);
	}

	public void setState(ErrorEntry errorEntry, State state) {
		errorEntry = errorEntryDAO.retrieve(errorEntry.getId());
		if(state.equals(errorEntry.getState())) {
			return;
		}
		String before = errorEntry.getState().name();
		errorEntry.setState(state);
		HistoryEntry he = addHistory(errorEntry, Messages.ERROR_ENTRY_FIELD_STATE, before, state.name(), true);
		updateModified(errorEntry);
		errorEntryDAO.update(errorEntry);

		notificationForChange(errorEntry, he);
	}

	public void updateModified(ErrorEntry errorEntry) {
		errorEntry.setModified(new Date());
	}

	private HistoryEntry addHistory(ErrorEntry errorEntry, List<HistoryEntryField> hefList) {

		HistoryEntry he = new HistoryEntry(this.user, new Date(), hefList, errorEntry);

		if(hefList.size() == 0) {
			LOG.info("No history to log.");
			return null;
		}

		for (HistoryEntryField historyEntryField : hefList) {
			historyEntryField.setHistoryEntry(he);
			this.historyEntryFieldDAO.add(historyEntryField);
		}

		this.historyEntryDAO.add(he);

		List<HistoryEntry> heList = errorEntry.getHistoryEntries();
		if(heList == null) {
			heList = new ArrayList<HistoryEntry>();
			errorEntry.setHistoryEntries(heList);
		}

		errorEntry.getHistoryEntries().add(he);
		this.errorEntryDAO.update(errorEntry);

		if(LOG.isDebugEnabled()) {
			LOG.debug("Added history entry...");
			LOG.debug(he);
		}

		return he;
	}

	private HistoryEntry addHistory(ErrorEntry errorEntry,
			List<String> fieldList, List<String> beforeList, List<String> afterList, boolean isFormatted) {

		if(fieldList.size() == 0) {
			LOG.info("No history to log.");
			return null;
		}

		List<HistoryEntryField> hefList = new ArrayList<HistoryEntryField>();


		HistoryEntry he = new HistoryEntry(this.user, new Date(), hefList, errorEntry);

		for(int i = 0; i < fieldList.size(); i++) {
			HistoryEntryField h = new HistoryEntryField(
					he,
					fieldList.get(i),
					beforeList.get(i),
					afterList.get(i),
					isFormatted);
			this.historyEntryFieldDAO.add(h);
			hefList.add(h);
		}

		this.historyEntryDAO.add(he);

		List<HistoryEntry> heList = errorEntry.getHistoryEntries();
		if(heList == null) {
			heList = new ArrayList<HistoryEntry>();
			errorEntry.setHistoryEntries(heList);
		}

		errorEntry.getHistoryEntries().add(he);
		this.errorEntryDAO.update(errorEntry);

		return he;
	}

	private HistoryEntry addHistory(ErrorEntry errorEntry,
			String field, String before, String after, boolean isFormatted) {

		List<String> fields = new ArrayList<String>(1);
		List<String> befores = new ArrayList<String>(1);
		List<String> afters = new ArrayList<String>(1);

		fields.add(field);
		befores.add(before);
		afters.add(after);

		return addHistory(errorEntry, fields, befores, afters, isFormatted);
	}

	private void appendErrorDetails(StringBuilder body, ErrorEntry errorEntry) {
		StringTemplate st = this.templateUtil.getTemplate(StringTemplateUtil.ERROR_DETAILS);

		st.setAttribute("problemID", errorEntry.getId());
		st.setAttribute("submitter", errorEntry.getSubmitter().getName());
		st.setAttribute("text", errorEntry.getMarkedTextNoCSS());
		st.setAttribute("root", BuildUtil.BASE_URL);

		if(errorEntry.getBadIntervention() != null) {
			GrammarCheckerBadIntervention bi = errorEntry.getBadIntervention();

			st.setAttribute("type", "Intervenção indevida");

			st.setAttribute("categoryOrRule", "Regra");
			st.setAttribute("valueCategoryOrRule", bi.getRule());

			st.setAttribute("errorTypeOrReplaceBy", "Erro");
			st.setAttribute("valueErrorTypeOrReplaceBy", messages.getString(bi.getClassification().toString()));
		} else {
			GrammarCheckerOmission o = errorEntry.getOmission();
			String category = null;
			if(o.getCategory() == null || o.getCategory().equals(CUSTOM)) {
				category = o.getCustomCategory();
			} else {
				category = o.getCategory();
			}

			st.setAttribute("type", "Omissão");

			st.setAttribute("categoryOrRule", "Categoria");
			st.setAttribute("valueCategoryOrRule", category);

			st.setAttribute("errorTypeOrReplaceBy", "Substituir por");
			st.setAttribute("valueErrorTypeOrReplaceBy", o.getReplaceBy());

		}

		body.append(st.toString());
	}

	private void notificationForReport(ErrorEntry errorEntry) {
		// only RSS
		// generate the body
		StringBuilder body = new StringBuilder();
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will create templates report: " + errorEntry);
		}
		StringTemplate st = this.templateUtil.getTemplate(StringTemplateUtil.ERROR_NEW);
		st.setAttribute("user", errorEntry.getSubmitter().getName());
		if(errorEntry.getComments() != null && errorEntry.getComments().size() > 0) {
			st.setAttribute("comment", errorEntry.getComments().get(0).getComment());
		}


		body.append(st.toString());

		appendErrorDetails(body, errorEntry);

		//RSS
		String subject = "Problema Reportado #" + errorEntry.getId() + " - Novo";
		String url = BuildUtil.BASE_URL + "reports/" + errorEntry.getId();
		notificator.rssFeed(subject, url, body.toString());

		StringTemplate stTweet = this.templateUtil.getTemplate(StringTemplateUtil.ERROR_NEW_TWEET);

		stTweet.setAttribute("user", errorEntry.getSubmitter().getTwitterRefOrName());
		stTweet.setAttribute("id", errorEntry.getId());
		stTweet.setAttribute("type", getEntryType(errorEntry));
		stTweet.setAttribute("text", errorEntry.getMarkedTextNoHTML());

		notificator.tweet(stTweet.toString(), url);

	}

	private void notificationForNewComment(ErrorEntry errorEntry, Comment comment, boolean tweet) {
		// get the users
		Set<User> userList = createToList(errorEntry);
		// generate the subject
		String subject = "Problema Reportado #" + errorEntry.getId() + " - Novo comentário";
		// generate the body
		StringBuilder body = new StringBuilder();

		StringTemplate st = this.templateUtil.getTemplate(StringTemplateUtil.NEW_COMMENT);
		st.setAttribute("user", comment.getUser().getName());
		st.setAttribute("comment", comment.getComment());

		body.append(st.toString());

		appendErrorDetails(body, errorEntry);

		// send it!
		notificator.sendEmail(StringEscapeUtils.unescapeHtml(body.toString()), subject, userList);

		//RSS
//		String friendlyStart = "Novo comentário de " + comment.getUser().getName() + " no problema " + errorEntry.getId();
		String url =  BuildUtil.BASE_URL + REPORTS + errorEntry.getId();
		notificator.rssFeed(subject, url, body.toString());

		if(tweet) {
		  StringTemplate stTweet = this.templateUtil.getTemplate(StringTemplateUtil.NEW_COMMENT_TWEET);

		  stTweet.setAttribute("user", comment.getUser().getTwitterRefOrName());
		  stTweet.setAttribute("ori", errorEntry.getSubmitter().getTwitterRefOrName());
		  stTweet.setAttribute("id", errorEntry.getId());
		  stTweet.setAttribute("type", getEntryType(errorEntry));
		  stTweet.setAttribute("comment", comment.getComment());

		  notificator.tweet(stTweet.toString(), url);
		}
	}

	private static final ResourceBundle messages =
	      ResourceBundle.getBundle("messages", new Locale("pt_BR"));

	private String replaceSpan(String span) {
		if(span == null) {
			return null;
		}
		span = span.replace("class=\"badint\"", "style='background-color: #ADFF2F'\"");
		return span.replace("class=\"omission\"", "style='background-color: #FA8072'\"");
	}

	private HistoryEntryField process(HistoryEntryField field) {
		HistoryEntryField c = new HistoryEntryField();
		c.setFieldName(messages.getString(field.getFieldName()));
		if(field.getIsFormatted()) {
			if(field.getBefore() != null) {
				c.setBefore(messages.getString(field.getBefore()));
			}
			if(field.getAfter() != null) {
				c.setAfter(messages.getString(field.getAfter()));
			}
		} else {
			c.setBefore(replaceSpan(field.getBefore()));
			c.setAfter(replaceSpan(field.getAfter()));
		}

		return c;
	}

	private String getEntryType(ErrorEntry errorEntry) {
		if(errorEntry.getBadIntervention() != null) {
			return "Intervenção indevida";
		}
		return "Omissão";
	}

	private void notificationForChange(ErrorEntry errorEntry, HistoryEntry historyEntry) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Sending notification for " + historyEntry);
		}
		if(historyEntry == null || historyEntry.getHistoryEntryField() == null || historyEntry.getHistoryEntryField().size() == 0) {
			// no changes
			return;
		}

		// get the users
		Set<User> userList = createToList(errorEntry);
		if(LOG.isDebugEnabled()) {
			LOG.debug("Will send email for #" + userList + " users.");
		}
		// generate the subject
		String subject = "Problema Reportado #" + errorEntry.getId() + " - Alterado";
		// generate the body
		StringBuilder body = new StringBuilder();

		StringTemplate stFeed = this.templateUtil.getTemplate(StringTemplateUtil.ERROR_CHANGED);
		StringTemplate stTweet = this.templateUtil.getTemplate(StringTemplateUtil.ERROR_CHANGED_TWEET);

		stFeed.setAttribute("user", historyEntry.getUser().getName());

		for (HistoryEntryField f : historyEntry.getHistoryEntryField()) {
			f = process(f);
			stFeed.setAttribute("changes", f);
			stTweet.setAttribute("changes", f);
		}

		body.append(stFeed.toString());

		appendErrorDetails(body, errorEntry);

		// send it!
		notificator.sendEmail(StringEscapeUtils.unescapeHtml(body.toString()), subject, userList);

		String url = BuildUtil.BASE_URL + REPORTS + errorEntry.getId();

		//RSS
		notificator.rssFeed(subject, url, body.toString());

		stTweet.setAttribute("ori", errorEntry.getSubmitter().getTwitterRefOrName());
		stTweet.setAttribute("user", historyEntry.getUser().getTwitterRefOrName());
		stTweet.setAttribute("id", errorEntry.getId());
		stTweet.setAttribute("type", getEntryType(errorEntry));

		notificator.tweet(stTweet.toString(), url);
	}

	private Set<User> createToList(ErrorEntry errorEntry) {
		Set<User> userList = new HashSet<User>();
		if(errorEntry.getSubmitter().getIsReceiveEmail()) {
			addUserIfIsReceveMail(errorEntry.getSubmitter(), userList);
		}
		for (HistoryEntry h : errorEntry.getHistoryEntries()) {
			addUserIfIsReceveMail(h.getUser(), userList);
		}
		for (Comment c : errorEntry.getComments()) {
			addUserIfIsReceveMail(c.getUser(), userList);
			for (Comment a : c.getAnswers()) {
				addUserIfIsReceveMail(a.getUser(), userList);
			}
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("Added " + userList.size() + " to TO list.");
			LOG.debug("The users are: " + Arrays.toString(userList.toArray()));
		}
		return userList;
	}

	private void addUserIfIsReceveMail(User user, Set<User> userList){
		if(user.getIsReceiveEmail() && user.getEmail() != null) {
			userList.add(user);
		}
	}

	public void multipleEdit(List<ErrorEntry> entries, Priority priorityEnum,
			State stateEnum, String comment) {
		LOG.info("Will update entrie: ");
		for (ErrorEntry errorEntry : entries) {
			LOG.info(errorEntry.getId());
		}
		LOG.info("Will add comment: " + comment);
		LOG.info("Set priority " + priorityEnum);
		LOG.info("State: " + stateEnum);

		for (ErrorEntry errorEntry : entries) {
			if(priorityEnum != null) {
				errorEntry.setPriority(priorityEnum);
			}
			if(stateEnum != null) {
				errorEntry.setState(stateEnum);
			}
			if(comment != null && comment.length() > 0) {
				addCommentToErrorEntry(errorEntry.getId(), this.user.getId(), comment, false);
			}
		}

	}

	public void refreshReports() {
	  List<ErrorEntry> list = errorEntryDAO.listAll();

	  for (ErrorEntry report : list) {
	    setStatus(report);
	    errorEntryDAO.update(report);
	  }
	}

public void setStatus(ErrorEntry report) {

   GrammarCheckerBadIntervention badIntervention = report.getBadIntervention();
   List<ProcessResult> results = cogrooFacade.processText(report.getText());

   if (report.getState().equals(State.REJECTED)) {
     report.setStatusFlag(STATUS_INVALID);
   }
   else {

     if (badIntervention != null) {
       for (ProcessResult result : results) {
         List<Mistake> mistakes = result.getMistakes();

         if (!mistakes.isEmpty()) {

           for (Mistake mistake : mistakes) {
             if (badIntervention.getRule().equals(mistake.getRuleIdentifier())) {
               report.setStatusFlag(STATUS_NOT);
               break;
             }
             else {
               report.setStatusFlag(STATUS_WARN);
             }

           }
         }
         else {
           report.setStatusFlag(STATUS_OK);
         }
       }
     }
     else {

       GrammarCheckerOmission omission = report.getOmission();

       if (omission != null) {

         for (ProcessResult result : results) {
           List<Mistake> mistakes = result.getMistakes();

           if (!mistakes.isEmpty()) {

             boolean status = false;

             for (Mistake mistake : mistakes) {

               RuleDefinition rule = rulesLogic.getRule(mistake.getRuleIdentifier());

               if(rule == null) {
                 LOG.warn("Got null rule for id: " + mistake.getRuleIdentifier());
             } else if ( Objects.equal(rule.getCategory(), omission.getCategory()) ) {
               status = true;
             }

           }
           if (status == true) {
             report.setStatusFlag(STATUS_OK);
           }
           else {
             report.setStatusFlag(STATUS_WARN);
           }
         }
         else {
           report.setStatusFlag(STATUS_NOT);
         }
       }
     }
   }
 }
}

  public ReportStats getStats() {
    ReportStats stats = new ReportStats(errorEntryDAO.listAll());
    return stats;
  }

}
