package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TicketSubjectsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TicketSubjects getTicketSubjectsSample1() {
        return new TicketSubjects().id(1L).title("title1").description("description1");
    }

    public static TicketSubjects getTicketSubjectsSample2() {
        return new TicketSubjects().id(2L).title("title2").description("description2");
    }

    public static TicketSubjects getTicketSubjectsRandomSampleGenerator() {
        return new TicketSubjects()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
