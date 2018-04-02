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
package org.springframework.lsp.simplelanguageserver.document;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Interface for accessing information about a document
 * including its contents. A IDocument instance is immutable
 * and represents a snapshot of the document state.
 */
public interface IDocument {
	
	//TODO: Cleanup this interface and get rid of
	// methods using IRegion
	// methods using IRegion-like access based on offset + length
	//   (should only use methods based on start - end type character
	//   ranges)

	String getUri();
	String get();
	IRegion getLineInformationOfOffset(int offset);
	int getLength();
	String get(int start, int len) throws BadLocationException;
	int getNumberOfLines();
	String getDefaultLineDelimiter();
	char getChar(int offset) throws BadLocationException;
	int getLineOfOffset(int offset) throws BadLocationException;
	IRegion getLineInformation(int line);
	int getLineOffset(int line) throws BadLocationException;
	void replace(int start, int len, String text) throws BadLocationException;
	String textBetween(int start, int end) throws BadLocationException;
	LanguageId getLanguageId();
	int getVersion();
	
	Range toRange(IRegion asRegion) throws BadLocationException;
	Position toPosition(int offset) throws BadLocationException;

}
