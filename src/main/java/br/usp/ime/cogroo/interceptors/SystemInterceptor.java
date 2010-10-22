package br.usp.ime.cogroo.interceptors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.usp.ime.cogroo.model.ApplicationData;

@Intercepts
public class SystemInterceptor implements Interceptor {

	private final ApplicationData appData;
	private final Result result;

//	private final HttpServletRequest request;
//	private final ServletContext context;

	public SystemInterceptor(Result result, ApplicationData appData) {
		this.result = result;
		this.appData = appData;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return true;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object resourceInstance) throws InterceptionException {

		result.include("appData", appData);
		stack.next(method, resourceInstance);
	}

}
