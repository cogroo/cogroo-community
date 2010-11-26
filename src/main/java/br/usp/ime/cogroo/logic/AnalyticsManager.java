package br.usp.ime.cogroo.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.model.DataFeed;
import br.usp.ime.cogroo.model.DataFeed.DataEntry;
import br.usp.ime.cogroo.model.DataFeed.Metric;
import br.usp.ime.cogroo.util.BuildUtil;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;
import com.google.api.client.xml.XmlHttpParser;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.atom.AtomParser;

/**
 * 
 * @author Michel
 * 
 */
@Component
@ApplicationScoped
public class AnalyticsManager {

	private HttpTransport transport;

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static final XmlNamespaceDictionary DICTIONARY = new XmlNamespaceDictionary();
	static {
		Map<String, String> map = DICTIONARY.namespaceAliasToUriMap;
		map.put("", "http://www.w3.org/2005/Atom");
		map.put("atom", "http://www.w3.org/2005/Atom");
		map.put("batch", "http://schemas.google.com/gdata/batch");
		map.put("gAcl", "http://schemas.google.com/acl/2007");
		map.put("gCal", "http://schemas.google.com/gCal/2005");
		map.put("gd", "http://schemas.google.com/g/2005");
		map.put("georss", "http://www.georss.org/georss");
		map.put("gml", "http://www.opengis.net/gml");
		map.put("openSearch", "http://a9.com/-/spec/opensearch/1.1/");
		map.put("xml", "http://www.w3.org/XML/1998/namespace");
	}

	public static class AnalyticsUrl extends GoogleUrl {
		@Key
		String ids; // Required
		@Key
		String dimensions;
		@Key
		String metrics; // Required
		@Key
		String sort;
		@Key
		String filters;
		@Key
		String segment;
		@Key("start-date")
		String startDate; // Required
		@Key("end-date")
		String endDate; // Required
		@Key("start-index")
		Integer startIndex;
		@Key("max-results")
		Integer maxResults;
		@Key
		Integer v;

		public void setDimensions(List<String> dimensions) {
			this.dimensions = new String();
			for (String d : dimensions) {
				this.dimensions += "," + d;
			}
			this.dimensions = this.dimensions.substring(1);
		}

		public void setMetrics(List<String> metrics) {
			this.metrics = new String();
			for (String m : metrics) {
				this.metrics += "," + m;
			}
			this.metrics = this.metrics.substring(1);
		}

		public void setStartDate(Date date) {
			this.startDate = DATE_FORMAT.format(date);
		}

		public void setEndDate(Date date) {
			this.endDate = DATE_FORMAT.format(date);
		}

		public AnalyticsUrl(String encodedUrl) {
			super(encodedUrl);
		}
	}

	public AnalyticsManager() {
		this.transport = setUpTransport(BuildUtil.APP_NAME);
		authenticate(BuildUtil.ANALYTICS_USR, BuildUtil.ANALYTICS_PWD);
	}

	public AnalyticsManager(String appName, String username, String password) {
		this.transport = setUpTransport(appName);
		authenticate(username, password);
	}

	public static HttpTransport setUpTransport(String appName) {
		HttpTransport transport = GoogleTransport.create();
		GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
		headers.setApplicationName(appName);
		headers.gdataVersion = "2";
		XmlHttpParser parser = new AtomParser();
		parser.namespaceDictionary = DICTIONARY;
		transport.addParser(parser);
		return transport;
	}

	public HttpRequest setUpDataRequest(String ids, List<String> metrics,
			List<String> dimensions, Date startDate, Date endDate) {
		HttpRequest dataRequest = transport.buildGetRequest();

		AnalyticsUrl url = new AnalyticsUrl(
				"https://www.google.com/analytics/feeds/data");
		url.ids = ids;
		url.setMetrics(metrics);
		url.setDimensions(dimensions);
		url.setStartDate(startDate);
		url.setEndDate(endDate);
		// url.prettyprint = true;

		dataRequest.url = url;

		return dataRequest;
	}

	public synchronized void authenticate(String username, String password) {
		ClientLogin authenticator = new ClientLogin();
		authenticator.authTokenType = "analytics";
		authenticator.username = username;
		authenticator.password = password;
		try {
			authenticator.authenticate().setAuthorizationHeader(transport);
		} catch (HttpResponseException e) {
			if (e.getMessage().equals("403 Forbidden")) {
				System.err
						.println("Did you set the Analytics username and password at build time?");
				e.printStackTrace();
			} else
				e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized DataFeed getData(String ids, List<String> metrics,
			List<String> dimensions, Date startDate, Date endDate) {
		HttpRequest request = setUpDataRequest(ids, metrics, dimensions,
				startDate, endDate);

		DataFeed feed = null;
		try {
			feed = request.execute().parseAs(DataFeed.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return feed;
	}

	/**
	 * Export metrics from a data feed on a format readable by javascript. The
	 * data feed must be obtained from an Analytics query containing "ga:date"
	 * as one adimension.
	 * 
	 * @param dataFeed
	 *            A data feed obtained from an Analytics query.
	 * @return A string separated by ',' and ';' as the pattern:
	 *         d1,m11,m12,[..],m1k;d2,m21,m22,[..],m2k;[..];dj,mj1,mj2,[..],mjk
	 */
	public String getDatedMetricsAsString(DataFeed dataFeed) {
		Calendar start = Calendar.getInstance();
		start.setTime(dataFeed.getStartDate());

		StringBuffer sb = new StringBuffer();
		for (DataEntry e : dataFeed.answers) {
			sb.append(AnalyticsManager.DATE_FORMAT.format(start.getTime()));
			start.add(Calendar.DATE, 1);
			sb.append(',');
			for (Metric m : e.metrics) {
				sb.append(m.value);
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

	public static void main(String[] args) {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, -1);
		Calendar monthAgo = (Calendar) today.clone();
		monthAgo.add(Calendar.MONTH, -1);

		AnalyticsManager manager = new AnalyticsManager("CoGrOO Comunidade",
				BuildUtil.ANALYTICS_USR, BuildUtil.ANALYTICS_PWD);

		ArrayList<String> metrics = new ArrayList<String>(2);
		metrics.add("ga:visits");
		metrics.add("ga:pageviews");
		metrics.add("ga:totalEvents");

		ArrayList<String> dimensions = new ArrayList<String>(1);
		dimensions.add("ga:date");

		DataFeed feed = manager.getData("ga:38929232", metrics, dimensions,
				monthAgo.getTime(), today.getTime());

		System.out.println(feed.title);
		System.out.println(feed.totalResults);
		System.out.println(feed.startDate);
		System.out.println(feed.endDate);
		List<DataEntry> answers = feed.answers;
		for (DataEntry a : answers) {
			System.out.println("................." + a.title);
			for (Metric m : a.metrics) {
				System.out.println(m.name);
				System.out.println(m.type);
				System.out.println(m.confidenceInterval);
				System.out.println(m.value);
			}
		}
	}
}
