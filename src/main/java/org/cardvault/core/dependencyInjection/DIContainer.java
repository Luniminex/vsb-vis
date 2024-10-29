package org.cardvault.core.dependencyInjection;

import org.cardvault.core.dependencyInjection.annotations.Initialization;
import org.cardvault.core.dependencyInjection.annotations.Injected;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DIContainer {
    private final Map<Class<?>, Object> services = new HashMap<>();

    public <T> void register(Class<T> serviceClass, T implementation) {
        services.put(serviceClass, implementation);
    }

    public <T> T resolve(Class<T> serviceClass) {
        return (T) services.get(serviceClass);
    }

    public Map<Class<?>, Object> getRegisteredServices() {
        return services;
    }

    public void injectDependencies(Object object) {
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Injected.class) && method.getParameterCount() == 1) {
                Class<?> dependencyType = method.getParameterTypes()[0];
                Object service = resolve(dependencyType);
                if (service != null) {
                    try {
                        method.invoke(object, service);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void init() {
        for (Object service : services.values()) {
            for (Method method : service.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Initialization.class)) {
                    try {
                        method.invoke(service);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
