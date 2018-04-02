package org.springframework.lsp.simplelanguageserver.reconcile;

public interface IProblemCollector {

	void beginCollecting();
	void endCollecting();
	void accept(ReconcileProblem problem);

	/**
	 * Problem collector that simply ignores/discards anything passed to it.
	 */
	IProblemCollector NULL = new IProblemCollector() {
		public void beginCollecting() {
		}
		public void endCollecting() {
		}
		public void accept(ReconcileProblem problem) {
		}
	};
}