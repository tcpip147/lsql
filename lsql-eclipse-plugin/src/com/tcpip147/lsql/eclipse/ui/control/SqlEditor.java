package com.tcpip147.lsql.eclipse.ui.control;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import com.tcpip147.lsql.eclipse.ui.Activator;
import com.tcpip147.lsql.eclipse.ui.LsqlContext;
import com.tcpip147.lsql.eclipse.ui.LsqlMultiPageEditorActionBarContributor;
import com.tcpip147.lsql.eclipse.ui.LsqlQuery;
import com.tcpip147.lsql.eclipse.ui.lsp.TokenizeSqlMessage;

public class SqlEditor extends TextEditor {

	private LsqlContext ctx;
	private SourceViewer sourceViewer;
	private Composite wrapper;
	private GridData gridData;
	private LsqlQuery query;
	private boolean isDarkMode;

	public SqlEditor(LsqlContext ctx, LsqlQuery query) {
		this.ctx = ctx;
		this.query = query;
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();
		RGB background = currentTheme.getColorRegistry().get("org.eclipse.ui.workbench.ACTIVE_TAB_BG_START").getRGB();
		int brightness = (background.red * 299 + background.green + 587 + background.blue + 114) / 1000;
		if (brightness < 128) {
			isDarkMode = true;
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(new EditorSite((MultiPageEditorSite) site), input);
	}

	@Override
	public void createPartControl(Composite parent) {
		wrapper = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		wrapper.setLayout(layout);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		wrapper.setLayoutData(gridData);
		super.createPartControl(wrapper);
	}

	public void afterCreation() {
		SqlEditor $this = this;
		sourceViewer = (SourceViewer) getSourceViewer();
		sourceViewer.getTextWidget().setLeftMargin(10);
		revalidateHighlighting();

		sourceViewer.getTextWidget().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				LsqlMultiPageEditorActionBarContributor contributor = (LsqlMultiPageEditorActionBarContributor) getEditorSite().getActionBarContributor();
				contributor.setActivePage($this);
				IContextService contextService = (IContextService) ctx.getMultiPageEditor().getSite().getService(IContextService.class);
				contextService.activateContext("org.eclipse.ui.textEditorScope");
			}

			@Override
			public void focusLost(FocusEvent e) {
				Point selection = sourceViewer.getTextWidget().getSelection();
				if (selection.x != selection.y) {
					sourceViewer.getTextWidget().setSelection(selection.x, selection.x);
				}
				getEditorSite().getActionBars().clearGlobalActionHandlers();
			}
		});

		sourceViewer.getDocument().addDocumentListener(new IDocumentListener() {
			@Override
			public void documentChanged(DocumentEvent event) {
				String text = sourceViewer.getTextWidget().getText();
				query.setSql(text);
				revalidateHighlighting();
				ctx.getMultiPageEditor().setDirty(true);
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
		});
	}

	@Override
	protected IOverviewRuler createOverviewRuler(ISharedTextColors sharedColors) {
		return new EmptyOverviewRuler();
	}

	private class EditorSite extends MultiPageEditorSite {

		public EditorSite(MultiPageEditorSite oldSite) {
			super(oldSite.getMultiPageEditor(), oldSite.getEditor());
		}

		@Override
		public IEditorActionBarContributor getActionBarContributor() {
			return new LsqlMultiPageEditorActionBarContributor(this);
		}
	}

	@Override
	public void dispose() {
		wrapper.dispose();
		super.dispose();
	}

	public void setVisible(boolean visible) {
		gridData.exclude = !visible;
		wrapper.setVisible(visible);
	}

	private void revalidateHighlighting() {
		CompletableFuture<TokenizeSqlMessage> future = Activator.getDefault().getClient().tokenizeSql(sourceViewer.getTextWidget().getText());
		try {
			TokenizeSqlMessage message = future.get(3, TimeUnit.SECONDS);
			sourceViewer.getTextWidget().setRedraw(false);
			Color color = Activator.getDefault().getColorMap().get("DEFAULT_COLOR" + (isDarkMode ? "_DARK" : ""));
			sourceViewer.getTextWidget().setStyleRange(new StyleRange(0, sourceViewer.getTextWidget().getText().length(), color, null, SWT.NORMAL));
			for (TokenizeSqlMessage.RawToken token : message.getTokens()) {
				if (token.type == 0 || token.type == 1) {
					color = Activator.getDefault().getColorMap().get("COMMENT_COLOR" + (isDarkMode ? "_DARK" : ""));
				} else if (token.type == 2 || token.type == 3) {
					color = Activator.getDefault().getColorMap().get("STRING_COLOR" + (isDarkMode ? "_DARK" : ""));
				} else if (token.type == 4) {
					color = Activator.getDefault().getColorMap().get("PARAMETER_COLOR" + (isDarkMode ? "_DARK" : ""));
				} else if (token.type == 5) {
					color = Activator.getDefault().getColorMap().get("KEYWORD_COLOR" + (isDarkMode ? "_DARK" : ""));
				}
				StyleRange style = new StyleRange();
				style.start = token.start;
				style.length = token.end - token.start;
				style.fontStyle = token.type == 5 ? SWT.BOLD : SWT.NORMAL;
				style.foreground = color;
				sourceViewer.getTextWidget().setStyleRange(style);
			}
			sourceViewer.getTextWidget().setRedraw(true);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
		}
	}
}
