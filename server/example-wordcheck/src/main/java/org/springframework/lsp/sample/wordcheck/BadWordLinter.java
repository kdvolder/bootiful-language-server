package org.springframework.lsp.sample.wordcheck;

import java.util.List;
import java.util.Set;

import org.springframework.lsp.simplelanguageserver.document.IDocument;
import org.springframework.lsp.simplelanguageserver.reconcile.LinterFunction;
import org.springframework.lsp.simplelanguageserver.reconcile.ReconcileProblem;

import com.google.common.collect.ImmutableSet;

import reactor.core.publisher.Flux;

public class BadWordLinter implements LinterFunction {

	private Set<String> badWords;

	public BadWordLinter(List<String> words) {
		this.badWords = ImmutableSet.copyOf(words);
	}

	@Override
	public Flux<ReconcileProblem> lint(IDocument doc) {
		// TODO Auto-generated method stub
		return null;
	}

}
