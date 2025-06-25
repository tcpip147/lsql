package com.tcpip147.lsql.eclipse.ui.control;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.tcpip147.lsql.eclipse.ui.util.SWTControls;

public class QueryEditDialog extends Dialog {

	private String title = "Add new query";

	private String id = "";
	private String description = "";

	private LineTextField tfId;
	private LineTextField tfDescription;
	private Label lbMessage;

	public QueryEditDialog(Shell shell) {
		super(shell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite panel = (Composite) super.createDialogArea(parent);
		panel.getShell().setText(title);
		createInputPanel(panel);
		createMessagePanel(panel);
		return panel;
	}

	private void createInputPanel(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tfId = SWTControls.createLineTextFieldWithLabel(panel, null, "Query ID");
		tfId.addListener(SWT.CHANGED, (e) -> {
			id = tfId.getText();
		});
		tfDescription = SWTControls.createLineTextFieldWithLabel(panel, null, "Description");
		tfDescription.addListener(SWT.CHANGED, (e) -> {
			description = tfDescription.getText();
		});
	}

	private void createMessagePanel(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		lbMessage = new Label(panel, SWT.NONE);
		lbMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 220);
	}

	@Override
	protected void okPressed() {
		id = tfId.getText();
		if (id.isEmpty()) {
			lbMessage.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
			lbMessage.setText("Please enter Query ID");
		} else {
			super.okPressed();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
