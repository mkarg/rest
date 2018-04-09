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

import java.net.URI;
import java.util.ServiceLoader;

import javax.ws.rs.core.Application;

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
interface JAXRS {

    /**
     * Invoked in Java SE environments to start the provided application at the
     * specified root URL.
     *
     * This method will not return until the JAX-RS application is terminated.
     *
     * @param rootURI
     *            The root URI to which the application will be bound.
     * @param application
     *            The application to start up.
     */
    static void start(final URI rootURI, final Application application) {
	ServiceLoader.load(JAXRS.class).iterator().next().bootstrap(rootURI, application);
    }

    /**
     * Implemented by JAX-RS products to actually perform startup of the application
     * in Java SE environments.
     *
     * <em>This method is not intended to be invoked by applications. Call
     * {@link JAXRS#start(URI, Application)} instead.</em>
     *
     * @param rootURI
     *            The root URI to which the application will be bound.
     * @param application
     *            The application to start up.
     */
    default void bootstrap(final URI rootURI, final Application application) {
	throw new UnsupportedOperationException();
    }
}
