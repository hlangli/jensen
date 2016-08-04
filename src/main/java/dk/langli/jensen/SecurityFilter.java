package dk.langli.jensen;

public interface SecurityFilter {
    public boolean isAllowed(MethodCall methodCall, Request request);
}
