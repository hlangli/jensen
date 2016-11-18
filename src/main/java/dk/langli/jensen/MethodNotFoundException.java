package dk.langli.jensen;

import java.util.Map;

@SuppressWarnings("serial")
public class MethodNotFoundException extends Exception {
    private final Map<String, ParameterTypeException> incompatibleMethods;

    public MethodNotFoundException(String message, Map<String, ParameterTypeException> incompatibleMethods) {
        super(message);
        this.incompatibleMethods = incompatibleMethods;
    }

    public Map<String, ParameterTypeException> getIncompatibleMethods() {
        return incompatibleMethods;
    }
}
