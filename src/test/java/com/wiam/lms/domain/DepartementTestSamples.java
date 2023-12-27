package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DepartementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Departement getDepartementSample1() {
        return new Departement().id(1L).nameAr("nameAr1").nameLat("nameLat1").description("description1");
    }

    public static Departement getDepartementSample2() {
        return new Departement().id(2L).nameAr("nameAr2").nameLat("nameLat2").description("description2");
    }

    public static Departement getDepartementRandomSampleGenerator() {
        return new Departement()
            .id(longCount.incrementAndGet())
            .nameAr(UUID.randomUUID().toString())
            .nameLat(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
