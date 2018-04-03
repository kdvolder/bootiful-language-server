package org.springframework.lsp.sample.wordcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.lsp.simplelanguageserver.SimpleLanguageServer;
import org.springframework.lsp.simplelanguageserver.reconcile.LinterFunction;

@SpringBootApplication
@EnableConfigurationProperties(Wordlist.class)
public class ExampleWordcheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleWordcheckApplication.class, args);
	}
	
	@Autowired Wordlist wordlist;

	@Bean LinterFunction linter() {
		return new BadWordLinter(wordlist.getWords());
	}
	
}
