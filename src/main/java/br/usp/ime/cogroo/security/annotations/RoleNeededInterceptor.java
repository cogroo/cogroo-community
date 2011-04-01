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
import br.usp.ime.cogroo.model.User;

@Intercepts(before = { SetLastURIInterceptor.class })
public class RoleNeededInterceptor implements Interceptor {

	private static final Logger LOG = Logger
			.getLogger(RoleNeededInterceptor.class);
	private final LoggedUser loggedUser;
	private final Validator validator;

	public RoleNeededInterceptor(Validator validator, LoggedUser loggedUser) {
		this.validator = validator;
		this.loggedUser = loggedUser;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return isAnotadedWithRoleNeeded(method);
	}

	private boolean isAnotadedWithRoleNeeded(ResourceMethod method) {
		Annotation rolesNeeded = method.getMethod().getAnnotation(
				RolesNeeded.class);
		return !(rolesNeeded == null);
	}

	@Override
	public synchronized void intercept(InterceptorStack stack,
			ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		if (LOG.isDebugEnabled())
			LOG.info("RolesNeeded Annotated!");

		if (isPermited(method)) {
			if (LOG.isDebugEnabled())
				LOG.info("Permitido, go to stack.next!");

			stack.next(method, resourceInstance);
		} else {
			if (LOG.isDebugEnabled())
				LOG.info("Negado, redirect to index page with error message.");
			validator.add(new ValidationMessage(
					ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS,
					ExceptionMessages.ERROR));
			validator.onErrorUse(Results.logic()).redirectTo(
					IndexController.class).index();
		}

	}

	private boolean isPermited(ResourceMethod method) {
		RolesNeeded roleList = method.getMethod().getAnnotation(
				RolesNeeded.class);
		User user = loggedUser.getUser();
		if (user == null)
			return false;
		LOG.info("Role do user..:" + user.getRole());		
		for (String role : roleList.roles()) {
			LOG.info(role);
			if (role.equals(user.getRole()))
				return true;
		}
		return false;
	}

}
