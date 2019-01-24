package ru.javaops.masterjava.web;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Statistics {
    public enum RESULT {
        SUCCESS, FAIL
    }

    public static void countSuccess(String payload, long startTime, RESULT result) {
        long now = System.currentTimeMillis();
        int ms = (int) (now - startTime);
        log.info(payload + " " + result.name() + " execution time(ms): " + ms);
        // place for statistics staff

    }

    public static void countFault(String payload, long startTime, RESULT result) {
        long now = System.currentTimeMillis();
        int ms = (int) (now - startTime);
        log.error(payload + " " + result.name() + " execution time(ms): " + ms);
        // place for statistics staff

    }
}
