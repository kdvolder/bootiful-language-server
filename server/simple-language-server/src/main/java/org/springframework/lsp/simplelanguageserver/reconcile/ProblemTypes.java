package org.springframework.lsp.simplelanguageserver.reconcile;

public class ProblemTypes {

	/**
	 * Creates a new problem type. The newly created problem type is not 'equals' to any other
	 * problem type.
	 *
	 * @param defaultSeverity
	 * @param typeName A unique name for this problem type. Note that it is the caller's responsibility that the typeName is unique.
	 *                 If this method is called more than once with identical typeName's it makes no attempts to veify that
	 *                 the name is uniquer, or to return the same object for the same typeName.
	 * @return A newly create problem type.
	 */
	public static ProblemType create(String typeName, ProblemSeverity defaultSeverity) {
		return new ProblemType() {
			@Override
			public String toString() {
				return typeName;
			}
			@Override
			public ProblemSeverity getDefaultSeverity() {
				return defaultSeverity;
			}
			@Override
			public String getCode() {
				return typeName;
			}
		};
	}

}
