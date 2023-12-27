package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SessionJoinModeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SessionJoinMode getSessionJoinModeSample1() {
        return new SessionJoinMode().id(1L).title("title1").description("description1");
    }

    public static SessionJoinMode getSessionJoinModeSample2() {
        return new SessionJoinMode().id(2L).title("title2").description("description2");
    }

    public static SessionJoinMode getSessionJoinModeRandomSampleGenerator() {
        return new SessionJoinMode()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
