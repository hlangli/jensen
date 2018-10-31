package dk.langli.jensen.broker;

import java.lang.reflect.Type;

public class IncompatibleParameter {
    private Type parameterType;
    private int index;

    public Type getParameterType() {
        return parameterType;
    }

    public void setParameterType(Type parameterType) {
        this.parameterType = parameterType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
