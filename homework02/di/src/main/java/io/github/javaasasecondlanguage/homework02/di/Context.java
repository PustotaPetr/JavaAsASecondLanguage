package io.github.javaasasecondlanguage.homework02.di;

import java.util.*;

public class Context {
    private final Map<Class<?>, HashMap<String, Object>> contextStore;
    public final String emptyQualifier = "";

    public Context() {
        contextStore = new HashMap<>();
        Injector.context = this;
    }

    public <T> Context register(T object, String qualifier) {
        Class<?> classObject = object.getClass();

        Class<?>[] interfaces;
        Class<?>[] rootInterface;

        while (!classObject.equals(Object.class)) {
            putInjection(classObject, object, qualifier);

            interfaces = classObject.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                putInjection(interfaces[i], object, qualifier);
                rootInterface = interfaces[i].getInterfaces();
                while (rootInterface.length > 0) {
                    putInjection(rootInterface[0], object, qualifier);
                    rootInterface = rootInterface[0].getInterfaces();
                }
            }

            classObject = classObject.getSuperclass();
        }
        return this;
    }

    public <T> Context register(T object) {
        return register(object, emptyQualifier);
    }

    public <T> T findInjection(Class<T> clazz, String qualifier) {
        HashMap<String, Object> injection = this.contextStore.get(clazz);

        return (T) injection.get(qualifier);
    }

    private void putInjection(Class<?> classObject, Object object, String qualifier) {
        HashMap<String, Object> node;

        if (this.contextStore.containsKey(classObject)) {
            node = this.contextStore.get(classObject);
        } else {
            node = new HashMap<>();
            this.contextStore.put(classObject, node);
        }

        node.put(qualifier, object);
    }
}
