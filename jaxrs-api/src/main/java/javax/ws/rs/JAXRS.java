/*
 * Copyright (c) 2018 Markus KARG. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package javax.ws.rs;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * Bootstrap class that is used to startup a JAX-RS application in Java SE
 * environments.
 * <p>
 * The {@code JAXRS} class is available in a Jakarta EE container environment as
 * well; however, support for the Java SE bootstrapping APIs is <em>not
 * required</em> in container environments.
 * </p>
 * <p>
 * In a Java SE environment an application is getting started by the following
 * command using default configuration values (i. e. mounting application at
 * {@code http://localhost:80/}:
 * </p>
 *
 * <pre>
 * JAXRS.start(new MyApplication(), JAXRS.Configuration.builder().build());
 * </pre>
 * <p>
 * The following example shows how to override default values for protocol,
 * host, port and root path:
 * </p>
 *
 * <pre>
 * JAXRS.Configuration config = JAXRS.Configuration.builder().protocol("HTTP").host("0.0.0.0").port(80).rootPath("api")
 * 	.build();
 * JAXRS.start(new MyApplication(), config);
 * </pre>
 *
 * <p>
 * This more complex example shows how to start multiple applications at the
 * same time and how to stop them:
 * </p>
 *
 * <pre>
 * JAXRS.Configuration.Builder configBuilder = JAXRS.Configuration.builder().protocol("HTTP").host("0.0.0.0").port(80);
 * JAXRS.Configuration firstConfig = configBuilder.rootPath("firstApp").build();
 * JAXRS.Configuration secondConfig = configBuilder.rootPath("secondApp").build();
 * CompletionStage&lt;Instance&gt; firstInstance = JAXRS.start(new MyFirstApplication(), firstConfig);
 * CompletionStage&lt;Instance&gt; secondInstance = JAXRS.start(new MySecondApplication(), secondConfig);
 * CompletableFuture.allOf(firstInstance, secondInstance).join(); // Wait for start of both
 * ...
 * CompletableFuture.allOf(firstInstance.stop(), secondInstance.stop()).join(); // Wait for end of both
 * </pre>
 *
 * @author Markus KARG (markus@headcrashing.eu)
 *
 * @since 2.2
 */
public interface JAXRS {

    /**
     * Invoked in Java SE environments to start the provided application at the
     * specified root URL.
     *
     * @param application
     *            The application to start up.
     * @param configuration
     *            Provides information needed for bootstrapping the application.
     * @return {@code CompletionStage} asynchronously producing handle of the
     *         running application {@link JAXRS.Instance instance}.
     */
    static CompletionStage<Instance> start(final Application application, final Configuration configuration) {
	return RuntimeDelegate.getInstance().bootstrap(application, configuration);
    }

    /**
     * Provides information needed by the JAX-RS implementation for bootstrapping an
     * application.
     * <p>
     * The configuration essentially consists of a set of parameters. While the set
     * of actually effective keys is product specific, the key constants defined by
     * the {@link JAXRS.Configuration} interface MUST be effective on all compliant
     * products. Any unknown key MUST be silently ignored.
     * </p>
     *
     * @author Markus KARG (markus@headcrashing.eu)
     *
     * @since 2.2
     */
    public static interface Configuration {

	/**
	 * Configuration key for the protocol an application is bound to. A compliant
	 * implementation at least MUST accept the strings {@code "HTTP"} and
	 * {@code "HTTPS"} if these protocols are supported. The default value is
	 * {@code "HTTP"}.
	 */
	static final String PROTOCOL = "javax.ws.rs.JAXRS.Protocol";

	/**
	 * Configuration key for the hostname or IP address an application is bound to.
	 * If a hostname is provided, the application MUST be bound to <em>all</em> IP
	 * addresses assigned to that hostname. A compliant implementation at least MUST
	 * accept strings bearing hostnames, IP4 address strings, and IP6 address
	 * strings. The default value is {@code "localhost"}.
	 */
	static final String HOST = "javax.ws.rs.JAXRS.Host";

	/**
	 * Configuration key for the TCP port an application is bound to. A compliant
	 * implementation MUST accept {@code java.lang.Integer} values. The default is
	 * {@code 80}.
	 */
	static final String PORT = "javax.ws.rs.JAXRS.Port";

	/**
	 * Configuration key for the root path an application is bound to. The default
	 * value is {@code "/"}.
	 */
	static final String ROOT_PATH = "javax.ws.rs.JAXRS.RootPath";

	/**
	 * Returns the value of the property with the given name, or {@code null} if
	 * there is no property of that name.
	 *
	 * @param name
	 *            a {@code String} specifying the name of the property.
	 * @return an {@code Object} containing the value of the property, or
	 *         {@code null} if no property exists matching the given name.
	 */
	Object getProperty(String name);

	/**
	 * @return {@link Builder} for bootstrap configuration.
	 */
	static Builder builder() {
	    return RuntimeDelegate.getInstance().createConfigurationBuilder();
	};

	/**
	 * Builder for bootstrap {@link Configuration}.
	 *
	 * @author Markus KARG (markus@headcrashing.eu)
	 *
	 * @since 2.2
	 */
	static interface Builder {

	    /**
	     * @return {@link Configuration} built from provided property values.
	     */
	    Configuration build();

	    /**
	     * Sets the property {@code name} to the provided {@code value}.
	     * <p>
	     * This method ignores unknown names and invalid values.
	     * </p>
	     *
	     * @param name
	     *            name of the parameter to set
	     * @param value
	     *            value to set, or {@code null} to use the default value.
	     * @return the updated builder.
	     */
	    Builder property(String name, Object value);

	    /**
	     * Convenience method to set the {@code protocol} to be used.
	     * <p>
	     * Same as if calling {@link #property(String, Object) property(PROTOCOL,
	     * Object)}.
	     * </p>
	     *
	     * @param protocol
	     *            protocol parameter of this configuration, or {@code null} to use
	     *            the default value.
	     * @return the updated builder.
	     * @see JAXRS.Configuration#PROTOCOL
	     */
	    default Builder protocol(String protocol) {
		return property(PROTOCOL, protocol);
	    }

	    /**
	     * Convenience method to set the {@code host} to be used.
	     * <p>
	     * Same as if calling {@link #property(String, Object) property(HOST, Object)}.
	     * </p>
	     *
	     * @param host
	     *            host parameter (IP address or hostname) of this configuration, or
	     *            {@code null} to use the default value.
	     * @return the updated builder.
	     * @see JAXRS.Configuration#HOST
	     */
	    default Builder host(String host) {
		return property(HOST, host);
	    }

	    /**
	     * Convenience method to set the {@code port} to be used.
	     * <p>
	     * Same as if calling {@link #property(String, Object) property(PORT, Object)}.
	     * </p>
	     *
	     * @param port
	     *            port parameter of this configuration, or {@code null} to use the
	     *            default value.
	     * @return the updated builder.
	     * @see JAXRS.Configuration#PORT
	     */
	    default Builder port(String port) {
		return property(PORT, port);
	    }

	    /**
	     * Convenience method to set the {@code rootPath} to be used.
	     * <p>
	     * Same as if calling {@link #property(String, Object) property(ROOT_PATH,
	     * Object)}.
	     * </p>
	     *
	     * @param rootPath
	     *            rootPath parameter of this configuration, or {@code null} to use
	     *            the default value.
	     * @return the updated builder.
	     * @throws IllegalArgumentException
	     *             if the rootPath is {@code null}.
	     * @see JAXRS.Configuration#ROOT_PATH
	     */
	    default Builder rootPath(String rootPath) {
		return property(ROOT_PATH, rootPath);
	    }
	}
    }

    /**
     * Handle of the running application instance.
     *
     * @author Markus KARG (markus@headcrashing.eu)
     *
     * @since 2.2
     */
    public interface Instance {

	/**
	 * Shutdown running application instance.
	 *
	 * @return {@code CompletionStage} asynchronously shutting down this application
	 *         instance.
	 */
	public CompletionStage<Void> stop();
    }

}
