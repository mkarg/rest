package cxf;

import static java.util.concurrent.TimeUnit.HOURS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

/**
 * This is just some sandbox to see whether pre-3.1 Jersey would boot in this
 * environment at all.
 *
 * @author Markus KARG (markus@headcrashing.eu)
 */
@Timeout(value = 1, unit = HOURS)
public final class CXFNativeIT {

    private static Client client;

    @BeforeAll
    static void createClient() {
        CXFNativeIT.client = ClientBuilder.newClient();
    }

    @AfterAll
    static void disposeClient() {
        CXFNativeIT.client.close();
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

        // when
        final RuntimeDelegate delegate = RuntimeDelegate.getInstance();
        final JAXRSServerFactoryBean bean = delegate.createEndpoint(application,
                JAXRSServerFactoryBean.class);
        bean.setAddress("http://localhost:9999" + bean.getAddress());
        System.out.println(bean.getAddress());
        final Server server = bean.create();
        server.start();

        // then
        final int actualPort = 9999;
        final long actualResponse = client
                .target(UriBuilder.fromUri("http://localhost").port(actualPort).path("application/resource"))
                .request().get(long.class); // "application/" omitted, as Grizzly Container in Jersey ignores it!

        // then
        assertThat(actualPort, is(greaterThan(0)));
        assertThat(actualResponse, is(expectedResponse));
        server.stop();
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

}