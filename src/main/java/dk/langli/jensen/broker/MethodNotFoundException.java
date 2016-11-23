package dk.langli.jensen.broker;

import java.util.Map;

@SuppressWarnings("serial")
public class MethodNotFoundException extends Exception {
    private final Map<String, Object> incompatibleMethods;

    public MethodNotFoundException(String message, Map<String, Object> incompatibleMethods) {
        this(message, incompatibleMethods, null);
    }

    public MethodNotFoundException(String message, Map<String, Object> incompatibleMethods, Throwable target) {
		super(message, target);
      this.incompatibleMethods = incompatibleMethods;
	}

	public Map<String, Object> getIncompatibleMethods() {
        return incompatibleMethods;
    }
}
