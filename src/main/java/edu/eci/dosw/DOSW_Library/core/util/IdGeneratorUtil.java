package edu.eci.dosw.DOSW_Library.core.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGeneratorUtil {

    private static final AtomicInteger counter = new AtomicInteger(0);

    private IdGeneratorUtil() {
    }

    public static String generateLoanId() {
        return "LOAN-" + counter.incrementAndGet();
    }

    public static void reset() {
        counter.set(0);
    }
}