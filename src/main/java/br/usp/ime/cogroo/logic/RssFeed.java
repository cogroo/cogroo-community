package br.usp.ime.cogroo.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
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

	private static final Logger LOG = Logger.getLogger(RssFeed.class);
	private static final String feedType = "rss_2.0";
	public static final String FILENAME = "feed.xml";

	private SyndFeed feed;

	private void init() {

		File f = new File(FILENAME);
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

					try {
						// write initial version
						Writer writer = new FileWriter(FILENAME);
						SyndFeedOutput output = new SyndFeedOutput();
						output.output(feed, writer);
						writer.close();
					} catch (Exception e) {
						LOG.error("Could not create RSS Feed", e);
					}
				} else {
					try {
						this.feed = new SyndFeedInput().build(f);
					} catch (Exception e) {
						LOG.error("Could not create RSS Feed", e);
					}
				}
			}
		}

	}

	public void addEntry(String title, String link, String value) {
		init();
		synchronized (this) {
			@SuppressWarnings("unchecked")
			List<SyndEntry> entries = new ArrayList<SyndEntry>((List<SyndEntry>)feed.getEntries());
			
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

			feed.setEntries(entries);
			
			try {
				// write initial version
				Writer writer = new FileWriter(FILENAME);
				SyndFeedOutput output = new SyndFeedOutput();
				output.output(feed, writer);
				writer.close();
			} catch (Exception e) {
				LOG.error("Could not add entry to feed", e);
			}
		}
	}

	public static void main(String[] args) {
		RssFeed r = new RssFeed();
		r.addEntry("Title", "http://cogroo", "E ae");
	}
}
