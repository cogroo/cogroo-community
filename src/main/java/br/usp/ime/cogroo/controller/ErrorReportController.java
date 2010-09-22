package br.usp.ime.cogroo.controller;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.usp.ime.cogroo.Util.RestUtil;
import br.usp.ime.cogroo.dao.ErrorReportDAO;
import br.usp.ime.cogroo.logic.ErrorReportLogic;
import br.usp.ime.cogroo.logic.SecurityUtil;
import br.usp.ime.cogroo.model.ErrorReport;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.pcs.lta.cogroo.errorreport.ErrorReportAccess;


@Resource
public class ErrorReportController {

	private static final Logger LOG = Logger
			.getLogger(ErrorReportController.class);

	private LoggedUser loggedUser;
	private ErrorReportLogic errorReportLogic;
	private Result result;
	private Validator validator;
	private ErrorReportDAO errorReportDAO;
	private SecurityUtil securityUtil; 
	
	public ErrorReportController(
			LoggedUser loggedUser, 
			ErrorReportLogic errorReportLogic,
			Result result,
			Validator validator,
			ErrorReportDAO errorReportDAO,
			SecurityUtil securityUtil) {
		this.loggedUser = loggedUser;
		this.errorReportLogic = errorReportLogic;
		this.result = result;
		this.validator = validator;
		this.errorReportDAO = errorReportDAO;
		this.securityUtil = securityUtil;
	}
	
	/**
	 * Gets error report from Cogroo Add-On
	 * @param userName an user name
	 * @param text a sample text
	 * @param comment comments about the issue
	 * @param version the Cogroo Add-on version
	 */
	@Post
	@Path("/cogrooErrorReport")
	public void addErrorEntry(String userName, String text, String comment, String version) {
		LOG.debug("Got new error report from: " + userName +
				" text: " + text +
				" comment: " + comment +
				" version: " + version);
		errorReportLogic.addErrorEntry(userName, text, comment, version);
		
	}
	
	@Post
	@Path("/submitErrorReport")
	public void submitErrorReport(String username, String token, String error) {
		
		error = securityUtil.decodeURLSafeString(error);
		
		LOG.debug("Got new error report from: " + username +
				" encrypted token: " + token +
				" error: " + error );
		String link = errorReportLogic.addErrorEntry(username, error);
		
		result.include("result", RestUtil.prepareResponse("result",link));
	}
	
	@Post
	@Path("/getErrorCategoriesForUser")
	public void getErrorCategoriesForUser(String username, String token) {
		LOG.debug("getErrorCategoriesForUser: " + username +
				" encrypted token: " + token);
		
		try {
			LOG.info("Will get rules for user.");
			Set<String> cat = this.errorReportLogic.getErrorCategoriesForUser(username);
			StringBuilder sb = new StringBuilder();
			Iterator<String> it = cat.iterator();
			while (it.hasNext()) {
				sb.append(it.next() + "|");
			}
			
			LOG.debug("Categories: " + sb);
			result.include("result", RestUtil.prepareResponse("categories",sb.toString()));
		} catch (Exception e) {
			LOG.error("Error getting categories", e);
		}
	}
	
	@Get
	@Path("/errorReports")
	public void list() {
		List<ErrorReport> reports = errorReportLogic.getAllReports();
		LOG.debug("Will list of size: "
				+ reports.size());
		result.include("errorReportList", reports);
	}
	
	@Post
	@Path("/errorReport")
	public void details(String errorReportID) {
		LOG.debug("Details for: " + errorReportID);
		result.include("errorReport", errorReportDAO.retrieve(new Long(errorReportID)));
	}
	
	@Post
	@Path("/errorReportAddComment")
	public void details(String errorReportID, String newComment) {
		LOG.debug("Details for: " + errorReportID);
		errorReportLogic.addComment(errorReportID, newComment);
		result.include("errorReport", errorReportDAO.retrieve(new Long(errorReportID)));
	}
	
}
