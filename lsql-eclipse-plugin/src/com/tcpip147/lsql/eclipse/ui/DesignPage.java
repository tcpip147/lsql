package com.tcpip147.lsql.eclipse.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.tcpip147.lsql.eclipse.ui.control.LineTextField;
import com.tcpip147.lsql.eclipse.ui.control.ListEx;
import com.tcpip147.lsql.eclipse.ui.control.QueryEditDialog;
import com.tcpip147.lsql.eclipse.ui.control.SqlEditor;
import com.tcpip147.lsql.eclipse.ui.lsp.ParseLsqlMessage;
import com.tcpip147.lsql.eclipse.ui.util.SWTControls;

public class DesignPage extends SashForm {

	private LsqlContext ctx;
	private SqlEditor editor;
	private Composite leftPanel;

	private LineTextField tfId;
	private LineTextField tfDescription;
	private ToolItem btnNew;
	private ToolItem btnDelete;
	private ToolItem btnDuplicate;
	private ToolItem btnMoveUp;
	private ToolItem btnMoveDown;
	private ListEx ltQueries;

	private IDocumentListener tfIdDocumentListener;
	private IDocumentListener tfDescriptionDocumentListener;
	private boolean hasError;
	private int selectedQuery;

	private List<LsqlQuery> queryList = new LinkedList<>();

	public DesignPage(Composite parent, LsqlContext ctx) {
		super(parent, SWT.HORIZONTAL);
		this.ctx = ctx;
		ctx.setDesignPage(this);
		setLayout(new FillLayout());
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createLeftPanel();
		createRightPanel();
		setWeights(220, 80);
		setSashWidth(5);
	}

	private void createLeftPanel() {
		leftPanel = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		leftPanel.setLayout(layout);
		leftPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private SqlEditor createSqlEditor(LsqlQuery query) {
		SqlEditor editor = new SqlEditor(ctx, query);
		try {
			editor.init(ctx.getMultiPageEditor().newSite(editor), new MockInput(query.getSql()));
			editor.createPartControl(leftPanel);
			editor.afterCreation();
		} catch (PartInitException e) {
			// TODO
		}
		return editor;
	}

	private void createRightPanel() {
		Composite panel = new Composite(this, SWT.NONE);
		panel.setLayout(new GridLayout(2, false));
		tfId = SWTControls.createLineTextFieldWithLabel(panel, editor, "Query ID ");
		tfIdDocumentListener = new IDocumentListener() {
			@Override
			public void documentChanged(DocumentEvent event) {
				int index = ltQueries.getSelection();
				if (index > -1) {
					LsqlQuery query = queryList.get(index);
					query.setId(tfId.getText());
					ltQueries.setText(index, tfId.getText());
					ctx.getMultiPageEditor().setDirty(true);
				}
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
		};
		tfDescriptionDocumentListener = new IDocumentListener() {
			@Override
			public void documentChanged(DocumentEvent event) {
				int index = ltQueries.getSelection();
				if (index > -1) {
					LsqlQuery query = queryList.get(index);
					query.setDescription(tfDescription.getText());
					ctx.getMultiPageEditor().setDirty(true);
				}
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
		};
		tfDescription = SWTControls.createLineTextFieldWithLabel(panel, editor, "Description ");
		ToolBar toolbar = new ToolBar(panel, SWT.FLAT | SWT.HORIZONTAL);
		GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.horizontalSpan = 2;
		toolbar.setLayoutData(gridData);

		btnNew = SWTControls.createButtonWithIcon(toolbar, "add");
		btnNew.setToolTipText("Add");
		btnNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				QueryEditDialog dialog = new QueryEditDialog(getShell());
				if (dialog.open() == Dialog.OK) {
					LsqlQuery query = new LsqlQuery();
					query.setId(dialog.getId());
					query.setDescription(dialog.getDescription());
					query.setSql("");
					queryList.add(query);
					ltQueries.add(query.getId());
					ltQueries.setSelection(ltQueries.size() - 1);
					onSelectQuery();
					ctx.getMultiPageEditor().setDirty(true);
				}
			}
		});

		btnDelete = SWTControls.createButtonWithIcon(toolbar, "delete");
		btnDelete.setToolTipText("Delete");
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = ltQueries.getSelection();
				if (index > -1) {
					LsqlQuery query = queryList.remove(index);
					if (query.getEditor() != null) {
						query.getEditor().dispose();
						leftPanel.layout();
					}
					ltQueries.remove(index);
					if (index > ltQueries.size() - 1) {
						ltQueries.setSelection(index - 1);
					} else {
						ltQueries.setSelection(index);
					}
					onSelectQuery();
					ctx.getMultiPageEditor().setDirty(true);
				}
			}
		});

		btnDuplicate = SWTControls.createButtonWithIcon(toolbar, "copy");
		btnDuplicate.setToolTipText("Duplicate");
		btnDuplicate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = ltQueries.getSelection();
				if (index > -1) {
					LsqlQuery query = queryList.get(index);
					queryList.add(index + 1, query.clone());
					ltQueries.add(index + 1, query.getId());
					ltQueries.setSelection(index + 1);
					ctx.getMultiPageEditor().setDirty(true);
				}
			}
		});

		btnMoveUp = SWTControls.createButtonWithIcon(toolbar, "arrow-up");
		btnMoveUp.setToolTipText("Move up");
		btnMoveUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = ltQueries.getSelection();
				if (index > 0) {
					LsqlQuery query = queryList.get(index);
					queryList.remove(index);
					queryList.add(index - 1, query);
					ltQueries.remove(index);
					ltQueries.add(index - 1, query.getId());
					ltQueries.setSelection(index - 1);
					ctx.getMultiPageEditor().setDirty(true);
				}
			}
		});

		btnMoveDown = SWTControls.createButtonWithIcon(toolbar, "arrow-down");
		btnMoveDown.setToolTipText("Move down");
		btnMoveDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = ltQueries.getSelection();
				if (index < ltQueries.size() - 1) {
					LsqlQuery query = queryList.get(index);
					queryList.remove(index);
					queryList.add(index + 1, query);
					ltQueries.remove(index);
					ltQueries.add(index + 1, query.getId());
					ltQueries.setSelection(index + 1);
					ctx.getMultiPageEditor().setDirty(true);
				}
			}
		});

		ltQueries = SWTControls.createListWithLabel(panel, "Query List ");
		ltQueries.addListener(SWT.Selection, (e) -> {
			onSelectQuery();
		});
	}

	private void onSelectQuery() {
		int index = ltQueries.getSelection();
		if (index > -1) {
			LsqlQuery query = queryList.get(index);
			tfId.getDocument().removeDocumentListener(tfIdDocumentListener);
			tfDescription.getDocument().removeDocumentListener(tfDescriptionDocumentListener);
			tfId.setDocument(new Document(query.getId()));
			tfDescription.setDocument(new Document(query.getDescription()));
			tfId.getDocument().addDocumentListener(tfIdDocumentListener);
			tfDescription.getDocument().addDocumentListener(tfDescriptionDocumentListener);
			if (query.getEditor() == null) {
				query.setEditor(createSqlEditor(query));
			}
			for (LsqlQuery q : queryList) {
				if (q.getEditor() != null) {
					q.getEditor().setVisible(false);
				}
			}
			selectedQuery = index;
			editor = query.getEditor();
			editor.setVisible(true);
			leftPanel.layout();
		} else {
			tfId.setText("");
			tfDescription.setText("");
		}
	}

	private class MockInput implements IStorageEditorInput {

		private String value;

		public MockInput(String value) {
			this.value = value;
		}

		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public IPersistableElement getPersistable() {
			return null;
		}

		@Override
		public String getToolTipText() {
			return null;
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			return null;
		}

		@Override
		public IStorage getStorage() throws CoreException {
			return new StringStorage(value);
		}
	}

	private class StringStorage implements IStorage {

		private String value;

		public StringStorage(String value) {
			this.value = value;
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			return null;
		}

		@Override
		public InputStream getContents() throws CoreException {
			return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
		}

		@Override
		public IPath getFullPath() {
			return null;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	}

	public void load() {
		if (queryList != null) {
			for (LsqlQuery query : queryList) {
				if (query.getEditor() != null) {
					query.getEditor().dispose();
				}
			}
			queryList.clear();
		}
		ltQueries.clear();
		for (Control child : leftPanel.getChildren()) {
			child.dispose();
		}
		CompletableFuture<ParseLsqlMessage> future = Activator.getDefault().getClient().parseLsql(ctx.getTextPage().getDocumentProvider().getDocument(ctx.getMultiPageEditor().getEditorInput()).get());
		try {
			ParseLsqlMessage message = future.get(3, TimeUnit.SECONDS);
			for (ParseLsqlMessage.RawQuery rawQuery : message.getQueryList()) {
				LsqlQuery query = new LsqlQuery();
				query.setId(rawQuery.id);
				query.setDescription(rawQuery.description);
				query.setSql(rawQuery.sql);
				queryList.add(query);
			}
			hasError = message.getErrors().size() > 0;
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
		}

		for (LsqlQuery query : queryList) {
			ltQueries.add(query.getId());
		}
		if (selectedQuery > -1 && selectedQuery < ltQueries.size()) {
			ltQueries.setSelection(selectedQuery);
			onSelectQuery();
		}
	}

	@Override
	public void dispose() {
		for (LsqlQuery query : queryList) {
			if (query.getEditor() != null) {
				query.getEditor().dispose();
			}
		}
		super.dispose();
	}

	public void navigateTo(String id) {
		navigateTo(id, 0, 0);
	}

	public void navigateTo(String id, int offset, int length) {
		for (int i = 0; i < queryList.size(); i++) {
			LsqlQuery query = queryList.get(i);
			if (id.equals(query.getId())) {
				ltQueries.setSelection(i);
				onSelectQuery();
				query.getEditor().selectAndReveal(offset, length);
				break;
			}
		}
	}

	public void sync() {
		if (!hasError) {
			String text = getText();
			IDocument document = ctx.getTextPage().getDocumentProvider().getDocument(ctx.getTextPage().getEditorInput());
			if (!document.get().equals(text)) {
				document.set(text);
			}
		} else {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(shell, "Synchronization Error", "Synchronization has been stopped due to an error.");
		}
	}

	public boolean hasError() {
		return hasError;
	}

	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < queryList.size(); i++) {
			LsqlQuery query = queryList.get(i);
			if (i > 0) {
				sb.append("\r\n");
				sb.append("\r\n");
			}
			sb.append("@id ").append(query.getId()).append("\r\n");
			sb.append("@description ").append(query.getDescription() == null ? "" : query.getDescription()).append("\r\n");
			sb.append("\r\n");
			sb.append(query.getSql());
		}
		return sb.toString();
	}

	public List<LsqlQuery> getQueryList() {
		return queryList;
	}

}
