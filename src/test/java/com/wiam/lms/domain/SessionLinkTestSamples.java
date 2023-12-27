package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SessionLinkTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SessionLink getSessionLinkSample1() {
        return new SessionLink().id(1L).title("title1").description("description1").link("link1");
    }

    public static SessionLink getSessionLinkSample2() {
        return new SessionLink().id(2L).title("title2").description("description2").link("link2");
    }

    public static SessionLink getSessionLinkRandomSampleGenerator() {
        return new SessionLink()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .link(UUID.randomUUID().toString());
    }
}
