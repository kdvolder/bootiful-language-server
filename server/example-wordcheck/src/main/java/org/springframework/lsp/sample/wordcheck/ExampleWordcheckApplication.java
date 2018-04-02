package org.springframework.lsp.sample.wordcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lsp.simplelanguageserver.SimpleLanguageServer;
import org.springframework.lsp.simplelanguageserver.reconcile.LinterFunction;

@SpringBootApplication
public class ExampleWordcheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleWordcheckApplication.class, args);
	}
	
	@Autowired Wordlist wordlist;

	@Autowired SimpleLanguageServer server;

	@Bean LinterFunction linter() {
		
	}
	
	
	@Override
	public void run(String... args) throws Exception {
		System.out.println(server);
	}
}
