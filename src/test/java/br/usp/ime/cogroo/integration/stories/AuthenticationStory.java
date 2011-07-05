package br.usp.ime.cogroo.integration.stories;

import org.junit.Test;

import br.usp.ime.cogroo.integration.stories.common.DefaultStory;

public class AuthenticationStory extends DefaultStory {

	@Test
	public void signUpWithAnInvalidUser() {
		given.theUserDoesntExist("edu").and().
		      iAmOnTheRootPage();
		when.iSignUpAs("edu","senha");
		then.iGetAnErrorWithDescription("Usuário não existe");		
	}

	@Test
	public void logout() {
		given.thereisAnUserNamed("deise").and().
			  iAmLoggedAs("deise");
		when.iLogout();
		then.iMustNotBeLoggedIn();
	}
}	
