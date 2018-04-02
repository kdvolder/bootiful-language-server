package org.springframework.lsp.simplelanguageserver.util;

/**
 * A listener used only for testing purposes. It can be attached to a {@link SimpleLanguageServer}
 * to allow tests to receive callbacks for certain 'interesting' points in the language server's
 * processing.
 *
 * @author Kris De Volder
 */
public interface LanguageServerTestListener {
	void reconcileStarted(String uri, int version);
}
