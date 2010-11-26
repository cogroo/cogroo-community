package br.usp.ime.cogroo.interceptors;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

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
	public synchronized void intercept(InterceptorStack stack,
			ResourceMethod method, Object resourceInstance)
			throws InterceptionException {

		AtomicInteger counter = (AtomicInteger) request.getSession()
				.getServletContext()
				.getAttribute(SessionListener.SESSION_COUNTER);
		if (counter == null)
			counter = new AtomicInteger();
		appData.setOnlineUsers(counter.get());
		result.include("appData", appData);
		stack.next(method, resourceInstance);
	}

}
