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
package org.springframework.lsp.simplelanguageserver.reconcile;

import org.springframework.lsp.simplelanguageserver.document.DocumentRegion;

public class DefaultReconcileProblemImpl implements ReconcileProblem {
	private final DocumentRegion region;
	private final ProblemType type;
	private final String message;

	public DefaultReconcileProblemImpl(ProblemType type, DocumentRegion region, String message) {
		this.type = type;
		this.region = region;
		this.message = message;
	}

	@Override
	public ProblemType getType() {
		return type;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getCode() {
		return type.getCode();
	}

	@Override
	public String toString() {
		return "ReconcileProblem [type=" + type.getCode() + ", message=" + message + "]";
	}

	@Override
	public DocumentRegion getRegion() {
		return region;
	}
}
