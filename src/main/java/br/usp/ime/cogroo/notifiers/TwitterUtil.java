package br.usp.ime.cogroo.notifiers;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.dao.ShortUrlDAO;
import br.usp.ime.cogroo.model.ShortUrl;
import br.usp.ime.cogroo.util.BuildUtil;

import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.BitlyException;
import com.rosaloves.bitlyj.Url;

@Component
class TwitterUtil {

	private static final Logger LOG = Logger.getLogger(TwitterUtil.class);

	private Twitter twitter;
	private static Provider bitly = as(BuildUtil.BITLY_USR, BuildUtil.BITLY_APIKEY);
	private boolean isAvaiable = false;
	private ShortUrlDAO shortUrlDAO;

	public TwitterUtil(ShortUrlDAO shortUrlDAO) {
		this.shortUrlDAO = shortUrlDAO;
		
		this.twitter = new TwitterFactory().getInstance();
		if (!twitter.getAuthorization().isEnabled()) {
			LOG.error("OAuth consumer key/secret is not set.");
		} else {
			isAvaiable = true;
		}
	}

	public void tweet(String text, String link) {
		if (isAvaiable) {
			try {
				Status status = twitter.updateStatus(merge(text, shortURL(link)));
				if (LOG.isDebugEnabled()) {
					LOG.debug("Tweeted: " + status.getText());
				}
			} catch (TwitterException e) {
				LOG.error("Failed to tweet", e);
			}
		} else {
			LOG.error("Twetter not avaiable");
		}
	}

	private static String merge(String text, String link) {
		int l = link.length();
		int t;
		if (text.length() > 135) {
			t = 135;
		} else {
			t = text.length();
		}
		if(t + l > 140) {
			return text.substring(0, t - l) + " ... " + link;
		}
		return text + " " + link;
		
	}

	private String shortURL(String ori) {
		String res;
		ShortUrl shortURL = this.shortUrlDAO.retrieve(ori);
		if(shortURL == null) {
			try {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Will create new bit.ly for url: " + ori);
				}
				Url url = bitly.call(shorten(ori));
				res = url.getShortUrl();
				
				this.shortUrlDAO.add(new ShortUrl(ori, res));
			} catch (BitlyException e) {
				LOG.error("Error generating bit.ly", e);
				res = ori;
			}
		} else {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Could get URL from cache.");
			}
			res = shortURL.getShortURL();
		}
		
		return res;
	}
	
}
