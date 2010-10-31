package br.usp.ime.cogroo.interceptors;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import br.usp.ime.cogroo.model.LoggedUser;

/**
 * @author Michel
 */
// @WebListener
public class SessionListener implements HttpSessionListener {

	public static final String SESSION_COUNTER = "sessionCounter";

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		AtomicInteger counter = (AtomicInteger) se.getSession().getServletContext()
				.getAttribute(SESSION_COUNTER);
		if (counter == null)
			counter = new AtomicInteger();
		counter.incrementAndGet();
		se.getSession().getServletContext()
				.setAttribute(SESSION_COUNTER, counter);

		// XXX Ideal caso ApplicationData fosse acessível a partir daqui.
		// appData.incOnlineUsers()
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		AtomicInteger counter = (AtomicInteger) se.getSession().getServletContext()
				.getAttribute(SESSION_COUNTER);
		if (counter == null)
			counter = new AtomicInteger(1);
		counter.decrementAndGet();
		se.getSession().getServletContext()
				.setAttribute(SESSION_COUNTER, counter);

		LoggedUser user = (LoggedUser) se.getSession().getAttribute(
				"loggedUser");
		if (user != null)
			user.logout();

		// appData.decOnlineUsers();
	}

}
