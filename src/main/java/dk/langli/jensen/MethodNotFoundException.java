package dk.langli.jensen;

import java.util.Map;

@SuppressWarnings("serial")
public class MethodNotFoundException extends Exception {
    private final Map<String, Object> incompatibleMethods;

    public MethodNotFoundException(String message, Map<String, Object> incompatibleMethods) {
        super(message);
        this.incompatibleMethods = incompatibleMethods;
    }

    public Map<String, Object> getIncompatibleMethods() {
        return incompatibleMethods;
    }
}
