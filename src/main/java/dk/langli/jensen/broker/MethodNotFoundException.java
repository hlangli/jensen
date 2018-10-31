package dk.langli.jensen.broker;

import java.util.Map;

@SuppressWarnings("serial")
public class MethodNotFoundException extends Exception {
    private final Map<String, IncompatibleParameter> incompatible;

    public MethodNotFoundException(String message, Map<String, IncompatibleParameter> incompatible) {
        this(message, incompatible, null);
    }

    public MethodNotFoundException(String message, Map<String, IncompatibleParameter> incompatible, Throwable target) {
        super(message, target);
        this.incompatible = incompatible;
    }

    public Map<String, IncompatibleParameter> getIncompatible() {
        return incompatible;
    }
}
