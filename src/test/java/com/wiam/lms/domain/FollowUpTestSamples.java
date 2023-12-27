package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FollowUpTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static FollowUp getFollowUpSample1() {
        return new FollowUp().id(1L).fromAya(1).toAya(1).notation("notation1").remarks("remarks1");
    }

    public static FollowUp getFollowUpSample2() {
        return new FollowUp().id(2L).fromAya(2).toAya(2).notation("notation2").remarks("remarks2");
    }

    public static FollowUp getFollowUpRandomSampleGenerator() {
        return new FollowUp()
            .id(longCount.incrementAndGet())
            .fromAya(intCount.incrementAndGet())
            .toAya(intCount.incrementAndGet())
            .notation(UUID.randomUUID().toString())
            .remarks(UUID.randomUUID().toString());
    }
}
