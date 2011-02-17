package br.usp.ime.cogroo.controller;

import java.io.File;
import java.util.Calendar;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.exceptions.Messages;
import br.usp.ime.cogroo.model.ApplicationData;

/**
 * @author Michel
 */
@Resource
public class StatsController {
	
	private static final Logger LOG = Logger.getLogger(StatsController.class);

	private static final int N = 10;

	private final Result result;
	private final ApplicationData appData;

	private final UserDAO userDAO;

	private Validator validator;

	public StatsController(Result result, ApplicationData appData,
			UserDAO userDAO, Validator validator) {
		this.result = result;
		this.appData = appData;
		this.userDAO = userDAO;
		this.validator = validator;
	}

	@Get
	@Path("/stats")
	public void stats() {
		try {
			Calendar monthAgo = Calendar.getInstance();
			monthAgo.add(Calendar.MONTH, -1);
	
			appData.setIdleUsers(userDAO.retrieveIdleUsers(
					monthAgo.getTimeInMillis(), N));
			appData.setTopUsers(userDAO.retrieveTopUsers(
					monthAgo.getTimeInMillis(), N));
	
			result.include("appData", appData);
		} catch(Exception e) {
			LOG.error(Messages.ERROR_LOADING_STATS, e);
			validator.add(new ValidationMessage(Messages.ERROR_LOADING_STATS,
					Messages.ERROR));
			validator.onErrorUse(Results.page()).of(IndexController.class)
					.index();
		}
	}

	@Get
	@Path("/stats/EstatisticasCogrooComunidade.csv")
	public File download() {
		return appData.getCsvStatsFile();
	}
}
