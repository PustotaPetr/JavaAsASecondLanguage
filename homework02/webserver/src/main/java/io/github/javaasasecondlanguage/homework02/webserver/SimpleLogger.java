package io.github.javaasasecondlanguage.homework02.webserver;

public class SimpleLogger implements Logger {
    @Override
    public void info(String msg) {
        System.out.println(msg);
    }
}
