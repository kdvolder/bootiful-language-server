package org.springframework.lsp.simplelanguageserver;

import java.util.function.Consumer;

import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.springframework.lsp.simplelanguageserver.document.TextDocument;

public interface DocumentStateTracker {

	boolean canHandleIncrementalChanges();
	void didOpen(DidOpenTextDocumentParams params);
	void didChange(DidChangeTextDocumentParams params);
	void didClose(DidCloseTextDocumentParams params);

}
