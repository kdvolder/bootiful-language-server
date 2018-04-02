package org.springframework.lsp.sample.wordcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lsp.simplelanguageserver.SimpleLanguageServer;

@SpringBootApplication
public class ExampleWordcheckApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ExampleWordcheckApplication.class, args);
	}

	@Autowired SimpleLanguageServer server;
	
	@Override
	public void run(String... args) throws Exception {
		System.out.println(server);
	}
}
