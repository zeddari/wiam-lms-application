package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Session getSessionSample1() {
        return new Session()
            .id(1L)
            .title("title1")
            .description("description1")
            .sessionSize(1)
            .currency("currency1")
            .targetedAge("targetedAge1")
            .planningType("planningType1");
    }

    public static Session getSessionSample2() {
        return new Session()
            .id(2L)
            .title("title2")
            .description("description2")
            .sessionSize(2)
            .currency("currency2")
            .targetedAge("targetedAge2")
            .planningType("planningType2");
    }

    public static Session getSessionRandomSampleGenerator() {
        return new Session()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .sessionSize(intCount.incrementAndGet())
            .currency(UUID.randomUUID().toString())
            .targetedAge(UUID.randomUUID().toString())
            .planningType(UUID.randomUUID().toString());
    }
}
