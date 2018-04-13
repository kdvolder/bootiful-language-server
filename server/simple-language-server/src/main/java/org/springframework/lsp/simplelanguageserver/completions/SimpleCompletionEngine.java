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
package org.springframework.lsp.simplelanguageserver.completions;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lsp.simplelanguageserver.DocumentRepository;
import org.springframework.lsp.simplelanguageserver.ServerCapabilityInitializer;
import org.springframework.lsp.simplelanguageserver.document.IDocument;
import org.springframework.lsp.simplelanguageserver.handlers.CompletionHandler;

import com.google.common.collect.ImmutableList;

import reactor.core.publisher.Flux;

public class SimpleCompletionEngine implements CompletionHandler, ServerCapabilityInitializer {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleCompletionEngine.class);

	private List<CompletionProvider> providers;
	private DocumentRepository documents;

//	private ConfigProps props;
	
//	@ConfigurationProperties("spring.lsp.simple.completions")
//	public static class ConfigProps {
//		
//		/**
//		 * Completion engine timeout in ms. Completion engine will only
//		 * wait for at most this amount of time for completion providers.
//		 * When the timeout is reached the engine will return whatever
//		 * completions it has gotten by that time and drop the rest.
//		 */
//		private long timeout = 5_000;
//
//		public long getTimeout() {
//			return timeout;
//		}
//
//		public void setTimeout(long timeout) {
//			this.timeout = timeout;
//		}
//	}

		
//	@Autowired
//	public void setProperties(ConfigProps props) {
//		this.props = props;
//	}
	
	@Autowired
	public void setProviders(List<CompletionProvider> providers) {
		this.providers = providers;
	}

	@Autowired
	public void setDocuments(DocumentRepository documents) {
		this.documents = documents;
	}

	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(TextDocumentPositionParams params) {
		try {
			IDocument doc = documents.getDocument(params.getTextDocument());
			if (doc!=null) { // null? document not open, that shouldn't happen
				int offset = doc.toOffset(params.getPosition());
				return toLSP4J(
						Flux.fromIterable(providers)
						.flatMap(provider -> provider.getCompletions(doc, offset))
						.map(this::toCompletionItem)
				);
			}
			return CompletableFuture.completedFuture(Either.forRight(new CompletionList(false, ImmutableList.of())));
		} catch (Exception e) {
			log.error("", e);
			return CompletableFuture.completedFuture(Either.forRight(new CompletionList(true, ImmutableList.of())));
		}
	}
	
	private CompletableFuture<Either<List<CompletionItem>, CompletionList>> toLSP4J(Flux<CompletionItem> itemFlux) {
		//TODO: add configurable timeout and max completion limit and let completion engine return as many results
		// as it can within the limit. (Should also properly set the isIncomplete attribute to false when a Flux of completions
		// has been terminated by these limiting constraints).
		
		//Note: this works for terminating flux with a timeout:
		//  flux.takeUntilOther(Mono.error(new TimeoutException()).delaySubscription(Duration)) 
		
		return itemFlux
				.collectList()
				.map(items -> Either.<List<CompletionItem>, CompletionList>forRight(new CompletionList(false, items)))
				.toFuture();
	}

	private CompletionItem toCompletionItem(CompletionProposal proposal) {
		CompletionItem completion = new CompletionItem(proposal.getLabel());
		completion.setTextEdit(proposal.getTextEdit());
		completion.setDetail(proposal.getDetail());
		completion.setFilterText(proposal.getFilterText());
		completion.setKind(proposal.getKind());
		return completion;
	}
	
	private Either<List<CompletionItem>, CompletionList> toLSP4J(List<CompletionItem> items) {
		CompletionList result = new CompletionList();
		result.setIsIncomplete(false); //TODO: if we implement limiting by time and element count... this should be set true sometimes.
		result.setItems(items);
		return Either.forRight(result);
	}

	@Override
	public void initializeCapabilities(ServerCapabilities c) {
		//TODO: lazy resolve support? 
		//TODO: configurable trigger characters via @ConfigurationProperties
		c.setCompletionProvider(new CompletionOptions());
	}

}
