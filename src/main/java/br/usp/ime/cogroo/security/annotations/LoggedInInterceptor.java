package br.usp.ime.cogroo.security.annotations;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.usp.ime.cogroo.controller.IndexController;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.interceptors.SetLastURIInterceptor;
import br.usp.ime.cogroo.model.LoggedUser;

@Intercepts(before={SetLastURIInterceptor.class})
public class LoggedInInterceptor implements Interceptor {
	
	private static final Logger LOG = Logger
			.getLogger(LoggedInInterceptor.class);
	private final LoggedUser loggedUser;
	private final Validator validator;

	public LoggedInInterceptor(Validator validator, LoggedUser loggedUser) {
		this.validator = validator;
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
			LOG.info("LoggedIn Annotated!");

		if (loggedUser.isLogged()) {
			if (LOG.isDebugEnabled())
				LOG.info("User Logged in, go to stack.next!");

			stack.next(method, resourceInstance);
		} else {
			if (LOG.isDebugEnabled())
				LOG
						.info("User NOT Logged in, redirect to index page with error message.");
			validator.add(new ValidationMessage(
					ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(
					IndexController.class).index();
		}

	}

}
