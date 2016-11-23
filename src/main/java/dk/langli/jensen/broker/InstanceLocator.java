package dk.langli.jensen.broker;

public interface InstanceLocator {
    public Object getInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException;
}
