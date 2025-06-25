package com.tcpip147.lsql.eclipse.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.tcpip147.lsql.eclipse.ui.Activator;
import com.tcpip147.lsql.eclipse.ui.control.LineTextField;
import com.tcpip147.lsql.eclipse.ui.control.ListEx;
import com.tcpip147.lsql.eclipse.ui.control.SqlEditor;

public class SWTControls {

	public static LineTextField createLineTextFieldWithLabel(Composite parent, SqlEditor editor, String name) {
		Label label = new Label(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gridData);
		label.setText(name);
		return new LineTextField(parent, editor);
	}

	public static ListEx createListWithLabel(Composite parent, String name) {
		ListEx list = new ListEx(parent);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		list.setLayoutData(gridData);
		return list;
	}

	public static ToolItem createButtonWithIcon(ToolBar parent, String icon) {
		ToolItem item = new ToolItem(parent, SWT.PUSH);
		item.setImage(Activator.getImage(icon));
		return item;
	}
}
