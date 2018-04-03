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

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.lsp")
public class LanguageServerAppProperties {

	private String name;
	
	/**
	 * If set, the server will try to connect to
	 * the LSP client on this port. Only at most one of `client-port` 
	 * or `server-port` should be set.
	 */
	private Integer clientPort; 
	
	/**
	 * The port on which the server will listen and wait for 
	 * an LSP client to connect.
	 */
	private Integer serverPort;

	public Integer getClientPort() {
		return clientPort;
	}

	public void setClientPort(Integer clientPort) {
		this.clientPort = clientPort;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	} 
	
	
}
