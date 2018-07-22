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

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.ws.rs.JAXRS;
import javax.ws.rs.JAXRS.Configuration;
import javax.ws.rs.core.Application;

/**
 * Java SE Bootstrap Example using CDI
 * <p>
 * This example demonstrates bootstrapping on Java SE platforms with the
 * possibility to inject services by CDI. It will startup the {@link HelloWorld}
 * application and inject a greeting service, effectively answering requests at
 * the URL {@code http://localhost:8080/cdi}.
 * </p>
 * <p>
 * To actually run this example, an implementation of CDI 2.x has to be added to
 * the classpath.
 * </p>
 * <p>
 * Note that support for CDI is <em>not mandatory</em>. Implementations could
 * choose to not implement it, or not to support explicit provision of
 * pre-initialized container initializers. Hence, as the example relies
 * particularly on CDI 2.0 {@code SeContainerInitializer} being supported by the
 * implementation, it is <em>not necessarily</em> portable.
 * </p>
 *
 * @author Markus KARG (markus@headcrashing.eu)
 */
public final class CdiJavaSeBootstrapExample {

    /**
     * Runs this example.
     *
     * @param args
     *            unused command line arguments
     * @throws InterruptedException
     *             when process is killed
     */
    public static final void main(final String[] args) throws InterruptedException {
        final SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        final SeContainer container = initializer.addBeanClasses(HelloWorld.class, GreetingService.class).initialize();

        final Application application = container.select(HelloWorld.class).get();

        final JAXRS.Configuration requestedConfiguration = JAXRS.Configuration.builder().port(8080).build();

        JAXRS.start(application, requestedConfiguration).thenAccept(instance -> {
            Runtime.getRuntime()
                    .addShutdownHook(new Thread(() -> instance.stop()
                            .thenAccept(stopResult -> System.out.printf("Stop result: %s [Native stop result: %s].%n",
                                    stopResult, stopResult.unwrap(Object.class)))));

            final Configuration actualConfigurarion = instance.configuration();
            System.out.printf("Instance %s running at %s://%s:%d%s [Native handle: %s].%n", instance,
                    actualConfigurarion.protocol().toLowerCase(), actualConfigurarion.host(),
                    actualConfigurarion.port(), actualConfigurarion.rootPath(), instance.unwrap(Object.class));
            System.out.println("Send SIGKILL to shutdown.");
        });

        Thread.currentThread().join();
    }

}
