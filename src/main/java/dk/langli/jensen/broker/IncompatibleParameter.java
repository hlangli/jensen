package dk.langli.jensen.broker;

public class IncompatibleParameter {
    private Class<?> parameterType;
    private int index;

    public Class<?> getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
