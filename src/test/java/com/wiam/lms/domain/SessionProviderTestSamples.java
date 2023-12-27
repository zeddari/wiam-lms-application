package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SessionProviderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SessionProvider getSessionProviderSample1() {
        return new SessionProvider().id(1L).name("name1").description("description1").website("website1");
    }

    public static SessionProvider getSessionProviderSample2() {
        return new SessionProvider().id(2L).name("name2").description("description2").website("website2");
    }

    public static SessionProvider getSessionProviderRandomSampleGenerator() {
        return new SessionProvider()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .website(UUID.randomUUID().toString());
    }
}
