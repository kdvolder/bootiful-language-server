/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.lsp.simplelanguageserver.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.lsp.simplelanguageserver.SimpleLanguageServer;
import org.springframework.util.Assert;


/**
 * LanguageServerApp is responsible for starting language server.
 * 
 * @author Kris De Volder
 * @author Martin Lippert
 */
public class LanguageServerApp implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(LanguageServerApp.class);
	
	public static final String STS4_LANGUAGESERVER_NAME = "sts4.languageserver.name";

	private LanguageServerAppProperties properties;
	private LanguageServer server;
	
	@Autowired
	public void setProperties(LanguageServerAppProperties properties) {
		this.properties = properties;
		Assert.hasText(properties.getName(), "spring.lsp.name property must be set");
		System.setProperty(STS4_LANGUAGESERVER_NAME, properties.getName()); //makes it easy to recognize language server processes.
	}

	@Autowired
	public void setServer(LanguageServer server) {
		this.server = server;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Integer serverPort = properties.getServerPort();
		if (serverPort!=null) {
			startAsSocketServer(serverPort);
			return;
		} 
		Integer clientPort = properties.getClientPort();
		start(clientPort);
	}
	
	protected static class Connection {
		final InputStream in;
		final OutputStream out;
		final Socket socket;

		private Connection(InputStream in, OutputStream out, Socket socket) {
			this.in = in;
			this.out = out;
			this.socket = socket;
		}

		void dispose() {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.warn("", e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.warn("", e);
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					log.warn("", e);
				}
			}
		}
	}

	public void start(Integer clientPort) throws IOException {
		log.info("Starting LS");
		Connection connection = null;
		try {
			connection = connectToClient(clientPort);
			run(connection);
		} catch (Throwable t) {
			log.error("", t);
			System.exit(1); //TODO: proper way to shutdown boot app?
		} finally {
			if (connection != null) {
				connection.dispose();
			}
		}
	}

	/**
	 * starts up the language server and let it listen for connections from the outside
	 * instead of connecting itself to an existing port or channel.
	 *
	 * This is meant for development only, to reduce turnaround times while working
	 * on the language server from within an IDE, so that you can start the language
	 * server right away in debug mode and let the vscode extension connect to that
	 * instance instead of vice versa.
	 *
	 * Source of inspiration:
	 * https://github.com/itemis/xtext-languageserver-example/blob/master/org.xtext.example.mydsl.ide/src/org/xtext/example/mydsl/ide/RunServer.java
	 */
	public void startAsSocketServer(int port) throws IOException, InterruptedException {
		log.info("Starting LS as standlone server on port {}", port);

		Function<MessageConsumer, MessageConsumer> wrapper = consumer -> {
			MessageConsumer result = consumer;
			return result;
		};

		Launcher<LanguageClient> launcher = createSocketLauncher(
				server, 
				LanguageClient.class,
				new InetSocketAddress("localhost", port), 
				createServerThreads(), 
				wrapper
		);

		if (server instanceof LanguageClientAware) {
			((LanguageClientAware)server).connect(launcher.getRemoteProxy());
		}
		Future<?> future = launcher.startListening();
		while (!future.isDone()) {
			//TODO: better way to keep boot app alive than by blocking the runner thread?
			Thread.sleep(10_000l);
		}
	}

	/**
	 * Creates the thread pool / executor passed to lsp4j server intialization. From the looks of things,
	 * @return
	 */
    protected ExecutorService createServerThreads() {
		return Executors.newCachedThreadPool();
	}

	private <T> Launcher<T> createSocketLauncher(Object localService, Class<T> remoteInterface, SocketAddress socketAddress, ExecutorService executorService, Function<MessageConsumer, MessageConsumer> wrapper) throws IOException {
        AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open().bind(socketAddress);
        AsynchronousSocketChannel socketChannel;
        try {
            socketChannel = serverSocket.accept().get();
            return Launcher.createIoLauncher(localService, remoteInterface, Channels.newInputStream(socketChannel), Channels.newOutputStream(socketChannel), executorService, wrapper);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Connection connectToClient(Integer port) throws IOException {
		if (port != null) {
			Socket socket = new Socket("localhost", port);

			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			log.info("Connected to parent using socket on port {}", port);
			return new Connection(in, out, socket);
		}
		else {
			InputStream in = System.in;
			PrintStream out = System.out;

			log.info("Connected to parent using stdio");

			return new Connection(in, out, null);
		}
	}

	/**
	 * Listen for requests from the parent node process.
	 * Send replies asynchronously.
	 * When the request stream is closed, wait for 5s for all outstanding responses to compute, then return.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	protected void run(Connection connection) throws InterruptedException, ExecutionException {
		ExecutorService executor = createServerThreads();
		Function<MessageConsumer, MessageConsumer> wrapper = (MessageConsumer consumer) -> {
			return (msg) -> {
				try {
					consumer.consume(msg);
				} catch (UnsupportedOperationException e) {
					//log a warning and ignore. We are getting some messages from vsCode the server doesn't know about
					log.warn("Unsupported message was ignored!", e);
				}
			};
		};
		Launcher<LanguageClient> launcher = Launcher.createLauncher(server,
				LanguageClient.class,
				connection.in,
				connection.out,
				executor,
				wrapper
		);

		if (server instanceof LanguageClientAware) {
			LanguageClient client = launcher.getRemoteProxy();
			((LanguageClientAware) server).connect(client);
		}

		launcher.startListening().get();
	}

}
