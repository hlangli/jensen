package dk.langli.jensen.broker;

import java.util.Map;

@SuppressWarnings("serial")
public class MethodNotFoundException extends Exception {
    private final Map<String, IncompatibleParameter> incompatibleMethods;

    public MethodNotFoundException(String message, Map<String, IncompatibleParameter> incompatibleMethods) {
        this(message, incompatibleMethods, null);
    }

    public MethodNotFoundException(String message, Map<String, IncompatibleParameter> incompatibleMethods, Throwable target) {
        super(message, target);
        this.incompatibleMethods = incompatibleMethods;
    }

    public Map<String, IncompatibleParameter> getIncompatibleMethods() {
        return incompatibleMethods;
    }
}
