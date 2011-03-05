package br.usp.ime.cogroo.logic;

import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Text sanitizer for HTML tags. Powered by AntiSamy (
 * {@link <a href="http://www.owasp.org/index.php/Category:OWASP_AntiSamy_Project"></a>}
 * ).
 * 
 * @author Michel
 * @version 1.0
 */
@Component
@ApplicationScoped
public class TextSanitizer {

	private static final Logger LOG = Logger.getLogger(TextSanitizer.class);

	private final URL slashdotPolicyLocation = getClass().getResource(
			"/antisamy-slashdot-1.4.3.xml");
	private final URL nothinggoesPolicyLocation = getClass().getResource(
			"/antisamy-nothinggoes-1.4.3.xml");

	private static Policy strictPolicy;
	private static Policy relaxedPolicy;

	private static final AntiSamy as = new AntiSamy();

	public TextSanitizer() {
		try {
			strictPolicy = Policy.getInstance(nothinggoesPolicyLocation);
			relaxedPolicy = Policy.getInstance(slashdotPolicyLocation);
		} catch (PolicyException e) {
			LOG.error("A problem was found reading the policy file.", e);
		}
	}

	/**
	 * Sanitize user input for HTML tags.
	 * 
	 * @param text
	 *            Tainted text which might contain invalid HTML tags.
	 * @param allowTextFormattingTags
	 *            <t>true</t>
	 * @return Input without HTML tags.
	 */
	public String sanitize(String text, boolean allowTextFormattingTags) {
		if (text != null) {
			Policy policy = allowTextFormattingTags ? relaxedPolicy
					: strictPolicy;
			CleanResults cr = null;
			try {
				cr = as.scan(text, policy);
				LOG.info("Antisamy errors found: " + cr.getNumberOfErrors());
				LOG.info("Antisamy scanning time (s): " + cr.getScanTime());
			} catch (ScanException e) {
				LOG.error("A problem was found scanning input text.", e);
			} catch (PolicyException e) {
				LOG.error("A problem was found reading the policy file.", e);
			}

			return cr == null ? null : cr.getCleanHTML();
		}
		return null;
	}
	
	public List<String> sanitize(List<String> text, boolean allowTextFormattingTags) {
		for (int i = 0; i < text.size(); i++)
			text.set(i, sanitize(text.get(i), allowTextFormattingTags));
		return text;
	}
}
