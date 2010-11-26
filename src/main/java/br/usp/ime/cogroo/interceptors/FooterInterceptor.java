package br.usp.ime.cogroo.interceptors;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.usp.ime.cogroo.model.ApplicationData;

@Intercepts
public class FooterInterceptor implements Interceptor {

	private final ApplicationData appData;
	private final Result result;

	private final HttpServletRequest request;

	// private final ServletContext context;

	public FooterInterceptor(Result result, HttpServletRequest request,
			ApplicationData appData) {
		this.result = result;
		this.request = request;
		this.appData = appData;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return true;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object resourceInstance) throws InterceptionException {

		if(appData != null && request != null) {
			HttpSession session = request.getSession();
			ServletContext context = session.getServletContext();
			String name = SessionListener.SESSION_COUNTER;
			Object attribute = context.getAttribute(name);
			AtomicInteger i = (AtomicInteger) attribute;
			if (i == null)
				i = new AtomicInteger();
			int counter = i.get();
			appData.setOnlineUsers(counter);
/*			appData.setOnlineUsers(((AtomicInteger) request.getSession()
					.getServletContext()
					.getAttribute(SessionListener.SESSION_COUNTER)).get());*/
			result.include("appData", appData);
		} 
		stack.next(method, resourceInstance);		
	}

}
