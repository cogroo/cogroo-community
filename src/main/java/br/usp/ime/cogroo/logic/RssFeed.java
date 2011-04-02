package br.usp.ime.cogroo.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;

@Component
@ApplicationScoped
public class RssFeed {
	
	private static final String RSS_TITLE = "CoGrOO Comunidade";
	private static final String RSS_LINK = "http://ccsl.ime.usp.br/cogroo/comunidade/";
	private static final String RSS_DESCRIPTION = "O que há de novo no CoGrOO Comunidade.";
	private static final int LIMIT = 10;
	
	private static final Logger LOG = Logger.getLogger(RssFeed.class);
	private static final String feedType = "rss_2.0";
	public static final String FEED_FILENAME = "feed.xml";
	public static final String TWITTER_FILENAME = "twitterFeed.xml";

	private SyndFeed feedRSS;
	private SyndFeed feedTwitter;
	
	public File getFeedFile() {
		this.feedRSS = init(FEED_FILENAME, feedRSS);
		return getFile(FEED_FILENAME);
	}
	
	public File getTwitterFile() {
		this.feedTwitter = init(TWITTER_FILENAME, feedTwitter);
		return getFile(TWITTER_FILENAME);
	}

	private SyndFeed init(String file, SyndFeed feed) {

		File f = new File(file);
		synchronized (this) {
			if (feed == null) {
				LOG.info("Will create RSS Feed.");
				if (!f.exists()) {
					// create the feed
					feed = new SyndFeedImpl();
					feed.setFeedType(feedType);

					feed.setTitle(RSS_TITLE);
					feed.setLink(RSS_LINK);
					feed.setDescription(RSS_DESCRIPTION);

					write(file, feed);
				} else {
					try {
						feed = new SyndFeedInput().build(f);
					} catch (Exception e) {
						LOG.error("Could not create RSS Feed", e);
					}
				}
			}
		}
		return feed;
	}

	public void addRssEntry(String title, String link, String value) {
		this.feedRSS = init(FEED_FILENAME, this.feedRSS);
		addEntry(title, link, value, FEED_FILENAME, this.feedRSS);
	}
	

	public void addTweet(String title, String link, String value) {
		this.feedTwitter = init(TWITTER_FILENAME, this.feedTwitter);
		addEntry(title, link, value, TWITTER_FILENAME, this.feedTwitter);
	}
	
	public void clean() {
		clean(TWITTER_FILENAME, this.feedTwitter);
		clean(FEED_FILENAME, this.feedRSS);
	}
	
	private void clean(String file, SyndFeed feed) {
		synchronized (this) {
			File f = new File(file);
			boolean couldDelete = f.delete();
			if(!couldDelete) {
				LOG.error("Couldn't delete RSS " + f.getAbsolutePath()) ;
			} else {
				feed = null;
			}
		}
		init(file, feed);
	}
	
	@SuppressWarnings("unchecked")
 	private void addEntry(String title, String link, String value, String file, SyndFeed feed) {
		synchronized (this) {
			List<SyndEntry> entries = new ArrayList<SyndEntry>();
			
			SyndEntry entry;
			SyndContent description;

			entry = new SyndEntryImpl();
			entry.setTitle(title);
			entry.setLink(link);
			entry.setPublishedDate(new Date());
			description = new SyndContentImpl();
			description.setType("text/plain");
			description.setValue(value);
			entry.setDescription(description);
			entries.add(entry);
			
			entries.addAll((List<SyndEntry>)feed.getEntries());
			
			Collections.sort(entries, new Comparator<SyndEntry>() {

				@Override
				public int compare(SyndEntry o1, SyndEntry o2) {
					Date d1 = o1.getPublishedDate();
					Date d2 = o2.getPublishedDate();
					// reverse order
					return d2.compareTo(d1);
				}
			});
			
			while(entries.size() > LIMIT ) {
				entries.remove(entries.size() - 1);
			}
			
			feed.setEntries(entries);
			
			write(file, feed);
		}
	}

	private void write(String file, SyndFeed feed) {
		try {
			// write initial version
			Writer writer = new FileWriter(file);
			SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed, writer);
			writer.close();
		} catch (Exception e) {
			LOG.error("Could not create RSS Feed", e);
		}
	}
	
	
	private File getFile(String file) {
		return new File(file);
	}
}
