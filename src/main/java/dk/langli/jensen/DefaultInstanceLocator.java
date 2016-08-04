package dk.langli.jensen;

import java.util.HashMap;
import java.util.Map;

public class DefaultInstanceLocator implements InstanceLocator {
    private Map<Class<?>, Object> instances = new HashMap<Class<?>, Object>();

    @Override
    public Object getInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        Object instance = instances.get(clazz);
        if(instance == null) {
            instance = clazz.newInstance();
            instances.put(clazz, instance);
        }
        return instance;
    }
}
