/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.lsp.sample.wordcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lsp.simplelanguageserver.completions.CompletionProposal;
import org.springframework.lsp.simplelanguageserver.completions.CompletionProvider;
import org.springframework.lsp.simplelanguageserver.document.BadLocationException;
import org.springframework.lsp.simplelanguageserver.document.DocumentRegion;
import org.springframework.lsp.simplelanguageserver.document.IDocument;
import org.springframework.lsp.simplelanguageserver.util.FuzzyMatcher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WordlistCompletionProvider implements CompletionProvider {

	private static final Logger log = LoggerFactory.getLogger(WordlistCompletionProvider.class);
	
	private final Wordlist wordlist;

	public WordlistCompletionProvider(Wordlist wordlist) {
		this.wordlist = wordlist;
	}

	@Override
	public Flux<CompletionProposal> getCompletions(IDocument doc, int position) {
		int start = position;
		while (Character.isLetter(doc.getSafeChar(start-1))) {
			start--;
		}
		DocumentRegion prefix = new DocumentRegion(doc, start, position);
		log.debug("Completion prexix '{}'", prefix);
		return Flux.fromIterable(wordlist.getWords())
		.flatMap(word -> {
			try {
				//It probably is not necessary to select specific words based on the 'query'.
				//I.e. returning too many words is actually fine.
				//Language server client (at least vscode) filters the list all itself. But I'm not
				//100% sure that every client does this. The spec isn't totally clear about that.
				if (FuzzyMatcher.matchScore(prefix, word)!=0.0) {
					log.debug("+ word: {}", word);
					return Mono.just(CompletionProposals.simple(prefix, word));
				} else {
					log.debug("- word: {}", word);
				}
			} catch (BadLocationException e) {
				log.error("", e);
			}
			return Mono.empty();
		});
	}

}
