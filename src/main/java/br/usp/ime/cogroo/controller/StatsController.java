package br.usp.ime.cogroo.controller;

import java.io.File;
import java.util.Calendar;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.model.ApplicationData;

/**
 * @author Michel
 */
@Resource
public class StatsController {

	private static final int N = 10;

	private final Result result;
	private final ApplicationData appData;

	private final UserDAO userDAO;

	public StatsController(Result result, ApplicationData appData,
			UserDAO userDAO) {
		this.result = result;
		this.appData = appData;
		this.userDAO = userDAO;
	}

	@Get
	@Path("/stats")
	public void stats() {
		Calendar monthAgo = Calendar.getInstance();
		monthAgo.add(Calendar.MONTH, -1);

		appData.setIdleUsers(userDAO.retrieveIdleUsers(
				monthAgo.getTimeInMillis(), N));
		appData.setTopUsers(userDAO.retrieveTopUsers(
				monthAgo.getTimeInMillis(), N));

		result.include("appData", appData);
	}

	@Get
	@Path("/stats/EstatisticasCogrooComunidade.csv")
	public File download() {
		return appData.getCsvStatsFile();
	}
}
