package com.tcpip147.lsql.eclipse.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

public class LsqlMultiPageEditorActionBarContributor extends MultiPageEditorActionBarContributor {

	private IEditorPart activeEditorPart;

	public LsqlMultiPageEditorActionBarContributor() {
	}

	public LsqlMultiPageEditorActionBarContributor(IEditorSite site) {
		super();
		init(site.getActionBars());
	}

	private IAction getAction(ITextEditor editor, String actionId) {
		return (editor == null ? null : editor.getAction(actionId));
	}

	@Override
	public void setActivePage(IEditorPart part) {
		if (activeEditorPart == part) {
			return;
		}

		activeEditorPart = part;

		IActionBars actionBars = getActionBars();
		if (actionBars != null) {
			ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;
			if (editor != null) {
				actionBars.clearGlobalActionHandlers();
				actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
						getAction(editor, ITextEditorActionConstants.DELETE));
				actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
						getAction(editor, ITextEditorActionConstants.UNDO));
				actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
						getAction(editor, ITextEditorActionConstants.REDO));
				actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(),
						getAction(editor, ITextEditorActionConstants.CUT));
				actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
						getAction(editor, ITextEditorActionConstants.COPY));
				actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(),
						getAction(editor, ITextEditorActionConstants.PASTE));
				actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
						getAction(editor, ITextEditorActionConstants.SELECT_ALL));
				actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(),
						getAction(editor, ITextEditorActionConstants.FIND));
				IAction action = getAction(editor, ITextEditorActionConstants.BLOCK_SELECTION_MODE);
				actionBars.setGlobalActionHandler(ITextEditorActionConstants.BLOCK_SELECTION_MODE, action);
				actionBars.updateActionBars();
			}
		}
	}
}
