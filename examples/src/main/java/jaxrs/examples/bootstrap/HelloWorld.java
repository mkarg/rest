package jaxrs.examples.bootstrap;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

@ApplicationPath("helloworld")
@Path("hello")
public class HelloWorld extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Collections.singleton(HelloWorld.class);
    }

    @GET
    public String sayHello() {
        return "Hello, World!";
    }

    @Inject
    private GreetingService service;

    @GET
    @Path("cdi")
    public String askService() {
        return this.service.greeting();
    }
}
