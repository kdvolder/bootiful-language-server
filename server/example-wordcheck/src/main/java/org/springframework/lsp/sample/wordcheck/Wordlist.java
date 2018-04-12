package org.springframework.lsp.sample.wordcheck;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.lsp.sample.wordcheck")
public class Wordlist {

	/**
	 * List of valid words. Words on this list are considered 'valid' by the 
	 * wordchecker. Any words not on the list are considered invalid.
	 */
	private List<String> words;

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}
	
}
