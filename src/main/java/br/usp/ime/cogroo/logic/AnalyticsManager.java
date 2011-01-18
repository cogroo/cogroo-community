package br.usp.ime.cogroo.logic;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.usp.ime.cogroo.model.DataFeed;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.xml.XmlNamespaceDictionary;

public interface AnalyticsManager {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final XmlNamespaceDictionary DICTIONARY = new XmlNamespaceDictionary();

	public abstract HttpRequest setUpDataRequest(String ids,
			List<String> metrics, List<String> dimensions, Date startDate,
			Date endDate);

	public abstract void authenticate(String username, String password);

	public abstract DataFeed getData(String ids, List<String> metrics,
			List<String> dimensions, Date startDate, Date endDate);

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
	public abstract String getDatedMetricsAsString(DataFeed dataFeed);

	public abstract String googleAnalyticsGetImageUrl(HttpServletRequest request)
			throws UnsupportedEncodingException;

}