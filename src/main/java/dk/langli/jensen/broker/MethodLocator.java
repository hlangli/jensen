package dk.langli.jensen.broker;

import dk.langli.jensen.Request;

@FunctionalInterface
public interface MethodLocator {
   public MethodCall getInvocation(Request request) throws ClassNotFoundException, MethodNotFoundException;
}
