package br.usp.ime.cogroo.logic;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gdata.data.analytics.DataFeed;

/**
 * 
 * @author Michel
 * 
 */
public interface AnalyticsManager {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	public abstract DataFeed getData(String ids, List<String> metrics,
			List<String> dimensions, Date startDate, Date endDate);

	public abstract void authenticate(String username, String password);

	/**
	 * Export metrics from a data feed on a format readable by javascript. The
	 * data feed must be obtained from an Analytics query containing "ga:date"
	 * as one dimension.
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