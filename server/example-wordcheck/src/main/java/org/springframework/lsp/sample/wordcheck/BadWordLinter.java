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

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lsp.simplelanguageserver.document.DocumentRegion;
import org.springframework.lsp.simplelanguageserver.document.IDocument;
import org.springframework.lsp.simplelanguageserver.reconcile.LinterFunction;
import org.springframework.lsp.simplelanguageserver.reconcile.ProblemSeverity;
import org.springframework.lsp.simplelanguageserver.reconcile.ProblemType;
import org.springframework.lsp.simplelanguageserver.reconcile.ProblemTypes;
import org.springframework.lsp.simplelanguageserver.reconcile.ReconcileProblem;

import com.google.common.collect.ImmutableSet;

import reactor.core.publisher.Flux;

public class BadWordLinter implements LinterFunction {
	
	private static final Logger log = LoggerFactory.getLogger(BadWordLinter.class);
	
	private static final Pattern SPACE = Pattern.compile("[^\\w]+");
	protected static final ProblemType BADWORD_PROBLEM = ProblemTypes.create("BADWORD", ProblemSeverity.ERROR);
	
	private Set<String> goodWords;

	public BadWordLinter(List<String> words) {
		this.goodWords = ImmutableSet.copyOf(words);
	}

	@Override
	public Flux<ReconcileProblem> lint(IDocument doc) {
		//Take care not produce the array of words until subscription time.
		return Flux.defer(() -> Flux.fromArray(new DocumentRegion(doc).split(SPACE)))
				.doOnNext(w -> log.debug("word = {}", w))
				.filter(w -> w.length()>0)
				.filter(w -> !goodWords.contains(w.toString()))
				.doOnNext(w -> log.debug("badWord = {}", w))
				.map(this::problem);
	}

	private ReconcileProblem problem(DocumentRegion badWord) {
		return BADWORD_PROBLEM.create(badWord, "'"+badWord+"' is a bad word!");
	}
	
}
