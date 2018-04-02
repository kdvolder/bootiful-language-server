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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lsp.simplelanguageserver.document.BadLocationException;
import org.springframework.lsp.simplelanguageserver.document.LanguageId;
import org.springframework.lsp.simplelanguageserver.document.TextDocument;
import org.springframework.lsp.simplelanguageserver.document.TextDocumentContentChange;
import org.springframework.lsp.simplelanguageserver.util.AsyncRunner;
import org.springframework.lsp.simplelanguageserver.util.ListenerList;

import com.google.common.collect.ImmutableList;

public class SimpleDocumentStateTracker implements DocumentStateTracker, DocumentListenerManager {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleDocumentStateTracker.class);
	
	private Map<String, TrackedDocument> documents = new HashMap<>();
	private ListenerList<TextDocumentContentChange> documentChangeListeners = new ListenerList<>();
	private ListenerList<TextDocument> documentCloseListeners = new ListenerList<>();
	
	private AsyncRunner async;

	public synchronized TextDocument getDocument(String url) {
		TrackedDocument doc = documents.get(url);
		if (doc==null) {
			log.warn("Trying to get document ["+url+"] but it did not exists. Creating it with language-id 'plaintext'");
			doc = createDocument(url, LanguageId.PLAINTEXT, 0, "");
		}
		return doc.getDocument();
	}

	private synchronized TrackedDocument createDocument(String url, LanguageId languageId, int version, String text) {
		TrackedDocument existingDoc = documents.get(url);
		if (existingDoc!=null) {
			log.warn("Creating document ["+url+"] but it already exists. Reusing existing!");
			return existingDoc;
		}
		TrackedDocument doc = new TrackedDocument(new TextDocument(url, languageId, version, text));
		documents.put(url, doc);
		return doc;
	}

	@Override
	public boolean canHandleIncrementalChanges() {
		return true;
	}

	@Override
	public final void didChange(DidChangeTextDocumentParams params) {
	  async.execute(() -> {
		try {
			VersionedTextDocumentIdentifier docId = params.getTextDocument();
			String url = docId.getUri();
//			Log.debug("didChange: "+url);
			if (url!=null) {
				TextDocument doc = getDocument(url);
				List<TextDocumentContentChangeEvent> changes = params.getContentChanges();
				doc.apply(params);
				fireDidChangeContent(doc, changes);
			}
		} catch (BadLocationException e) {
			log.error("", e);
		}
	  });
	}

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
	  async.execute(() -> {
		TextDocumentItem docId = params.getTextDocument();
		String url = docId.getUri();
		//Log.info("didOpen: "+params.getTextDocument().getUri());
		LanguageId languageId = LanguageId.of(docId.getLanguageId());
		int version = docId.getVersion();
		if (url!=null) {
			String text = params.getTextDocument().getText();
			TrackedDocument td = createDocument(url, languageId, version, text).open();
//			Log.info("Opened "+td.getOpenCount()+" times: "+url);
			TextDocument doc = td.getDocument();
			TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent() {
				@Override
				public Range getRange() {
					return null;
				}

				@Override
				public Integer getRangeLength() {
					return null;
				}

				@Override
				public String getText() {
					return text;
				}
			};
			TextDocumentContentChange evt = new TextDocumentContentChange(doc, ImmutableList.of(change));
			documentChangeListeners.fire(evt);
		}
	  });
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
	  async.execute(() -> {
		//Log.info("didClose: "+params.getTextDocument().getUri());
		String url = params.getTextDocument().getUri();
		if (url!=null) {
			TrackedDocument doc = documents.get(url);
			if (doc!=null) {
				if (doc.close()) {
					log.info("Closed: {}", url);
					//Clear diagnostics when a file is closed. This makes the errors disapear when the language is changed for
					// a document (this resulst in a dicClose even as being sent to the language server if that changes make the
					// document go 'out of scope'.
					documentCloseListeners.fire(doc.getDocument());
					documents.remove(url);
				} else {
					log.warn("Close event ignored! Assuming document still open because openCount = {}", doc.getOpenCount());
				}
			} else {
				log.warn("Document closed, but it didn't exist! Close event ignored");
			}
		}
	  });
	}

	public AsyncRunner getAsync() {
		return async;
	}

	@Autowired public void setAsync(AsyncRunner async) {
		this.async = async;
	}

	void fireDidChangeContent(TextDocument doc, List<TextDocumentContentChangeEvent> changes) {
		documentChangeListeners.fire(new TextDocumentContentChange(doc, changes));
	}

	@Override
	public void onDidChangeContent(Consumer<TextDocumentContentChange> l) {
		documentChangeListeners.add(l);
	}

	@Override
	public void onDidClose(Consumer<TextDocument> l) {
		documentCloseListeners.add(l);
	}
}
