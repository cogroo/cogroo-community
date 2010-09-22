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
import br.usp.ime.cogroo.Messages;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.model.LoggedUser;
import br.usp.ime.cogroo.model.User;

public class LoginControllerTest {
	
	private LoginController loginController;
	private Result mockResult;
	private UserDAO mockUserDAO;
	private LoggedUser loggedUser;
	
	@Before
	public void setUp(){
		
		mockResult = new MockResult();
		mockUserDAO = mock(UserDAO.class);
		loggedUser = new LoggedUser();
		Validator mockValidator = new MockValidator();
		loginController = new LoginController(mockResult, mockUserDAO, loggedUser, mockValidator);

	}
	
	@Test
	public void testCannotLoginWithEmptyUserName() {
		User userWithEmptyName = new User("");
		
		try {
			loginController.login(userWithEmptyName);
			fail();			
		} catch(ValidationException e) {
			assertTrue("Couldn't assert that message for empty user was created.", 
					checkIfContainsError(e.getErrors(), Messages.USER_CANNOT_BE_EMPTY, Messages.ERROR));
		}
		
	}
	
	@Test
	public void testCannotLoginWithUserNameWithOnlySpaces() {
		User userWithEmptyName = new User("   ");
		
		try {
			loginController.login(userWithEmptyName);
			fail();			
		} catch(ValidationException e) {
			assertTrue("Couldn't assert that message for empty user was created.", 
					checkIfContainsError(e.getErrors(), Messages.USER_CANNOT_BE_EMPTY, Messages.ERROR));
		}
		
	}
	
	@Test
	public void testUserCanLogin() {
		String userName = "aUser";
		User userWithName = new User(userName);
		loginController.login(userWithName);
		
		when(mockUserDAO.retrieve(userName)).thenReturn(userWithName);
		
		assertEquals(userWithName.getName(), loggedUser.getUser().getName());

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
