package org.springframework.lsp.simplelanguageserver.document;

import org.springframework.util.Assert;

public class DocumentUtil {

	//TODO: this stuff belongs in IDocument and its implementation, not here. This class should be removed.

	/**
	 * Fetch text between two offsets. Doesn't throw BadLocationException.
	 * If either one or both of the offsets points outside the
	 * document then they will be adjusted to point the appropriate boundary to
	 * retrieve the text just upto the end or beginning of the document instead.
	 */
	public static String textBetween(IDocument doc, int start, int end) {
		Assert.isTrue(start<=end);
		if (start>=doc.getLength()) {
			return "";
		}
		if (start<0) {
			start = 0;
		}
		if (end>doc.getLength()) {
			end = doc.getLength();
		}
		if (end<start) {
			end = start;
		}
		try {
			return doc.get(start, end-start);
		} catch (BadLocationException e) {
			//unless the code above is wrong... this is supposed to be impossible!
			throw new IllegalStateException("Bug!", e);
		}
	}

}
