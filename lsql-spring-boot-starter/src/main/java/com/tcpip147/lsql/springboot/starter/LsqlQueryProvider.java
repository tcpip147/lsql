package com.tcpip147.lsql.springboot.starter;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.methvin.watcher.DirectoryChangeEvent.EventType;
import io.methvin.watcher.DirectoryWatcher;

public class LsqlQueryProvider {

	private Map<String, LsqlQuery> queryMap = new HashMap<>();
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private Map<Path, ScheduledFuture<?>> debounceMap = new ConcurrentHashMap<>();

	public LsqlQueryProvider(LsqlProperties properties) {
		for (String path : properties.getPaths()) {
			URL url = this.getClass().getResource(path);
			Stream<Path> stream = null;
			Path dir = null;
			try {
				dir = Paths.get(url.toURI());
				stream = Files.walk(dir);
				stream.filter(p -> p.toString().endsWith(".lsql")).forEach(p -> {
					parse(p);
				});
				if (properties.isWatch()) {
					startWatchQueryFiles(dir);
				}
			} catch (IOException | URISyntaxException e) {
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		}
	}

	private void parse(Path path, boolean override) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path.toFile());
			CharStream stream = CharStreams.fromStream(fis);
			LsqlLexer lexer = new LsqlLexer(stream);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			LsqlParser parser = new LsqlParser(tokens);
			LsqlListener listener = new LsqlListener();
			ParseTreeWalker walker = new ParseTreeWalker();
			walker.walk(listener, parser.statements());
			if (override) {
				for (LsqlQuery query : listener.getQueryList()) {
					queryMap.remove(query.getId());
				}
			}
			for (LsqlQuery query : listener.getQueryList()) {
				if (queryMap.get(query.getId()) != null) {
					throw new RuntimeException("Query already exists : " + query.getId());
				}
				queryMap.put(query.getId(), query);
			}
		} catch (IOException e) {
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void parse(Path path) {
		parse(path, false);
	}

	private void startWatchQueryFiles(final Path path) {
		DirectoryWatcher watcher;
		try {
			watcher = DirectoryWatcher.builder().path(path).listener(event -> {
				if (event.eventType() == EventType.MODIFY) {
				}
				debounce(path, () -> {
					if (event.path().toString().toLowerCase().endsWith(".lsql")) {
						parse(event.path(), true);
					}
				});
			}).build();
			watcher.watchAsync();
		} catch (IOException e) {
		}
	}

	private void debounce(Path file, Runnable task) {
		ScheduledFuture<?> existing = debounceMap.get(file);
		if (existing != null && !existing.isDone()) {
			existing.cancel(false);
		}
		ScheduledFuture<?> future = scheduler.schedule(() -> {
			debounceMap.remove(file);
			task.run();
		}, 100, TimeUnit.MILLISECONDS);
		debounceMap.put(file, future);
	}

	public LsqlQuery getQuery(String queryId) {
		return queryMap.get(queryId);
	}
}
