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
package org.springframework.lsp.simplelanguageserver.reconcile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lsp.simplelanguageserver.DiagnosticPublisher;
import org.springframework.lsp.simplelanguageserver.DocumentListenerManager;
import org.springframework.lsp.simplelanguageserver.document.BadLocationException;
import org.springframework.lsp.simplelanguageserver.document.IDocument;
import org.springframework.lsp.simplelanguageserver.document.Region;
import org.springframework.lsp.simplelanguageserver.document.TextDocumentContentChange;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Responds to document changes and calls {@link LinterFunction} to validate
 * document contents.
 */
public class SimpleReconciler {

	private LinterFunction linterFunction;
	private DocumentListenerManager documents;
	private Disposable dispoable;
	private Scheduler scheduler = Schedulers.newSingle("SimpleReconciler");
	private DiagnosticPublisher diagnosticsPublisher;

	@Autowired
	public void setLinterFunction(LinterFunction linterFunction) {
		this.linterFunction = linterFunction;
	}

	@Autowired
	public void setDocuments(DocumentListenerManager documents) {
		this.documents = documents;
	}
	
	@PostConstruct
	public void initialize() {
		dispoable = documents.onDidChangeContent((TextDocumentContentChange event) -> {
			IDocument doc = event.getDocument();
			Flux<ReconcileProblem> problems = linterFunction.lint(event.getDocument());
			//TODO:  make this 'smarter' to ensure responsiveness, support cancelation etc.
			problems.filter(p -> getDiagnosticSeverity(p)!=null)
			.flatMap(p -> toDiagnostic(doc, p))
			.collectList()
			.subscribeOn(scheduler) //Use a dedicated scheduler for reconciler work.
			.subscribe(diagnostics -> 
				diagnosticsPublisher.publishDiagnostics(event.getDocument().getId(), diagnostics
			));
		});
	}
	
	private Mono<Diagnostic> toDiagnostic(IDocument doc, ReconcileProblem problem) {
		DiagnosticSeverity severity = getDiagnosticSeverity(problem);
		if (severity!=null) {
			try {
				Diagnostic d = new Diagnostic();
				d.setRange(doc.toRange(new Region(problem.getOffset(), problem.getLength())));
				d.setCode(problem.getCode());
				d.setMessage(problem.getMessage());
				d.setSeverity(getDiagnosticSeverity(problem));
				return Mono.just(d);
			} catch (BadLocationException e) {
				//Ignore invalid reconcile problems.
			}
		}
		return Mono.empty();
	}
	
	protected DiagnosticSeverity getDiagnosticSeverity(ReconcileProblem problem) {
		ProblemSeverity severity = problem.getType().getDefaultSeverity();
		switch (severity) {
		case ERROR:
			return DiagnosticSeverity.Error;
		case WARNING:
			return DiagnosticSeverity.Warning;
		case INFO:
			return DiagnosticSeverity.Information;
		case HINT:
			return DiagnosticSeverity.Hint;
		case IGNORE:
			return null;
		default:
			throw new IllegalStateException("Bug! Missing switch case?");
		}
	}

	@PreDestroy
	public void dispose() {
		if (dispoable!=null) {
			dispoable.dispose();
		}
	}

	@Autowired
	public void setDiagnosticsPublisher(DiagnosticPublisher diagnosticsPublisher) {
		this.diagnosticsPublisher = diagnosticsPublisher;
	}
	
}
