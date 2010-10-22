package br.usp.ime.cogroo.model;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class ApplicationData {		
	public ApplicationData() {
		System.out.println("..........Construtor..........");
	}

	public String getVersion() {
		return "Versão TESTE";
	}


}
