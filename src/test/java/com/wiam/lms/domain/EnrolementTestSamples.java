package com.wiam.lms.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class EnrolementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Enrolement getEnrolementSample1() {
        return new Enrolement().id(1L);
    }

    public static Enrolement getEnrolementSample2() {
        return new Enrolement().id(2L);
    }

    public static Enrolement getEnrolementRandomSampleGenerator() {
        return new Enrolement().id(longCount.incrementAndGet());
    }
}
