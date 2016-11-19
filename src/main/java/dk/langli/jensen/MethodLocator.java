package dk.langli.jensen;

public interface MethodLocator {
   public MethodCall getInvocation(Request request) throws ClassNotFoundException, MethodNotFoundException;
}
