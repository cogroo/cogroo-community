package br.usp.ime.cogroo.security.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RolesNeeded {
	String[] roles();
}
