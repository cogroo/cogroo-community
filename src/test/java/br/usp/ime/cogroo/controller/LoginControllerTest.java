package br.usp.ime.cogroo.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
import br.usp.ime.cogroo.dao.UserDAO;
import br.usp.ime.cogroo.exceptions.ExceptionMessages;
import br.usp.ime.cogroo.logic.AnalyticsManager;
import br.usp.ime.cogroo.logic.AnalyticsManagerImpl;
import br.usp.ime.cogroo.model.ApplicationData;
import br.usp.ime.cogroo.model.DataFeed;
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
		MockServletContext mockContext= new MockServletContext("../classes/");
		mockContext.getRealPath("");
		AnalyticsManager am = mock(AnalyticsManager.class);
		when(am.getDatedMetricsAsString(any(DataFeed.class))).thenReturn("2010-11-17,0,8,21;2010-11-18,12,20,81;2010-11-19,9,16,65");
		loggedUser = new LoggedUser(new ApplicationData(am, mockContext));
		Validator mockValidator = new MockValidator();
		HttpServletRequest mockRequest = new MockHttpServletRequest();
		loginController = new LoginController(mockResult, mockUserDAO, loggedUser, mockValidator, mockRequest);

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
					checkIfContainsError(e.getErrors(), ExceptionMessages.USER_CANNOT_BE_EMPTY, ExceptionMessages.ERROR));
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
					checkIfContainsError(e.getErrors(), ExceptionMessages.USER_CANNOT_BE_EMPTY, ExceptionMessages.ERROR));
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
