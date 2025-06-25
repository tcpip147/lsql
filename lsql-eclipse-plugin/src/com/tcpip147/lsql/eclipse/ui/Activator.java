package com.tcpip147.lsql.eclipse.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.RemoteEndpoint;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.tcpip147.lsql.eclipse.ui.lsp.LspLanguageClient;
import com.tcpip147.lsql.eclipse.ui.lsp.LspLanguageServer;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.tcpip147.lsql.eclipse.ui";
	private static Activator plugin;
	private Map<String, Color> colorMap = new HashMap<>();
	private LspLanguageServer server;
	private LspLanguageClient client;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		registColors();
		startLspServer();
	}

	private void registColors() {
		colorMap.put("COMMENT_COLOR", new Color(Display.getDefault(), 128, 128, 128));
		colorMap.put("COMMENT_COLOR_DARK", new Color(Display.getDefault(), 128, 128, 128));
		colorMap.put("STRING_COLOR", new Color(Display.getDefault(), 0, 128, 0));
		colorMap.put("STRING_COLOR_DARK", new Color(Display.getDefault(), 23, 196, 131));
		colorMap.put("KEYWORD_COLOR", new Color(Display.getDefault(), 128, 0, 0));
		colorMap.put("KEYWORD_COLOR_DARK", new Color(Display.getDefault(), 204, 120, 50));
		colorMap.put("PARAMETER_COLOR", new Color(Display.getDefault(), 0, 0, 128));
		colorMap.put("PARAMETER_COLOR_DARK", new Color(Display.getDefault(), 253, 197, 109));
		colorMap.put("DEFAULT_COLOR", new Color(Display.getDefault(), 0, 0, 0));
		colorMap.put("DEFAULT_COLOR_DARK", new Color(Display.getDefault(), 221, 221, 221));
	}

	private void startLspServer() {
		server = new LspLanguageServer();
		try {
			server.start();
			client = new LspLanguageClient();
			Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(client, server.getInputStream(), server.getOutputStream());
			LanguageServer proxyServer = launcher.getRemoteProxy();
			server.setProxy(proxyServer);
			RemoteEndpoint remoteEndpoint = launcher.getRemoteEndpoint();
			client.connect(proxyServer, remoteEndpoint);
			launcher.startListening();
		} catch (IOException e) {
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		unregistColors();
		stopLspServer();
	}

	private void unregistColors() {
		for (String key : colorMap.keySet()) {
			colorMap.get(key).dispose();
		}
		colorMap.clear();
	}

	private void stopLspServer() {
		server.stop();
	}

	public static Activator getDefault() {
		return plugin;
	}

	public LspLanguageClient getClient() {
		return client;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		ImageDescriptor add = imageDescriptorFromPlugin(PLUGIN_ID, "icons/row_add.png");
		registry.put("add", add);
		ImageDescriptor delete = imageDescriptorFromPlugin(PLUGIN_ID, "icons/row_delete.png");
		registry.put("delete", delete);
		ImageDescriptor edit = imageDescriptorFromPlugin(PLUGIN_ID, "icons/row_edit.png");
		registry.put("edit", edit);
		ImageDescriptor copy = imageDescriptorFromPlugin(PLUGIN_ID, "icons/row_copy.png");
		registry.put("copy", copy);
		ImageDescriptor arrowUp = imageDescriptorFromPlugin(PLUGIN_ID, "icons/arrow_up.png");
		registry.put("arrow-up", arrowUp);
		ImageDescriptor arrowDown = imageDescriptorFromPlugin(PLUGIN_ID, "/icons/arrow_down.png");
		registry.put("arrow-down", arrowDown);
		ImageDescriptor reset = imageDescriptorFromPlugin(PLUGIN_ID, "/icons/reset.png");
		registry.put("reset", reset);
	}

	public static Image getImage(String key) {
		return getDefault().getImageRegistry().get(key);
	}

	public Map<String, Color> getColorMap() {
		return colorMap;
	}
}
