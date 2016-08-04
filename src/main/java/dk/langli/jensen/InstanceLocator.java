package dk.langli.jensen;

public interface InstanceLocator {
    public Object getInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException;
}
