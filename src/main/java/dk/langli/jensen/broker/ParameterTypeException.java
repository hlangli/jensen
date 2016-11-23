package dk.langli.jensen.broker;

@SuppressWarnings("serial")
public class ParameterTypeException extends Exception {
	private final Class<?> parameterType;
	private final int index;
    
    public ParameterTypeException(String message, Class<?> parameterType, int index) {
        super(message);
        this.parameterType = parameterType;
		this.index = index;
	}

    public Class<?> getParameterType() {
        return parameterType;
    }

    public int getIndex() {
        return index;
    }
}
