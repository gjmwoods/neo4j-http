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
package org.neo4j.http.plugin;

import org.neo4j.annotations.service.ServiceProvider;
import org.neo4j.common.DependencyResolver;
import org.neo4j.configuration.Config;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.http.Application;
import org.neo4j.kernel.extension.ExtensionFactory;
import org.neo4j.kernel.extension.context.ExtensionContext;
import org.neo4j.kernel.lifecycle.Lifecycle;
import org.neo4j.kernel.lifecycle.LifecycleAdapter;
import org.neo4j.logging.internal.LogService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@ServiceProvider
public class ProxyEmbeddedServerExtension extends ExtensionFactory<ProxyEmbeddedServerExtension.Dependencies>{

	public ProxyEmbeddedServerExtension() {
		super("http-proxy");
	}

	@Override
	public Lifecycle newInstance(ExtensionContext context, Dependencies dependencies) {
		System.out.println("newInstance");
		return new ProxyEmbeddedServerLifecycle();
	}

	interface Dependencies {
		DatabaseManagementService dbms();

		DependencyResolver dependencyResolver();

		Config config();

		LogService logService();
	}

	public static final class ProxyEmbeddedServerLifecycle extends LifecycleAdapter {

		ConfigurableApplicationContext context;

		@Override
		public void start() throws Exception {
			System.out.println("STARTED");
			context = SpringApplication.run(Application.class);
			displayAllBeans(context);
		}

		@Override
		public void stop() throws Exception {
			System.out.println("STOPPED");
			if (context != null) {
				context.stop();
			}
		}
	}

	public static void displayAllBeans(ConfigurableApplicationContext applicationContext) {
		String[] allBeanNames = applicationContext.getBeanDefinitionNames();
		for(String beanName : allBeanNames) {
			var bean = applicationContext.getBean(beanName);
			System.out.println(bean);
		}
	}
}
