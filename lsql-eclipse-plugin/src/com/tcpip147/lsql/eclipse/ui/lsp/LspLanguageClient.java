package com.tcpip147.lsql.eclipse.ui.lsp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.jsonrpc.RemoteEndpoint;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

public class LspLanguageClient implements LanguageClient {

	private RemoteEndpoint remoteEndpoint;
	private int requestId = 0;
	private Map<Integer, CompletableFuture<?>> requestMap = new HashMap<>();

	@Override
	public void logMessage(MessageParams message) {
	}

	@Override
	public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
		for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
			if (diagnostic.getSeverity() != null) {
				if (diagnostic.getSeverity() == DiagnosticSeverity.Error) {
				}
			}
		}
	}

	@Override
	public void showMessage(MessageParams messageParams) {
	}

	@Override
	public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
		return null;
	}

	@Override
	public void telemetryEvent(Object object) {
	}

	public void connect(LanguageServer server, RemoteEndpoint remoteEndpoint) {
		this.remoteEndpoint = remoteEndpoint;
	}

	public CompletableFuture<ParseLsqlMessage> parseLsql(String text) {
		CompletableFuture<ParseLsqlMessage> future = new CompletableFuture<>();
		remoteEndpoint.notify("$/parseLsql", Map.of("requestId", requestId, "text", text));
		requestMap.put(requestId++, future);
		return future;
	}

	@SuppressWarnings("unchecked")
	@JsonNotification("$/parseLsql")
	public void handleParseLsql(ParseLsqlMessage message) {
		CompletableFuture<ParseLsqlMessage> future = (CompletableFuture<ParseLsqlMessage>) requestMap.get(message.getRequestId());
		if (future != null) {
			future.complete(message);
		}
	}

	public CompletableFuture<TokenizeSqlMessage> tokenizeSql(String text) {
		CompletableFuture<TokenizeSqlMessage> future = new CompletableFuture<>();
		remoteEndpoint.notify("$/tokenizeSql", Map.of("requestId", requestId, "text", text));
		requestMap.put(requestId++, future);
		return future;
	}

	@SuppressWarnings("unchecked")
	@JsonNotification("$/tokenizeSql")
	public void handleTokenizeSql(TokenizeSqlMessage message) {
		CompletableFuture<TokenizeSqlMessage> future = (CompletableFuture<TokenizeSqlMessage>) requestMap.get(message.getRequestId());
		if (future != null) {
			future.complete(message);
		}
	}
}