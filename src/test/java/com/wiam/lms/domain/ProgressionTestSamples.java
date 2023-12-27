package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProgressionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Progression getProgressionSample1() {
        return new Progression()
            .id(1L)
            .justifRef("justifRef1")
            .lateArrival(1)
            .earlyDeparture(1)
            .grade1("grade11")
            .description("description1")
            .taskStart(1)
            .taskEnd(1)
            .taskStep(1);
    }

    public static Progression getProgressionSample2() {
        return new Progression()
            .id(2L)
            .justifRef("justifRef2")
            .lateArrival(2)
            .earlyDeparture(2)
            .grade1("grade12")
            .description("description2")
            .taskStart(2)
            .taskEnd(2)
            .taskStep(2);
    }

    public static Progression getProgressionRandomSampleGenerator() {
        return new Progression()
            .id(longCount.incrementAndGet())
            .justifRef(UUID.randomUUID().toString())
            .lateArrival(intCount.incrementAndGet())
            .earlyDeparture(intCount.incrementAndGet())
            .grade1(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .taskStart(intCount.incrementAndGet())
            .taskEnd(intCount.incrementAndGet())
            .taskStep(intCount.incrementAndGet());
    }
}
