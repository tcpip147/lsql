package com.tcpip147.lsql.eclipse.ui;

public class LsqlContext {

	private LsqlMultiPageEditor multiPageEditor;
	private DesignPage designPage;
	private TextPage textPage;

	public LsqlMultiPageEditor getMultiPageEditor() {
		return multiPageEditor;
	}

	public void setMultiPageEditor(LsqlMultiPageEditor multiPageEditor) {
		this.multiPageEditor = multiPageEditor;
	}

	public DesignPage getDesignPage() {
		return designPage;
	}

	public void setDesignPage(DesignPage designPage) {
		this.designPage = designPage;
	}

	public TextPage getTextPage() {
		return textPage;
	}

	public void setTextPage(TextPage textPage) {
		this.textPage = textPage;
	}
}
