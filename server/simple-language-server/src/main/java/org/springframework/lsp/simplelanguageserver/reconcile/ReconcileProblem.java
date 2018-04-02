package org.springframework.lsp.simplelanguageserver.reconcile;

import java.util.List;

/**
 * Minimal interface that objects representing a reconciler problem must
 * implement.
 *
 * @author Kris De Volder
 */
public interface ReconcileProblem {
	ProblemType getType();
	String getMessage();
	int getOffset();
	int getLength();
	String getCode();
}
