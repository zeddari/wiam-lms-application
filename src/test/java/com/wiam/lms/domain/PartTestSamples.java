package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PartTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Part getPartSample1() {
        return new Part().id(1L).titleAr("titleAr1").titleLat("titleLat1").description("description1").duration(1).videoLink("videoLink1");
    }

    public static Part getPartSample2() {
        return new Part().id(2L).titleAr("titleAr2").titleLat("titleLat2").description("description2").duration(2).videoLink("videoLink2");
    }

    public static Part getPartRandomSampleGenerator() {
        return new Part()
            .id(longCount.incrementAndGet())
            .titleAr(UUID.randomUUID().toString())
            .titleLat(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .duration(intCount.incrementAndGet())
            .videoLink(UUID.randomUUID().toString());
    }
}
