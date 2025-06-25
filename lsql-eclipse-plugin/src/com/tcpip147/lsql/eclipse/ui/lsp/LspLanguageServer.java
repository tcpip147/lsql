package com.tcpip147.lsql.eclipse.ui.lsp;

import java.io.File;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.eclipse.lsp4e.server.StreamConnectionProvider;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.wildwebdeveloper.embedder.node.NodeJSManager;

public class LspLanguageServer implements StreamConnectionProvider {

	private Process process;
	private LanguageServer proxy;

	@Override
	public void start() throws IOException {
		File nodejs = NodeJSManager.getNodeJsLocation();
		InputStream in = getClass().getResourceAsStream("/server.js");
		Path serverRunnable = Files.createTempFile("server", ".js");
		Files.copy(in, serverRunnable, StandardCopyOption.REPLACE_EXISTING);
		serverRunnable.toFile().deleteOnExit();
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(nodejs.getAbsolutePath(), serverRunnable.toAbsolutePath().toString(), "--stdio");
		process = pb.start();
	}

	@Override
	public void stop() {
		if (proxy != null) {
			proxy.shutdown();
		}
		if (process != null) {
			process.destroy();
		}
	}

	@Override
	public InputStream getErrorStream() {
		return process.getErrorStream();
	}

	@Override
	public InputStream getInputStream() {
		if ("true".equals(System.getProperty("com.tcpip147.lsql.eclipse.ui.debug"))) {
			return new DebugInputStream(process.getInputStream());
		}
		return process.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() {
		if ("true".equals(System.getProperty("com.tcpip147.lsql.eclipse.ui.debug"))) {
			return new DebugOutputStream(process.getOutputStream());
		}
		return process.getOutputStream();
	}

	public class DebugInputStream extends FilterInputStream {
		public DebugInputStream(InputStream in) {
			super(in);
		}

		@Override
		public int read() throws IOException {
			int b = super.read();
			if (b != -1) {
				System.out.print((char) b);
			}
			return b;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int read = super.read(b, off, len);
			if (read > 0) {
				System.out.print(new String(b, off, read));
			}
			return read;
		}
	}

	public class DebugOutputStream extends FilterOutputStream {
		public DebugOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void write(int b) throws IOException {
			super.write(b);
			System.out.print((char) b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			super.write(b, off, len);
			System.out.print(new String(b, off, len));
		}
	}

	public void setProxy(LanguageServer proxy) {
		this.proxy = proxy;
	}
}
