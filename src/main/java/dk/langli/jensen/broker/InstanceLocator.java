package dk.langli.jensen.broker;

@FunctionalInterface
public interface InstanceLocator {
    public Object getInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException;
}
