package jersey;

import static java.util.concurrent.TimeUnit.HOURS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriBuilderException;

/**
 * This is just some sandbox to see whether pre-3.1 Jersey would boot in this
 * environment at all.
 *
 * @author Markus KARG (markus@headcrashing.eu)
 */
@Timeout(value = 1, unit = HOURS)
public final class JerseyNativeIT {

    private static Client client;

    @BeforeAll
    static void createClient() {
        JerseyNativeIT.client = ClientBuilder.newClient();
    }

    @AfterAll
    static void disposeClient() {
        JerseyNativeIT.client.close();
    }

    /**
     * This is just some sandbox to see whether pre-3.1 Jersey would boot in this
     * environment at all.
     * 
     * @throws ExecutionException       if the instance didn't boot correctly
     * @throws InterruptedException     if the test took much longer than usually
     *                                  expected
     * @throws IOException
     * @throws UriBuilderException
     * @throws IllegalArgumentException
     */
    @Test
    public final void shouldBootInstanceUsingNativeAPI() throws InterruptedException, ExecutionException,
            IllegalArgumentException, UriBuilderException, IOException {
        // given
        final long expectedResponse = System.currentTimeMillis();
        final Application application = new StaticApplication(expectedResponse);
        final int expectedPort = someFreeIpPort();
        final URI baseUri = UriBuilder.fromUri("http://localhost").port(expectedPort).build();
        final ResourceConfig resourceConfig = ResourceConfig.forApplication(application);

        // when
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig);

        // then
        final int actualPort = server.getListeners().iterator().next().getPort();
        final long actualResponse = client
                .target(UriBuilder.newInstance().scheme("http").host("localhost").port(actualPort).path("resource"))
                .request().get(long.class); // "application/" omitted, as Grizzly Container in Jersey ignores it!

        // then
        assertThat(actualPort, is(greaterThan(0)));
        assertThat(actualPort, is(expectedPort));
        assertThat(actualResponse, is(expectedResponse));
        server.shutdown();
    }

    @ApplicationPath("application")
    public static final class StaticApplication extends Application {

        private final StaticResource staticResource;

        private StaticApplication(final long staticResponse) {
            this.staticResource = new StaticResource(staticResponse);
        }

        @Override
        public final Set<Object> getSingletons() {
            return Collections.<Object>singleton(staticResource);
        }

        @Path("resource")
        public static final class StaticResource {

            private final long staticResponse;

            private StaticResource(final long staticResponse) {
                this.staticResponse = staticResponse;
            }

            @GET
            public final long staticResponse() {
                return this.staticResponse;
            }
        }
    };

    private static final int someFreeIpPort() throws IOException {
        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }

}