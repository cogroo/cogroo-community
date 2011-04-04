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
import br.usp.ime.cogroo.controller.RegisterController;
import br.usp.ime.cogroo.model.LoggedUser;

@Intercepts
public class SetLastURIInterceptor implements Interceptor {

	private final HttpServletRequest request;
	private static final Logger LOG = Logger
			.getLogger(SetLastURIInterceptor.class);
	private final LoggedUser loggedUser;

	public SetLastURIInterceptor(HttpServletRequest request,
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

		if (!loggedUser.isLogged()) {
			Method invokedMethod = method.getMethod();
			Class<?> clazz = invokedMethod.getDeclaringClass();
			if (LOG.isDebugEnabled()) {
				LOG.debug("clazz: " + clazz.getName());
				LOG.debug("invokedMethod: " + invokedMethod.getName());
			}

			if (!clazz.equals(LoginController.class)
					&& !(clazz.equals(RegisterController.class) && invokedMethod
							.getName().equals("register"))) {
				String lastURL = request.getRequestURL().toString();
				if (LOG.isDebugEnabled()) {
					LOG.info("Saving last visited URL:" + lastURL);
				}
				loggedUser.setLastURIVisited(lastURL);

			}
		}

		stack.next(method, resourceInstance);
	}

}
