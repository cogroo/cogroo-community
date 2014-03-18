package utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.security.annotations.LoggedInInterceptor;
import br.usp.ime.cogroo.security.annotations.RoleNeededInterceptor;


public class InterceptorUtil {
    // check if a method has a annotation interceptor
    public static void intercept(Method method, LoggedUser loggedUser, Result result) {
      ResourceMethod resourceMethod = mock(ResourceMethod.class);
      when(resourceMethod.getMethod()).thenReturn(method);

      InterceptorStack stack = mock(InterceptorStack.class);
      Object resourceInstance = mock(Object.class);

      LoggedInInterceptor loggedInInterceptor = new LoggedInInterceptor(result, loggedUser);

      if(loggedInInterceptor.accepts(resourceMethod)) {
        loggedInInterceptor.intercept(stack, resourceMethod, resourceInstance);
      }

      RoleNeededInterceptor roleNeededInterceptor = new RoleNeededInterceptor(result, loggedUser);

        if(roleNeededInterceptor.accepts(resourceMethod)) {
          roleNeededInterceptor.intercept(stack, resourceMethod, resourceInstance);
        }
    }

    public static void validate(Method method, LoggedUser loggedUser, Result result) {
      intercept(method, loggedUser, result);
      if(result.included().containsKey("errors")) {
        @SuppressWarnings("unchecked")
        List<Message> list = (List<Message>) result.included().get("errors");
        ValidationException ve = new ValidationException(list);
        throw ve;
      }
    }

    public static Method findMethod(Class<?> theClass, String methodName,
        Class<?> ... params) {
      try {
        Method method = theClass.getMethod(methodName, params);
        return method;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
}
