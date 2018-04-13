package org.springframework.lsp.simplelanguageserver.autoconf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lsp.simplelanguageserver.SimpleDocumentStateTracker;
import org.springframework.lsp.simplelanguageserver.SimpleLanguageServer;
import org.springframework.lsp.simplelanguageserver.SimpleTextDocumentService;
import org.springframework.lsp.simplelanguageserver.SimpleWorkspaceService;
import org.springframework.lsp.simplelanguageserver.app.LanguageServerApp;
import org.springframework.lsp.simplelanguageserver.app.LanguageServerAppProperties;
import org.springframework.lsp.simplelanguageserver.completions.CompletionProvider;
import org.springframework.lsp.simplelanguageserver.completions.SimpleCompletionEngine;
import org.springframework.lsp.simplelanguageserver.reconcile.LinterFunction;
import org.springframework.lsp.simplelanguageserver.reconcile.SimpleReconciler;
import org.springframework.lsp.simplelanguageserver.util.AsyncRunner;

@Configuration
//TODO: add some @ConditionalOn stuffs so user can override this
public class SimpleLanguageServerAutoConf {

	@Bean AsyncRunner asyncRunner() {
		return new AsyncRunner("SimpleLanguageServer-handler");
	}

	@Bean SimpleLanguageServer server() {
		return new SimpleLanguageServer();
	}

	//@ConditionalOnBean({LinterFunction.class})
	@Bean SimpleReconciler reconciler() {
		return new SimpleReconciler();
	}
	
	//@ConditionalOnBean({CompletionProvider.class})
	@Bean SimpleCompletionEngine completionEngine() {
		return new SimpleCompletionEngine();
	}
	
	@Bean SimpleTextDocumentService textDocumentService() {
		return new SimpleTextDocumentService();
	}
	
	@Bean SimpleDocumentStateTracker documentTracker() {
		return new SimpleDocumentStateTracker();
	}
	
	@Bean SimpleWorkspaceService worspaceService() {
		return new SimpleWorkspaceService();
	}
	
	@Bean LanguageServerApp launcher() {
		return new LanguageServerApp();
	}
	
	@Bean LanguageServerAppProperties properties() {
		return new LanguageServerAppProperties();
	}

}
