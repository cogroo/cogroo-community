package br.usp.ime.cogroo.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.logic.AnalyticsManager;
import br.usp.ime.cogroo.util.BuildUtil;

@Component
@ApplicationScoped
public class ApplicationData {

	private boolean initialized = false;

	private AnalyticsManager manager;
	
	private AtomicInteger events = new AtomicInteger();
	private AtomicInteger visits = new AtomicInteger();
	private AtomicInteger pageviews = new AtomicInteger();

	private AtomicInteger reportedErrors = new AtomicInteger();
	private AtomicInteger dictionaryEntries = new AtomicInteger();
	private AtomicInteger registeredMembers = new AtomicInteger();

	private AtomicInteger onlineUsers = new AtomicInteger();

	private List<User> loggedUsers = new ArrayList<User>();
	private List<User> idleUsers = new ArrayList<User>();
	private List<User> topUsers = new ArrayList<User>();

	private Calendar lastUpdated;
	private File csvFolder;
	private File csvStatsFile;
	private String temporalData;
	
	private static final Calendar LAUNCH_DAY = Calendar.getInstance();
	static {
		LAUNCH_DAY.clear();
		LAUNCH_DAY.set(2010, 10, 10);
	}

	private static final long ONE_DAY = 24 * 60 * 60 * 1000L;

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

	public ApplicationData(AnalyticsManager manager, ServletContext context) {
		this.manager = manager;
		csvFolder = new File(context.getRealPath("/WEB-INF/csv"));
		csvFolder.mkdir();
		updateStats();
	}

	@PostConstruct
	public void populate() {
		// TODO This method is run after the creation of the first instance of
		// this class. Should consult DB and initiate variables, but DAO is not
		// promptly available. Maybe a solution is related to
		// http://www.guj.com.br/posts/list/200676.java .
	}

	public File getCsvStatsFile() {
		Calendar now = Calendar.getInstance();
		if (now.getTimeInMillis() - lastUpdated.getTimeInMillis() > ONE_DAY)
			updateStats();
		return csvStatsFile;
	}

	public String getTemporalData() {
		Calendar now = Calendar.getInstance();
		if (now.getTimeInMillis() - lastUpdated.getTimeInMillis() > ONE_DAY)
			updateStats();
		return temporalData;
	}

	private static Calendar zeroTime(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}

	private synchronized void updateStats() {
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);

		DataFeed feed = manager.getData(IDS, METRICS, DIMENSIONS,
				LAUNCH_DAY.getTime(), yesterday.getTime());

		String metrics = manager.getDatedMetricsAsString(feed);

		String header = "data,eventos,visitas,impressões"
				+ System.getProperty("line.separator");
		String csv = metrics.replaceAll(";",
				System.getProperty("line.separator"));

		File statsFile = new File(csvFolder, "stats.csv");

		try {
			FileWriter fw = new FileWriter(statsFile);
			fw.write(header);
			fw.write(csv);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.lastUpdated = zeroTime(Calendar.getInstance());
		this.csvStatsFile = statsFile;
		this.temporalData = metrics;
		
		setEvents(feed.aggregates.metrics.get(0).value);
		setVisits(feed.aggregates.metrics.get(1).value);
		setPageviews(feed.aggregates.metrics.get(2).value);
	}

	public String getVersion() {
		return BuildUtil.POM_VERSION;
	}

	public Date getDate() {
		return BuildUtil.BUILD_TIME;
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public int getEvents() {
		Calendar now = Calendar.getInstance();
		if (now.getTimeInMillis() - lastUpdated.getTimeInMillis() > ONE_DAY) {
			updateStats();
		}
		return events.get();
	}

	public void setEvents(int events) {
		this.events.set(events);
	}

	public int getVisits() {
		Calendar now = Calendar.getInstance();
		if (now.getTimeInMillis() - lastUpdated.getTimeInMillis() > ONE_DAY)
			updateStats();
		return visits.get();
	}

	public void setVisits(int visits) {
		this.visits.set(visits);
	}

	public int getPageviews() {
		Calendar now = Calendar.getInstance();
		if (now.getTimeInMillis() - lastUpdated.getTimeInMillis() > ONE_DAY)
			updateStats();
		return pageviews.get();
	}

	public void setPageviews(int pageviews) {
		this.pageviews.set(pageviews);
	}

	public int getReportedErrors() {
		return this.reportedErrors.get();
	}

	public void setReportedErrors(int reportedErrors) {
		this.reportedErrors.set(reportedErrors);
	}

	public void incReportedErrors() {
		this.reportedErrors.incrementAndGet();
	}

	public void decReportedErrors() {
		this.reportedErrors.decrementAndGet();
	}

	public int getDictionaryEntries() {
		return dictionaryEntries.get();
	}

	public void setDictionaryEntries(int dictionaryEntries) {
		this.dictionaryEntries.set(dictionaryEntries);
	}

	public void incDictionaryEntries() {
		this.dictionaryEntries.incrementAndGet();
	}

	public void decDictionaryEntries() {
		this.dictionaryEntries.decrementAndGet();
	}

	public int getRegisteredMembers() {
		return registeredMembers.get();
	}

	public void setRegisteredMembers(int registeredMembers) {
		this.registeredMembers.set(registeredMembers);
	}

	public void incRegisteredMembers() {
		this.registeredMembers.incrementAndGet();
	}

	public void decRegisteredMembers() {
		this.registeredMembers.decrementAndGet();
	}

	public int getOnlineUsers() {
		if (onlineUsers.get() < loggedUsers.size())
			setOnlineUsers(loggedUsers.size());
		return onlineUsers.get();
	}

	public void setOnlineUsers(int onlineUsers) {
		this.onlineUsers.set(onlineUsers);
	}

	public void incOnlineUsers() {
		this.onlineUsers.incrementAndGet();
	}

	public void decOnlineUsers() {
		this.onlineUsers.decrementAndGet();
	}

	public int getOnlineMembers() {
		return this.loggedUsers.size();
	}

	public int getOnlineVisits() {
		return getOnlineUsers() - loggedUsers.size();
	}

	public List<User> getLoggedUsers() {
		return loggedUsers;
	}

	public void addLoggedUser(User user) {
		this.loggedUsers.add(user);
	}

	public void removeLoggedUser(User user) {
		this.loggedUsers.remove(user);
	}

	public List<User> getIdleUsers() {
		return idleUsers;
	}

	public void setIdleUsers(List<User> idleUsers) {
		this.idleUsers = idleUsers;
	}

	public List<User> getTopUsers() {
		return topUsers;
	}

	public void setTopUsers(List<User> topUsers) {
		this.topUsers = topUsers;
	}
}
