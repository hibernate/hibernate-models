package org.hibernate.models.accessor.tck.tests.interfacemethod;

public class GreetingServiceImpl implements GreetingService {

    private String greeting;

    @Override
    public String getGreeting() {
        return greeting;
    }

    @Override
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
