package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TicketsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Tickets getTicketsSample1() {
        return new Tickets().id(1L).title("title1").description("description1");
    }

    public static Tickets getTicketsSample2() {
        return new Tickets().id(2L).title("title2").description("description2");
    }

    public static Tickets getTicketsRandomSampleGenerator() {
        return new Tickets().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
