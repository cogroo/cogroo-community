package br.usp.ime.cogroo.interceptors;

// TODO estou no package correto?

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

// XXX Acho que a annotation só funciona com Tomcat 7, pois é Servlets 3.0.
//@WebListener
/**
 * @author Michel
 */
public class SessionListener implements HttpSessionListener {

	private static final String SESSION_COUNTER = "sessionCounter";

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		Integer counter = (Integer) se.getSession().getServletContext()
				.getAttribute(SESSION_COUNTER);
		if (counter == null)
			counter = 0;
		counter++;
		se.getSession().getServletContext()
				.setAttribute(SESSION_COUNTER, counter);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		Integer counter = (Integer) se.getSession().getServletContext()
				.getAttribute(SESSION_COUNTER);
		if (counter == null)
			counter = 0;
		counter--;
		se.getSession().getServletContext()
				.setAttribute(SESSION_COUNTER, counter);

		// FIXME Fazer logout do usuário atual
		/*
		 * User user = loggedUser.getUser(); user.setLogged(false);
		 * userDAO.update(user); loggedUser.logout();
		 */
	}

}
