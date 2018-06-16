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

import javax.ws.rs.JAXRS;
import javax.ws.rs.JAXRS.Configuration;
import javax.ws.rs.core.Application;

/**
 * Java SE Bootstrap Example.
 *
 * @author Markus KARG (markus@headcrashing.eu)
 */
public final class JavaSeBootstrapMinimal {

    /**
     * Starts example using default properties, running on HTTPS.
     *
     * @param args Command line arguments
     */
    public static void main(final String[] args) {
        final Application application = new HelloWorld();
        final Configuration configuration = Configuration.builder()
                .protocol("HTTP")
                .port(8080)
                .build();
        JAXRS.start(application, configuration).thenAccept(instance -> {
            final Configuration conf = instance.configuration();
            System.out.printf("Instance %s running at %s://%s:%d/%s [Native handle: %s].%n", instance,
                    conf.protocol().toLowerCase(), conf.host(), conf.port(), conf.rootPath(),
                    instance.unwrap(Object.class));
        }).toCompletableFuture().join();

    }
}
