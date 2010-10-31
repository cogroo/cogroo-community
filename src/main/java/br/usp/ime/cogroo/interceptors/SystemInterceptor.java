package br.usp.ime.cogroo.interceptors;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.usp.ime.cogroo.dao.DictionaryEntryDAO;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.dao.errorreport.ErrorEntryDAO;
import br.usp.ime.cogroo.model.ApplicationData;

@Intercepts
public class SystemInterceptor implements Interceptor {

	private final ApplicationData appData;
	private final Result result;

	private final ErrorEntryDAO errorEntryDAO;
	private final DictionaryEntryDAO dictionaryEntryDAO;
	private final UserDAO userDAO;

	private final HttpServletRequest request;

	// private final ServletContext context;

	public SystemInterceptor(Result result, HttpServletRequest request,
			ApplicationData appData, ErrorEntryDAO errorEntryDAO,
			DictionaryEntryDAO dictionaryEntryDAO, UserDAO userDAO) {
		this.result = result;
		this.request = request;
		this.appData = appData;
		this.errorEntryDAO = errorEntryDAO;
		this.dictionaryEntryDAO = dictionaryEntryDAO;
		this.userDAO = userDAO;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return true;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object resourceInstance) throws InterceptionException {
		// XXX Ineficiente. Porém, não consigo acessar a DAO em nenhum outro
		// ponto "global".
		appData.setReportedErrors((int) errorEntryDAO.count());
		appData.setDictionaryEntries((int) dictionaryEntryDAO.count());	// TODO testar em doncovim
		appData.setRegisteredMembers((int) userDAO.count());
		appData.setOnlineUsers(((AtomicInteger) request.getSession()
				.getServletContext()
				.getAttribute(SessionListener.SESSION_COUNTER)).get());

		result.include("appData", appData);
		stack.next(method, resourceInstance);
	}

}
