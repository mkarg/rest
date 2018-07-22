package jaxrs.examples.bootstrap;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService {

    public String greeting() {
        return "Hello, world!";
    }

}