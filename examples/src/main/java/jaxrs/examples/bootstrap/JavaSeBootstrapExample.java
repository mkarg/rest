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

package jaxrs.examples.bootstrap;

import java.io.IOException;
import java.util.concurrent.CompletionException;

import javax.ws.rs.JAXRS;
import javax.ws.rs.JAXRS.Configuration;

/**
 * Java SE Bootstrap Example.
 *
 * @author Markus KARG (markus@headcrashing.eu)
 */
public final class JavaSeBootstrapExample {

    /**
     * Starts the example using default properties.
     *
     * @param args
     *            Command line arguments
     */
    public static final void main(final String[] args) {
        JAXRS.start(new HelloWorld(), JAXRS.Configuration.builder().build()).thenCompose(instance -> {
            try {
                final Configuration conf = instance.configuration();
                System.out.printf("Instance %s running at %s://%s:%d%s [Native handle: %s].%n", instance,
                        conf.protocol().toLowerCase(), conf.host(), conf.port(), conf.rootPath(),
                        instance.unwrap(Object.class));
                System.out.println("Press any key to shutdown.");
                System.in.read();
                return instance.stop();
            } catch (final IOException e) {
                throw new CompletionException(e);
            }
        }).thenAccept(stopResult -> System.out.printf("Stop result: %s [Native stop result: %s].%n", stopResult,
                stopResult.unwrap(Object.class))).toCompletableFuture().join();
    }

}
