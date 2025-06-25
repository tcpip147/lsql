package com.tcpip147.lsql.eclipse.ui.hyperlink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.tcpip147.lsql.eclipse.ui.Activator;
import com.tcpip147.lsql.eclipse.ui.LsqlMultiPageEditor;
import com.tcpip147.lsql.eclipse.ui.lsp.ParseLsqlMessage;

public class JavaHyperlink implements IHyperlink {

	private final String text;
	private final IRegion region;

	public JavaHyperlink(String text, IRegion region) {
		this.text = text;
		this.region = region;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getHyperlinkText() {
		return null;
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public void open() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			IProject project = page.getActiveEditor().getEditorInput().getAdapter(IFile.class).getProject();
			List<IFile> fileList = listProjectFiles(project);
			IFile targetFile = null;
			for (IFile file : fileList) {
				if ("lsql".equals(file.getFileExtension())) {
					try {
						StringBuilder sb = new StringBuilder();
						try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents()))) {
							int c;
							while ((c = reader.read()) > -1) {
								sb.append((char) c);
							}
						} catch (IOException | CoreException e) {
						}
						CompletableFuture<ParseLsqlMessage> future = Activator.getDefault().getClient().parseLsql(sb.toString());
						ParseLsqlMessage message = future.get(3, TimeUnit.SECONDS);
						for (ParseLsqlMessage.RawQuery query : message.getQueryList()) {
							if (text.equals(query.id)) {
								targetFile = file;
								break;
							}
						}
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
					}
				}
			}
			if (targetFile != null) {
				try {
					LsqlMultiPageEditor editor = (LsqlMultiPageEditor) IDE.openEditor(page, targetFile, true);
					editor.navigateTo(text);
				} catch (PartInitException e) {
				}
			}
		}
	}

	private List<IFile> listProjectFiles(IProject project) {
		List<IFile> fileList = new ArrayList<>();
		listFiles(project, fileList);
		return fileList;
	}

	private void listFiles(IResource resource, List<IFile> fileList) {
		if (resource instanceof IFile file) {
			fileList.add(file);
		} else if (resource instanceof IFolder folder) {
			try {
				for (IResource res : folder.members()) {
					listFiles(res, fileList);
				}
			} catch (CoreException e) {
			}
		} else if (resource instanceof IProject project) {
			try {
				for (IResource res : project.members()) {
					listFiles(res, fileList);
				}
			} catch (CoreException e) {
			}
		}
	}
}
