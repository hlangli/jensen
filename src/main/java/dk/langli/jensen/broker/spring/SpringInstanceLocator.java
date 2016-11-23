package dk.langli.jensen.broker.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import dk.langli.jensen.broker.InstanceLocator;

public class SpringInstanceLocator implements InstanceLocator {
    private final ApplicationContext applicationContext;
    
    @Autowired
    public SpringInstanceLocator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public Object getInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        return applicationContext.getBean(clazz);
    }
}
