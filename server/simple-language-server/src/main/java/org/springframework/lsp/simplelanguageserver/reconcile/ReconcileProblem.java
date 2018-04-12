package org.springframework.lsp.simplelanguageserver.reconcile;

import org.eclipse.lsp4j.Range;
import org.springframework.lsp.simplelanguageserver.document.DocumentRegion;

/**
 * Minimal interface that objects representing a reconciler problem must
 * implement.
 *
 * @author Kris De Volder
 */
public interface ReconcileProblem {
	ProblemType getType();
	String getMessage();
	String getCode();
	DocumentRegion getRegion();
}
