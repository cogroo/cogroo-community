package br.usp.ime.cogroo.interceptors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
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
	//private final Result result;

	private final ErrorEntryDAO errorEntryDAO;
	private final UserDAO userDAO;
	private final DictionaryEntryDAO dictionaryEntryDAO;

	//private final HttpServletRequest request;

	// private final ServletContext context;

	public SystemInterceptor(ApplicationData appData,
			ErrorEntryDAO errorEntryDAO, UserDAO userDAO, DictionaryEntryDAO dictionaryEntryDAO) {
		this.appData = appData;
		this.errorEntryDAO = errorEntryDAO;
		this.userDAO = userDAO;
		this.dictionaryEntryDAO = dictionaryEntryDAO;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return !appData.isInitialized();
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object resourceInstance) throws InterceptionException {
		// XXX Ineficiente. Porém, não consigo acessar a DAO em nenhum outro
		// ponto "global" único. Ver comentário em ApplicationData.populate().
		appData.setReportedErrors((int) errorEntryDAO.count());
		appData.setRegisteredMembers((int) userDAO.count());
		appData.setDictionaryEntries((int) dictionaryEntryDAO.count());
		appData.setInitialized(true);
		stack.next(method, resourceInstance);
	}

}
