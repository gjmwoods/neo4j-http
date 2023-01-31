/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.http;

import org.neo4j.http.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;

import static org.neo4j.http.plugin.ProxyEmbeddedServerExtension.displayAllBeans;

/**
 * Main entry point.
 * @author Michael J. Simons
 */
@SpringBootApplication(proxyBeanMethods = false)
@EnableConfigurationProperties(ApplicationProperties.class)
public class Application {

	/**
	 * @param args Command line arguments provided to the application.
	 */
	public static void main(String[] args) {
		var context = SpringApplication.run(Application.class, args);
	}
}
