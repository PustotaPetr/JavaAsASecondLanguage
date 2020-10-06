package io.github.javaasasecondlanguage.homework02.di;

import java.util.SortedMap;

public class Injector {
    public static Context context;

    public static <T> T inject(Class<T> clazz) {
        return inject(clazz, "");
    }

    public static <T> T inject(Class<T> clazz, String qualifier) {
        return context.findInjection(clazz, qualifier);
    }
}
