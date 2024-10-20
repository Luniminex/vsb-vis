package org.cardvault.core.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public enum LogLevel {
        DEBUG("\u001B[36m"), // Cyan
        INFO("\u001B[32m"),  // Green
        ERROR("\u001B[31m"), // Red
        SUCCESS("\u001B[34m"), // Blue
        FAIL("\u001B[35m"); // Purple

        private final String color;

        LogLevel(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String RESET = "\u001B[0m";

    public static void log(LogLevel level, String message) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[2];
        String className = element.getClassName();
        String methodName = element.getMethodName();
        String timestamp = dateFormat.format(new Date());

        System.out.printf("%s [%s%s%s] %s.%s - %s%n", timestamp, level.getColor(), level, RESET, className, methodName, message);
    }

    public static void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public static void info(String message) {
        log(LogLevel.INFO, message);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public static void success(String message) {
        log(LogLevel.SUCCESS, message);
    }

    public static void fail(String message) {
        log(LogLevel.FAIL, message);
    }
}
