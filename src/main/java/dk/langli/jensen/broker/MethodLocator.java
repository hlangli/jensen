package dk.langli.jensen.broker;

import dk.langli.jensen.Request;

public interface MethodLocator {
   public MethodCall getInvocation(Request request) throws ClassNotFoundException, MethodNotFoundException;
}
