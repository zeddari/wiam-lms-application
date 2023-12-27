package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SiteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Site getSiteSample1() {
        return new Site().id(1L).nameAr("nameAr1").nameLat("nameLat1").description("description1").localisation("localisation1");
    }

    public static Site getSiteSample2() {
        return new Site().id(2L).nameAr("nameAr2").nameLat("nameLat2").description("description2").localisation("localisation2");
    }

    public static Site getSiteRandomSampleGenerator() {
        return new Site()
            .id(longCount.incrementAndGet())
            .nameAr(UUID.randomUUID().toString())
            .nameLat(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .localisation(UUID.randomUUID().toString());
    }
}
