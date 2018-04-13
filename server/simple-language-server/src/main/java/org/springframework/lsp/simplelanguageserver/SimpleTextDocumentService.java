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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentFormattingParams;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams;
import org.eclipse.lsp4j.DocumentRangeFormattingParams;
import org.eclipse.lsp4j.DocumentSymbolParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.ReferenceParams;
import org.eclipse.lsp4j.RenameParams;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lsp.simplelanguageserver.handlers.CompletionHandler;
import org.springframework.util.Assert;

public class SimpleTextDocumentService implements TextDocumentService, ServerCapabilityInitializer {
	
	@Autowired(required=false)
	public void setCompletionHandler(CompletionHandler completionHandler) {
		Assert.isNull(this.completionHandler, "completionHandler already set");
		this.completionHandler = completionHandler;
	}

	public DocumentStateTracker getDocumentStateTracker() {
		return documentStateTracker;
	}
	
	@Autowired(required=false)
	public void setDocumentStateTracker(DocumentStateTracker documentStateTracker) {
		this.documentStateTracker = documentStateTracker;
	}
	
	private CompletionHandler completionHandler;
	private DocumentStateTracker documentStateTracker;

	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(TextDocumentPositionParams position) {
		return completionHandler.completion(position);
	}

	@Override
	public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams position) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<List<? extends Location>> definition(TextDocumentPositionParams position) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(TextDocumentPositionParams position) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<List<? extends SymbolInformation>> documentSymbol(DocumentSymbolParams params) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<List<? extends Command>> codeAction(CodeActionParams params) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		if (documentStateTracker!=null) {
			documentStateTracker.didOpen(params);
		}
	}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		documentStateTracker.didChange(params);
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
		documentStateTracker.didClose(params);
	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeCapabilities(ServerCapabilities c) {
		if (documentStateTracker!=null) {
			c.setTextDocumentSync(documentStateTracker.canHandleIncrementalChanges() 
					? TextDocumentSyncKind.Incremental 
					: TextDocumentSyncKind.Full
			);
		} else {
			c.setTextDocumentSync(TextDocumentSyncKind.None);
		}
	}
}
