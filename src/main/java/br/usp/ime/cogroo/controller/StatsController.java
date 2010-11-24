package br.usp.ime.cogroo.controller;

import java.util.ArrayList;
import java.util.Calendar;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.logic.AnalyticsManager;
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.DataFeed;

/**
 * @author Michel
 */
@Resource
public class StatsController {

	private static final String IDS = "ga:38929232";
	private static final ArrayList<String> METRICS = new ArrayList<String>(2);
	static {
		METRICS.add("ga:totalEvents");
		METRICS.add("ga:visits");
		METRICS.add("ga:pageviews");
	}

	private static final ArrayList<String> DIMENSIONS = new ArrayList<String>(1);
	static {
		DIMENSIONS.add("ga:date");
	}

	private static final int N = 10;

	private final Result result;
	private final ApplicationData appData;

	private final UserDAO userDAO;

	private final AnalyticsManager manager;

	public StatsController(Result result, ApplicationData appData,
			UserDAO userDAO, AnalyticsManager manager) {
		this.result = result;
		this.appData = appData;
		this.userDAO = userDAO;
		this.manager = manager;
	}

	@Get
	@Path("/stats")
	public void stats() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, -1);
		Calendar monthAgo = (Calendar) now.clone();
		monthAgo.add(Calendar.MONTH, -2);

		DataFeed feed = manager.getData(IDS, METRICS, DIMENSIONS,
				monthAgo.getTime(), now.getTime());
		String metrics = manager.getDatedMetricsAsString(feed);

		appData.setIdleUsers(userDAO.retrieveIdleUsers(
				monthAgo.getTimeInMillis(), N));
		appData.setTopUsers(userDAO.retrieveTopUsers(
				monthAgo.getTimeInMillis(), N));

		result.include("metrics", metrics).include("appData", appData);
	}
}
