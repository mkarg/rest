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

import java.util.function.Function;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * Bootstrap class that is used to startup a JAX-RS application in Java SE
 * environments.
 *
 * The {@code JAXRS} class is available in a Jakarta EE container environment as
 * well; however, support for the Java SE bootstrapping APIs is <em>not
 * required</em> in container environments.
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
     * This method will not return until the JAX-RS application is terminated.
     *
     * @param application
     *            The application to start up.
     * @param configurationProvider
     *            Returns the value of a requested configuration parameter
     *            identified by the provided key. While the set of actually
     *            effective keys is product specific, the key constants defined by
     *            the {@link JAXRS} interface MUST be accepted by compliant
     *            products.
     */
    static void start(final Application application, final Function<String, Object> configurationProvider) {
	RuntimeDelegate.getInstance().bootstrap(application, configurationProvider);
    }

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
}
