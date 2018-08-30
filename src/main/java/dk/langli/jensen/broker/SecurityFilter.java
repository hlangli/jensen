package dk.langli.jensen.broker;

import dk.langli.jensen.Request;

@FunctionalInterface
public interface SecurityFilter {
    public boolean isAllowed(Request request);
}
