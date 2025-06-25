package com.tcpip147.lsql.eclipse.ui.hyperlink;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

public class JavaHyperlinkDetector extends AbstractHyperlinkDetector implements IHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();
		IRegion lineRegion;
		String line;
		try {
			lineRegion = document.getLineInformationOfOffset(offset);
			int lineOffset = lineRegion.getOffset();
			line = document.get(lineOffset, lineRegion.getLength());
			int[] range = getRangeOfStringLiteral(line, offset - lineOffset);
			if (range[0] < range[1]) {
				IRegion targetRegion = new Region(range[0] + lineOffset, range[1] - range[0]);
				return new IHyperlink[] { new JavaHyperlink(line.substring(range[0] + 1, range[1] - 1), targetRegion) };
			}
		} catch (BadLocationException e) {
			return null;
		}
		return null;
	}

	private int[] getRangeOfStringLiteral(String text, int offset) {
		int[] range = new int[2];
		String regex = "\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"";
		Pattern pattern = Pattern.compile(regex);
		String line = text;
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			if (offset > matcher.start() && offset < matcher.end()) {
				range[0] = matcher.start();
				range[1] = matcher.end();
			}
			if (matcher.end() >= line.length()) {
				break;
			}
			line = line.substring(matcher.end());
		}
		return range;
	}

}
