package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DiplomaTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DiplomaType getDiplomaTypeSample1() {
        return new DiplomaType().id(1L).titleAr("titleAr1").titleLat("titleLat1");
    }

    public static DiplomaType getDiplomaTypeSample2() {
        return new DiplomaType().id(2L).titleAr("titleAr2").titleLat("titleLat2");
    }

    public static DiplomaType getDiplomaTypeRandomSampleGenerator() {
        return new DiplomaType()
            .id(longCount.incrementAndGet())
            .titleAr(UUID.randomUUID().toString())
            .titleLat(UUID.randomUUID().toString());
    }
}
