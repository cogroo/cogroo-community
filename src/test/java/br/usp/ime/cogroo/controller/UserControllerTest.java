package br.usp.ime.cogroo.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import utils.InterceptorUtil;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.logic.TextSanitizer;
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.security.Admin;
import static org.mockito.Mockito.*;


public class UserControllerTest {

	private MockResult result;
	private UserController adminController;
	private UserController userController;

	private User wesley;
	private User michel;
	private User william;
	private User admin;
	private User giliane;
	private UserController unknownController;
	private EntityManager em;
  private LoggedUser unknownLoggedUser;

	@Before
	public void setUp() {
		result = new MockResult();
		Validator validator = new MockValidator();

		em = HSQLDBEntityManagerFactory.createEntityManager();

		ApplicationData appData = mock(ApplicationData.class);

		UserDAO userDAO = new UserDAO(em);
		// add some users
		wesley = new User("Wesley");
		michel = new User("Michel");
		william = new User("William");
		giliane = new User("Giliane");
		admin = new User("admin");

		em.getTransaction().begin();
		userDAO.add(wesley);
		userDAO.add(michel);
		userDAO.add(william);
		userDAO.add(giliane);
		userDAO.add(admin);
		em.getTransaction().commit();


		LoggedUser loggedUser1 = new LoggedUser(appData);
		loggedUser1.login(admin);


		TextSanitizer ts = new TextSanitizer();
		adminController = new UserController(result, userDAO, loggedUser1, validator, null, ts);

		LoggedUser loggedUser2 = new LoggedUser(appData);
		loggedUser2.login(william);
		userController = new UserController(result, userDAO, loggedUser2, validator, null, ts);

		unknownLoggedUser = new LoggedUser(appData);
		unknownController = new UserController(result, userDAO, unknownLoggedUser, validator, null, ts);
	}

	@Test
	public void testUnknownUserCantSeeUserList() throws SecurityException, NoSuchMethodException {
		try {
		    InterceptorUtil.validate(UserController.class.getMethod("userList"), unknownLoggedUser, result);
			unknownController.userList();
			fail();
		} catch (ValidationException e) {
			List<Message> errors = e.getErrors();
			assertTrue(errors.size() > 0);
			Message message = errors.get(0);
			assertEquals(ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS, message.getMessage());
			assertEquals(ExceptionMessages.ERROR, message.getCategory());
		}
	}

	@Test
	public void testUserCanSeeUserList() {
		userController.userList();
		List<User> userList = result.included("userList");
		assertTrue(userList.size() > 0);
	}

	@Test
	public void testUserCanSeeUserDetails() {
		userController.user(william);
		User user = result.included("user");
		Collection<String> roles = result.included("roleList");
		assertEquals(william, user);
		assertTrue(roles.size() > 0);
	}

	@Test
	public void testUnknownUserCantSeeUserDetails() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		try {
		    Method method = InterceptorUtil.findMethod(UserController.class, "user", User.class);
		    InterceptorUtil.validate(method, unknownLoggedUser, result);
		    method.invoke(unknownController, william);
			fail();
		} catch (ValidationException e) {
			List<Message> errors = e.getErrors();
			assertTrue(errors.size() > 0);
			Message message = errors.get(0);
			assertEquals(ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS, message.getMessage());
			assertEquals(ExceptionMessages.ERROR, message.getCategory());
		}
	}

	@Test
	public void testUnknownUserCantSetRole() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		try {
		  Method method = InterceptorUtil.findMethod(
		      UserController.class,
		      "userRole",
		      User.class, String.class);
          InterceptorUtil.validate(method, unknownLoggedUser, result);
          method.invoke(unknownController, wesley, Admin.ROLE_NAME);
			fail();
		} catch (ValidationException e) {
			List<Message> errors = e.getErrors();
			assertTrue(errors.size() > 0);
			Message message = errors.get(0);
			assertEquals(ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS, message.getMessage());
			assertEquals(ExceptionMessages.ERROR, message.getCategory());
		}
	}

	@Test
	public void testUserCantSetRole() {
		try {
			userController.userRole(wesley, Admin.ROLE_NAME);
			fail();
		} catch (ValidationException e) {
			List<Message> errors = e.getErrors();
			assertTrue(errors.size() > 0);
			Message message = errors.get(0);
			assertEquals(ExceptionMessages.ONLY_LOGGED_USER_CAN_DO_THIS, message.getMessage());
			assertEquals(ExceptionMessages.ERROR, message.getCategory());
		}
	}

	@Test
	public void testAdminCanSetRole() {
		adminController.userRole(wesley, Admin.ROLE_NAME);
		assertEquals(Admin.ROLE_NAME, wesley.getRoleName());
	}

	@Test
	public void testAdminCanSetRole2() {
		adminController.userRole(william, Admin.ROLE_NAME);
		userController.userRole(wesley, Admin.ROLE_NAME);
		assertEquals(Admin.ROLE_NAME, wesley.getRoleName());

	}
}
