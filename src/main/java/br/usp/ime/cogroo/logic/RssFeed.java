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
	public static final String FILENAME = "feed.xml";

	private SyndFeed feed;
	
	private void write() {
		try {
			// write initial version
			Writer writer = new FileWriter(FILENAME);
			SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed, writer);
			writer.close();
		} catch (Exception e) {
			LOG.error("Could not create RSS Feed", e);
		}
	}

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

					write();
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

	
	@SuppressWarnings("unchecked")
	public void addEntry(String title, String link, String value) {
		init();
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
			
			write();
		}
	}

	public void clean() {
		synchronized (this) {
			File f = new File(FILENAME);
			boolean couldDelete = f.delete();
			if(!couldDelete) {
				LOG.error("Couldn't delete RSS " + f.getAbsolutePath()) ;
			} else {
				this.feed = null;
			}
		}
		init();
	}
}
