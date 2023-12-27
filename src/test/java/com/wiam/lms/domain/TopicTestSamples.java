package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TopicTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Topic getTopicSample1() {
        return new Topic().id(1L).titleAr("titleAr1").titleLat("titleLat1").description("description1");
    }

    public static Topic getTopicSample2() {
        return new Topic().id(2L).titleAr("titleAr2").titleLat("titleLat2").description("description2");
    }

    public static Topic getTopicRandomSampleGenerator() {
        return new Topic()
            .id(longCount.incrementAndGet())
            .titleAr(UUID.randomUUID().toString())
            .titleLat(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
