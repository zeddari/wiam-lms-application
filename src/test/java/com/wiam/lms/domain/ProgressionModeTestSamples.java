package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProgressionModeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProgressionMode getProgressionModeSample1() {
        return new ProgressionMode().id(1L).titleAr("titleAr1").titleLat("titleLat1");
    }

    public static ProgressionMode getProgressionModeSample2() {
        return new ProgressionMode().id(2L).titleAr("titleAr2").titleLat("titleLat2");
    }

    public static ProgressionMode getProgressionModeRandomSampleGenerator() {
        return new ProgressionMode()
            .id(longCount.incrementAndGet())
            .titleAr(UUID.randomUUID().toString())
            .titleLat(UUID.randomUUID().toString());
    }
}
