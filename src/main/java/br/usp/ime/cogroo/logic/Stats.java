package br.usp.ime.cogroo.logic;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;

/**
 * 
 * @author Michel
 */
@Component
public class Stats {

	//TODO Usar o do Apache:
	/*	<!-- Define the default session timeout for your application,
	    in minutes.  From a servlet or JSP page, you can modify
	    the timeout for a particular session dynamically by using
	    HttpSession.getMaxInactiveInterval(). -->
	
	<session-config>
	 <session-timeout>30</session-timeout>    <!-- 30 minutes -->
	</session-config>*/
	private final long ONLINE_TIMEOUT = 1 * 30 * 1000;

	private UserDAO userDAO;
	private ErrorEntryDAO errorEntryDAO;

	private int totalMembers = -1;
	private int onlineMembers = -1;
	private int reportedErrors = -1;

	public Stats(UserDAO userDAO, ErrorEntryDAO errorEntryDAO) {
		this.userDAO = userDAO;
		this.errorEntryDAO = errorEntryDAO;
	}

	public int getTotalMembers() {
		// TODO Pode ser ineficiente. Melhor guardar no BD (escalabilidade) e só atualizar depois de X minutos.
		return (int) userDAO.count();
	}

	public int getOnlineMembers() {
		long timeout = System.currentTimeMillis() - ONLINE_TIMEOUT;
		return (int) userDAO.countLoginLater(timeout);
	}

	public int getReportedErrors() {
		return (int) errorEntryDAO.count();
	}

}
