package dk.langli.jensen;

public interface SecurityFilter {
    public boolean isAllowed(Request request);
}
