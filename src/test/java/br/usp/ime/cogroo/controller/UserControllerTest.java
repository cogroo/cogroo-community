package br.usp.ime.cogroo.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import utils.HSQLDBEntityManagerFactory;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.security.Admin;
import br.usp.ime.cogroo.security.RoleProvider;

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
	
	@Before
	public void setUp() {
		result = new MockResult();
		Validator validator = new MockValidator();

		em = HSQLDBEntityManagerFactory.createEntityManager();
		
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
		
		
		LoggedUser loggedUser1 = new LoggedUser(null);
		loggedUser1.setUser(admin);
		
		
		
		adminController = new UserController(result, userDAO, loggedUser1, validator, null);
		
		LoggedUser loggedUser2 = new LoggedUser(null);
		loggedUser2.setUser(william);
		userController = new UserController(result, userDAO, loggedUser2, validator, null);
		
		LoggedUser loggedUser3 = new LoggedUser(null);
		unknownController = new UserController(result, userDAO, loggedUser3, validator, null);
	}
	
	@Test
	public void testUnknownUserCantSeeUserList() {
		try {
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
	public void testUnknownUserCantSeeUserDetails() {
		try {
			unknownController.user(william);
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
	public void testUnknownUserCantSetRole() {
		try {
			unknownController.userRole(wesley, Admin.ROLE_NAME);
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
