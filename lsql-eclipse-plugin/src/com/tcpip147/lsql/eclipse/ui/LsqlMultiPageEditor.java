package com.tcpip147.lsql.eclipse.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.tcpip147.lsql.eclipse.ui.control.SqlEditor;
import com.tcpip147.lsql.eclipse.ui.lsp.ParseLsqlMessage;

public class LsqlMultiPageEditor extends MultiPageEditorPart implements IGotoMarker {

	private LsqlContext ctx;
	private DesignPage designPage;
	private TextPage textPage;
	private IResourceChangeListener resourceListener;
	private boolean isDirty;

	public LsqlMultiPageEditor() {
		ctx = new LsqlContext();
		ctx.setMultiPageEditor(this);
		resourceListener = new LsqlResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(event -> {
			if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
				Display.getDefault().asyncExec(() -> firePropertyChange(PROP_TITLE));
			}
		});
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener);
	}

	@Override
	protected void createPages() {
		createDesignPage();
		createTextPage();
		addPageChangedListener(new IPageChangedListener() {
			@Override
			public void pageChanged(PageChangedEvent e) {
				if (getActivePage() == 0) {
					designPage.load();
				}
			}
		});
	}

	private void createDesignPage() {
		designPage = new DesignPage(getContainer(), ctx);
		addPage(0, designPage);
		setPageText(0, "Design");
	}

	private void createTextPage() {
		try {
			textPage = new TextPage();
			ctx.setTextPage(textPage);
			IEditorInput input = getEditorInput();
			addPage(1, textPage, input);
			setPageText(1, "Source");
		} catch (PartInitException e) {
			// TODO
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		saveDesignPage();
		validate();
		getEditor(1).doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		saveDesignPage();
		IEditorPart editorPart = getEditor(1);
		textPage.doSaveAs();
		setInput(editorPart.getEditorInput());
	}

	private void validate() {
		CompletableFuture<ParseLsqlMessage> future = Activator.getDefault().getClient().parseLsql(ctx.getTextPage().getDocumentProvider().getDocument(ctx.getMultiPageEditor().getEditorInput()).get());
		try {
			ParseLsqlMessage message = future.get(3, TimeUnit.SECONDS);
			List<ParseLsqlMessage.RawError> errorMessageList = message.getErrors();
			List<SyntaxError> errorList = new ArrayList<>();
			for (ParseLsqlMessage.RawError raw : errorMessageList) {
				SyntaxError error = new SyntaxError();
				error.setMessage(raw.message);
				error.setPos(raw.pos);
				errorList.add(error);
			}
			IFile file = ((FileEditorInput) ctx.getMultiPageEditor().getEditorInput()).getFile();
			IDocument doc = ctx.getTextPage().getDocumentProvider().getDocument(ctx.getMultiPageEditor().getEditorInput());
			IMarker marker;
			try {
				file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				for (SyntaxError error : errorList) {
					marker = file.createMarker(IMarker.PROBLEM);
					marker.setAttribute(IMarker.MESSAGE, error.getMessage());
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					marker.setAttribute(IMarker.LINE_NUMBER, doc.getLineOfOffset(error.getPos()) + 1);
					marker.setAttribute(IMarker.CHAR_START, error.getPos());
					marker.setAttribute(IMarker.CHAR_END, error.getPos() + 1);
				}
			} catch (CoreException | BadLocationException e) {
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
		}
	}

	private void saveDesignPage() {
		if (getActivePage() == 0) {
			designPage.sync();
			setDirty(false);
		}
	}

	@Override
	public boolean isDirty() {
		if (getActivePage() == 0) {
			return super.isDirty() || isDirty;
		} else {
			return super.isDirty();
		}
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
		textPage.dispose();
		designPage.dispose();
		super.dispose();
	}

	@Override
	public Image getTitleImage() {
		if (hasErrors()) {
			return Activator.getImage("reset");
		}
		return super.getTitleImage();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (input instanceof IFileEditorInput fileInput) {
			setPartName(fileInput.getName());
		} else if (input instanceof FileStoreEditorInput fileInput) {
			setPartName(fileInput.getName());
		}
		super.init(site, input);
	}

	private void superSetInput(FileEditorInput input) {
		if (getEditorInput() != null) {
			IFile file = ((FileEditorInput) getEditorInput()).getFile();
			file.getWorkspace().removeResourceChangeListener(resourceListener);
		}
		setInput(input);
		if (getEditorInput() != null) {
			IFile file = ((FileEditorInput) getEditorInput()).getFile();
			file.getWorkspace().addResourceChangeListener(resourceListener);
			setPartName(file.getName());
		}
	}

	private void closeEditor(boolean save) {
		IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			IWorkbenchPage page = pages[i];
			page.closeEditor(page.findEditor(textPage.getEditorInput()), save);
		}
	}

	public IEditorSite newSite(SqlEditor sqlEditor) {
		return super.createSite(sqlEditor);
	}

	public void navigateTo(String id) {
		designPage.navigateTo(id);
	}

	public void navigateTo(String id, int offset, int length) {
		designPage.navigateTo(id, offset, length);
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void gotoMarker(IMarker marker) {
		if (getActivePage() == 0) {
			int charStart = MarkerUtilities.getCharStart(marker);
			int charEnd = MarkerUtilities.getCharEnd(marker);
			gotoOffset(charStart, charEnd);
		} else {
			textPage.gotoMarker(marker);
		}
	}

	private void gotoOffset(int charStart, int charEnd) {
		CompletableFuture<ParseLsqlMessage> future = Activator.getDefault().getClient().parseLsql(textPage.getDocumentProvider().getDocument(getEditorInput()).get());
		try {
			ParseLsqlMessage message = future.get(3, TimeUnit.SECONDS);
			for (ParseLsqlMessage.RawQuery rawQuery : message.getQueryList().reversed()) {
				if (charStart >= rawQuery.start) {
					navigateTo(rawQuery.id, charStart - rawQuery.start, charEnd - charStart);
					return;
				}
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
		}
	}

	private class LsqlResourceChangeListener implements IResourceChangeListener, IResourceDeltaVisitor {

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IEditorInput input = getEditorInput();
			IResource resource = null;
			if (input instanceof FileEditorInput) {
				resource = ((FileEditorInput) input).getFile();
			} else if (input instanceof FileStoreEditorInput) {
				resource = ((FileStoreEditorInput) input).getAdapter(IResource.class);
			}
			if (delta == null || !delta.getResource().equals(resource)) {
				return true;
			}
			if (delta.getKind() == IResourceDelta.REMOVED) {
				Display display = getSite().getShell().getDisplay();
				if ((delta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
					display.asyncExec(() -> {
						if (!isDirty()) {
							closeEditor(false);
						}
					});
				} else {
					IFile newFile = ResourcesPlugin.getWorkspace().getRoot().getFile(delta.getMovedToPath());
					display.asyncExec(() -> {
						superSetInput(new FileEditorInput(newFile));
					});
				}
			}
			return false;
		}

		@Override
		public void resourceChanged(IResourceChangeEvent evt) {
			try {
				IResourceDelta delta = evt.getDelta();
				if (delta != null) {
					delta.accept(this);
				}
			} catch (CoreException e) {
				// TODO
			}
		}
	}

	private boolean hasErrors() {
		IEditorInput input = getEditorInput();
		IFile file = input.getAdapter(IFile.class);
		if (file == null || !file.exists())
			return false;
		try {
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
			for (IMarker marker : markers) {
				int severity = marker.getAttribute(IMarker.SEVERITY, -1);
				if (severity == IMarker.SEVERITY_ERROR) {
					return true;
				}
			}
		} catch (CoreException e) {
		}
		return false;
	}
}
