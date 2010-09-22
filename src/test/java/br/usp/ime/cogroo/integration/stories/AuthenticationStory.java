package br.usp.ime.cogroo.integration.stories;

import org.junit.Test;

import br.usp.ime.cogroo.integration.stories.common.DefaultStory;

public class AuthenticationStory extends DefaultStory {

	@Test
	public void signUpWithANewUser() {
		given.theUserDoesntExist("edu").and().
		      iAmOnTheRootPage();
		when.iSignUpAs("edu");
		then.iMustBeLoggedInAs("edu");		
	}

	@Test
	public void logout() {
		given.thereisAnUserNamed("deise").and().
			  iAmLoggedAs("deise");
		when.iLogout();
		then.iMustNotBeLoggedIn();
	}
}	
