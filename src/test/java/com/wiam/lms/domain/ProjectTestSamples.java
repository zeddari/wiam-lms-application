package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Project getProjectSample1() {
        return new Project()
            .id(1L)
            .titleAr("titleAr1")
            .titleLat("titleLat1")
            .description("description1")
            .goals("goals1")
            .requirement("requirement1")
            .videoLink("videoLink1");
    }

    public static Project getProjectSample2() {
        return new Project()
            .id(2L)
            .titleAr("titleAr2")
            .titleLat("titleLat2")
            .description("description2")
            .goals("goals2")
            .requirement("requirement2")
            .videoLink("videoLink2");
    }

    public static Project getProjectRandomSampleGenerator() {
        return new Project()
            .id(longCount.incrementAndGet())
            .titleAr(UUID.randomUUID().toString())
            .titleLat(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .goals(UUID.randomUUID().toString())
            .requirement(UUID.randomUUID().toString())
            .videoLink(UUID.randomUUID().toString());
    }
}
