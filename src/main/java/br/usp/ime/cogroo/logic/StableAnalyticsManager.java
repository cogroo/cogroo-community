package br.usp.ime.cogroo.logic;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.util.BuildUtil;

import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.client.analytics.DataQuery;
import com.google.gdata.data.analytics.DataEntry;
import com.google.gdata.data.analytics.DataFeed;
import com.google.gdata.data.analytics.Metric;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * 
 * @author Michel
 * 
 */
@Component
@ApplicationScoped
public class StableAnalyticsManager implements AnalyticsManager, Serializable {

  private static final long serialVersionUID = -8942593409150938968L;

  private AnalyticsService analyticsService;

	public StableAnalyticsManager() {
		analyticsService = new AnalyticsService(BuildUtil.APP_NAME);
		authenticate(BuildUtil.ANALYTICS_USR, BuildUtil.ANALYTICS_PWD);
	}

	public StableAnalyticsManager(String appName, String username,
			String password) {
		analyticsService = new AnalyticsService(appName);
		authenticate(username, password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.usp.ime.cogroo.logic.AnalyticsManager#authenticate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public synchronized void authenticate(String username, String password) {
		try {
			analyticsService.setUserCredentials(BuildUtil.ANALYTICS_USR,
					BuildUtil.ANALYTICS_PWD);
		} catch (AuthenticationException e) {
			System.err.println("Authentication failed : " + e.getMessage());
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.usp.ime.cogroo.logic.AnalyticsManager#getData(java.lang.String,
	 * java.util.List, java.util.List, java.util.Date, java.util.Date)
	 */
	@Override
	public synchronized DataFeed getData(String ids, List<String> metrics,
			List<String> dimensions, Date startDate, Date endDate) {
		try {
			DataQuery query = new DataQuery(new URL(
					"https://www.google.com/analytics/feeds/data"));
			query.setStartDate(DATE_FORMAT.format(startDate));
			query.setEndDate(DATE_FORMAT.format(endDate));

			StringBuffer sb = new StringBuffer();
			for (String d : dimensions) {
				sb.append(d);
				sb.append(',');
			}
			query.setDimensions(sb.substring(0, sb.length() - 1));

			sb = new StringBuffer();
			for (String m : metrics) {
				sb.append(m);
				sb.append(',');
			}
			query.setMetrics(sb.substring(0, sb.length() - 1));

			query.setIds(ids);

			return analyticsService.getFeed(query.getUrl(), DataFeed.class);
		} catch (IOException e) {
			System.err.println("Network error trying to retrieve feed: "
					+ e.getMessage());
			return null;
		} catch (ServiceException e) {
			System.err
					.println("Analytics API responded with an error message: "
							+ e.getMessage());
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.usp.ime.cogroo.logic.AnalyticsManager#getDatedMetricsAsString(br.usp
	 * .ime.cogroo.model.DataFeed)
	 */
	@Override
	public String getDatedMetricsAsString(DataFeed dataFeed) {
		Calendar start = Calendar.getInstance();
		String startString = dataFeed.getStartDate().getValue();

		Date startDate = null;
		try {
			startDate = DATE_FORMAT.parse(startString);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		start.setTime(startDate);

		StringBuffer sb = new StringBuffer();
		for (DataEntry e : dataFeed.getEntries()) {
			sb.append(DATE_FORMAT.format(start.getTime()));
			start.add(Calendar.DATE, 1);
			sb.append(',');
			for (Metric m : e.getMetrics()) {
				sb.append(m.getValue());
				sb.append(',');
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(';');
		}
		return sb.substring(0, sb.length() - 1);
	}

	// Copyright 2009 Google Inc. All Rights Reserved.
	private static final String GA_ACCOUNT = "MO-18985930-2";
	private static final String GA_PIXEL = "ga.jspf";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.usp.ime.cogroo.logic.AnalyticsManager#googleAnalyticsGetImageUrl(javax
	 * .servlet.http.HttpServletRequest)
	 */
	@Override
	public String googleAnalyticsGetImageUrl(HttpServletRequest request)
			throws UnsupportedEncodingException {
		StringBuilder url = new StringBuilder();
		url.append(GA_PIXEL + "?");
		url.append("utmac=").append(GA_ACCOUNT);
		url.append("&utmn=").append(
				Integer.toString((int) (Math.random() * 0x7fffffff)));

		String referer = request.getHeader("referer");
		String query = request.getQueryString();
		String path = request.getRequestURI();

		if (referer == null || "".equals(referer)) {
			referer = "-";
		}
		url.append("&utmr=").append(URLEncoder.encode(referer, "UTF-8"));

		if (path != null) {
			if (query != null) {
				path += "?" + query;
			}
			url.append("&utmp=").append(URLEncoder.encode(path, "UTF-8"));
		}

		url.append("&guid=ON");

		return url.toString();
	}

	static class StressTest implements Runnable {

		static final String IDS = "ga:38929232";

		static final ArrayList<String> METRICS = new ArrayList<String>(2);
		{
			METRICS.add("ga:totalEvents");
			METRICS.add("ga:visits");
			METRICS.add("ga:pageviews");
		}

		static final ArrayList<String> DIMENSIONS = new ArrayList<String>(1);
		{
			DIMENSIONS.add("ga:date");
		}

		static final Calendar LAUNCH_DAY = Calendar.getInstance();
		{
			LAUNCH_DAY.clear();
			LAUNCH_DAY.set(2010, 10, 10);
		}

		static Calendar twoDaysAgo = Calendar.getInstance();
		static {
			twoDaysAgo.add(Calendar.DATE, -2);
		}

		private AnalyticsManager manager;
		private DataFeed dataFeed;

		public StressTest(AnalyticsManager manager) {
			this.manager = manager;
		}

		@Override
		public void run() {
			dataFeed = this.manager.getData(IDS, METRICS, DIMENSIONS,
					LAUNCH_DAY.getTime(), twoDaysAgo.getTime());

		}
	}

	public static void main(String[] args) throws InterruptedException {
		StableAnalyticsManager manager = new StableAnalyticsManager();

		StressTest st = new StressTest(manager);

		final int NUM_THREADS = 1;
		ArrayList<Thread> threads = new ArrayList<Thread>(NUM_THREADS);

		for (int i = 0; i < NUM_THREADS; i++) {
			Thread t = new Thread(st);
			t.start();
			threads.add(t);
		}
		
		DataFeed dataFeed = null;
		for (Thread t : threads) {
			t.join();
			dataFeed = st.dataFeed;
		}

		for (DataEntry entry : dataFeed.getEntries()) {
			System.out.println("Date: " + entry.stringValueOf("ga:date"));
			System.out.println("Events: "
					+ entry.stringValueOf("ga:totalEvents"));
			System.out.println("Visits: " + entry.stringValueOf("ga:visits"));
			System.out.println("Pageviews: "
					+ entry.stringValueOf("ga:pageviews"));
			System.out.println();
		}
	}
}