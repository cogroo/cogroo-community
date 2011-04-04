package br.usp.ime.cogroo.security.annotations;

import static br.com.caelum.vraptor.view.Results.logic;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.usp.ime.cogroo.controller.IndexController;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.interceptors.SetLastURLInterceptor;
import br.usp.ime.cogroo.model.LoggedUser;

@Intercepts(before = { SetLastURLInterceptor.class })
public class LoggedInInterceptor implements Interceptor {

	private static final Logger LOG = Logger
			.getLogger(LoggedInInterceptor.class);
	private final LoggedUser loggedUser;
	private final Result result;

	public LoggedInInterceptor(Result result, LoggedUser loggedUser) {
		this.result = result;
		this.loggedUser = loggedUser;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return isAnotadedWithLoggIn(method);
	}

	private Boolean isAnotadedWithLoggIn(ResourceMethod method) {
		Annotation loggedIn = method.getMethod().getAnnotation(LoggedIn.class);
		return !(loggedIn == null);
	}

	@Override
	public synchronized void intercept(InterceptorStack stack,
			ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		if (LOG.isDebugEnabled())
			LOG.debug("LoggedIn Annotated!");

		if (loggedUser.isLogged()) {
			if (LOG.isDebugEnabled())
				LOG.debug("User Logged in, go to stack.next!");

			stack.next(method, resourceInstance);
		} else {
			if (LOG.isDebugEnabled())
				LOG
						.debug("User NOT Logged in, redirect to index page with error message.");
			result.include("errors", Arrays.asList(new ValidationMessage(
					ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS,
					ExceptionMessages.ERROR)));
			result.use(logic()).redirectTo(IndexController.class).index();
		}

	}

}
