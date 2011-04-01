package br.usp.ime.cogroo.interceptors;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.usp.ime.cogroo.controller.LoginController;
import br.usp.ime.cogroo.model.LoggedUser;

@Intercepts
public class AuthorizationInterceptor implements Interceptor {

	private final HttpServletRequest request;
	private static final Logger LOG = Logger
			.getLogger(AuthorizationInterceptor.class);
	private final LoggedUser loggedUser;

	public AuthorizationInterceptor(HttpServletRequest request,
			LoggedUser loggedUser) {
		this.request = request;
		this.loggedUser = loggedUser;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return true;
	}

	@Override
	public synchronized void intercept(InterceptorStack stack,
			ResourceMethod method, Object resourceInstance)
			throws InterceptionException {

		Method invokedMethod = method.getMethod();
		if (!invokedMethod.getDeclaringClass().equals(LoginController.class)) {
			LOG.info("Registrando a ultima URL visitada..:"
					+ request.getRequestURL().toString());
			loggedUser.setLastURIVisited(request.getRequestURI());
		}

		stack.next(method, resourceInstance);
	}

}
