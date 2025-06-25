package com.tcpip147.lsql.eclipse.ui.control;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class EmptyOverviewRuler implements IOverviewRuler {
	private Composite composite = null;
	private Composite headerComposite = null;

	@Override
	public void setModel(IAnnotationModel model) {
	}

	@Override
	public IAnnotationModel getModel() {
		return null;
	}

	@Override
	public void update() {
	}

	@Override
	public Control createControl(Composite parent, ITextViewer viewer) {
		composite = new Composite(parent, SWT.NONE);
		headerComposite = new Composite(parent, SWT.NONE);
		return composite;
	}

	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public int getLineOfLastMouseButtonActivity() {
		return 0;
	}

	@Override
	public int toDocumentLineNumber(int number) {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public void addAnnotationType(Object type) {
	}

	@Override
	public void addHeaderAnnotationType(Object type) {
	}

	@Override
	public int getAnnotationHeight() {
		return 0;
	}

	@Override
	public Control getHeaderControl() {
		return headerComposite;
	}

	@Override
	public boolean hasAnnotation(int i) {
		return false;
	}

	@Override
	public void removeAnnotationType(Object type) {
	}

	@Override
	public void removeHeaderAnnotationType(Object type) {
	}

	@Override
	public void setAnnotationTypeColor(Object type, Color color) {
	}

	@Override
	public void setAnnotationTypeLayer(Object type, int layer) {
	}
}
