package io.github.javaasasecondlanguage.lecture05.practice3;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

public class HandlerMap implements InvocationHandler {
    private final Map<?, ?> map;
    private final ArrayList<String> history = new ArrayList<>();

    public HandlerMap(Map<?, ?> map) {
        this.map = map;
        history.add("");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object invoke = method.invoke(map, args);
        String status = "";
        for (Object key : map.keySet()) {
            status += "(" + "\"" + key.toString() + "\", " + map.get(key) + "), ";
        }
        if (status.length() > 0) {
            status = status.substring(0, status.length()-2);
        }
        history.add(status);
        String methodStr = "";
        methodStr += method.getName() + "(";
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                methodStr += args[i].toString();
                if (i < args.length - 1) {
                    methodStr += ", ";
                }
            }
        }
        methodStr += ")";
        System.out.println("");
        System.out.println(methodStr);
        System.out.println("History:");

        for (String step: history            ) {
            System.out.println("[" + step + "]");
        }
        return invoke;
    }
}
