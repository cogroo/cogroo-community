package br.usp.ime.cogroo.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.exceptions.Messages;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;
import br.usp.ime.cogroo.util.CriptoUtils;

public class LoginControllerTest {
	
	private LoginController loginController;
	private Result mockResult;
	private UserDAO mockUserDAO;
	private LoggedUser loggedUser;
	
	@Before
	public void setUp(){
		
		mockResult = new MockResult();
		mockUserDAO = mock(UserDAO.class);
		loggedUser = new LoggedUser(null);
		Validator mockValidator = new MockValidator();
		loginController = new LoginController(mockResult, mockUserDAO, loggedUser, mockValidator, null);

	}
	
	@Test
	public void testCannotLoginWithEmptyUserName() {
		String username = "";
		String password = "";
		
		try {
			loginController.login(username, password);
			fail();			
		} catch(ValidationException e) {
			assertTrue("Couldn't assert that message for empty user was created.", 
					checkIfContainsError(e.getErrors(), Messages.USER_CANNOT_BE_EMPTY, Messages.ERROR));
		}
		
	}
	
	@Test
	public void testCannotLoginWithUserNameWithOnlySpaces() {
//		User userWithEmptyName = new User("   ");
		String username = "    ";
		String password = "";
		
		try {
			loginController.login(username, password);
			fail();			
		} catch(ValidationException e) {
			assertTrue("Couldn't assert that message for empty user was created.", 
					checkIfContainsError(e.getErrors(), Messages.USER_CANNOT_BE_EMPTY, Messages.ERROR));
		}
		
	}
	
	@Test
	public void testUserCanLogin() {
		String userName = "aUser";
		String password = "password";
		
		String passCripto = CriptoUtils.digestMD5(userName, password);

		
		User userWithName = new User(userName);
		userWithName.setPassword(passCripto);
		
		when(mockUserDAO.retrieveByLogin(userName)).thenReturn(userWithName);
		
		loginController.login(userName, password);
		
		assertEquals(userWithName.getLogin(), loggedUser.getUser().getLogin());

	}
	
	private boolean checkIfContainsError(List<Message> errors, String message, String category) {
		boolean foundError = false;
		for (Message error : errors) {
			if(error.getMessage().equals(message) && error.getCategory().equals(category)) {
				foundError = true;
			}
		}
		return foundError;
	}
	
}
