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
package org.springframework.lsp.simplelanguageserver;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lsp.simplelanguageserver.util.AsyncRunner;

import com.google.common.collect.ImmutableList;

public class SimpleLanguageServer implements LanguageServer, LanguageClientAware, DiagnosticPublisher {

	private static Logger log = LoggerFactory.getLogger(SimpleLanguageServer.class);
	private AsyncRunner async;
	private LanguageClient client;
	private TextDocumentService textDocumentService;
	private List<ServerCapabilityInitializer> serverCapabilityInitializers;
	
	private WorkspaceService workspaceService;

	@Autowired
	public void setTextDocumentService(TextDocumentService textDocumentService) {
		this.textDocumentService = textDocumentService;
	}

	@Autowired
	public void setWorkspaceService(WorkspaceService workspaceService) {
		this.workspaceService = workspaceService;
	}

	@Autowired
	public void setServerCapabilityInitializers(List<ServerCapabilityInitializer> serverCapabilityInitializers) {
		this.serverCapabilityInitializers = serverCapabilityInitializers;
	}
	
	@Autowired
	public void setAsync(AsyncRunner async) {
		this.async = async;
	}

	@Override
	public void connect(LanguageClient client) {
		this.client = client;
	}

	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
		return async.invoke(() -> new InitializeResult(getServerCapabilities()));
	}

	protected final ServerCapabilities getServerCapabilities() {
		ServerCapabilities c = new ServerCapabilities();
		log.debug("Configuring Server Capabilities...");
		for (ServerCapabilityInitializer i : serverCapabilityInitializers) {
			log.debug("Calling serverCapabilityInitializer {}", i);
			i.initializeCapabilities(c);
		}
		log.info("Determined Server Capabilities {}", c);
		return c;
	}

	@Override
	public CompletableFuture<Object> shutdown() {
		return CompletableFuture.completedFuture("ok");
	}

	@Override
	public void exit() {
		//Carefull calling System.exit directly can cause deadlock!
		//Not totally understood why.
		async.execute(() -> System.exit(0));
	}

	@Override
	public TextDocumentService getTextDocumentService() {
		return textDocumentService;
	}

	@Override
	public WorkspaceService getWorkspaceService() {
		return workspaceService;
	}

	@Override
	public void publishDiagnostics(TextDocumentIdentifier docId, Collection<Diagnostic> diagnostics) {
		if (client!=null && diagnostics!=null) {
			PublishDiagnosticsParams params = new PublishDiagnosticsParams();
			params.setUri(docId.getUri());
			params.setDiagnostics(ImmutableList.copyOf(diagnostics));
			client.publishDiagnostics(params);
		}
	}

}
