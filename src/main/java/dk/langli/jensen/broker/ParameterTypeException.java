package dk.langli.jensen.broker;

@SuppressWarnings("serial")
public class ParameterTypeException extends Exception {
	private final IncompatibleParameter incompatibleParameter;
    
    public ParameterTypeException(String message, Class<?> parameterType, int index) {
        super(message);
        incompatibleParameter = new IncompatibleParameter();
        incompatibleParameter.setParameterType(parameterType);
        incompatibleParameter.setIndex(index);
    }

    public ParameterTypeException(String message, IncompatibleParameter incompatibleParameter) {
        super(message);
        this.incompatibleParameter = incompatibleParameter;
	}

    public IncompatibleParameter getIncompatibleParameter() {
        return incompatibleParameter;
    }
}
