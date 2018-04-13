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
package org.springframework.lsp.sample.wordcheck;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.springframework.lsp.simplelanguageserver.completions.CompletionProposal;
import org.springframework.lsp.simplelanguageserver.document.BadLocationException;
import org.springframework.lsp.simplelanguageserver.document.DocumentRegion;

/**
 * Various convenience methods for creating completion proposals.
 */
public class CompletionProposals {
	
	public static CompletionProposal simple(DocumentRegion replace, String newText) throws BadLocationException {
		Range range = replace.asRange();
		return new CompletionProposal() {
			
			@Override
			public TextEdit getTextEdit() {
				return new TextEdit(range, newText);
			}
			
			@Override
			public String getLabel() {
				return newText;
			}
			
			@Override
			public CompletionItemKind getKind() {
				return CompletionItemKind.Value;
			}
			
			@Override
			public String getDetail() {
				return null;
			}
			
			@Override
			public String toString() {
				return "CompletionProposal("+newText+")";
			}
		};
	}
	
}
