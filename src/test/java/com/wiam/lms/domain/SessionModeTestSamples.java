package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SessionModeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SessionMode getSessionModeSample1() {
        return new SessionMode().id(1L).title("title1").description("description1");
    }

    public static SessionMode getSessionModeSample2() {
        return new SessionMode().id(2L).title("title2").description("description2");
    }

    public static SessionMode getSessionModeRandomSampleGenerator() {
        return new SessionMode()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
