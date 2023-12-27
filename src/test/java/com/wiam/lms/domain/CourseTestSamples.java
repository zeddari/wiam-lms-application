package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Course getCourseSample1() {
        return new Course()
            .id(1L)
            .titleAr("titleAr1")
            .titleLat("titleLat1")
            .description("description1")
            .subTitles("subTitles1")
            .requirement("requirement1")
            .duration(1)
            .option("option1")
            .videoLink("videoLink1");
    }

    public static Course getCourseSample2() {
        return new Course()
            .id(2L)
            .titleAr("titleAr2")
            .titleLat("titleLat2")
            .description("description2")
            .subTitles("subTitles2")
            .requirement("requirement2")
            .duration(2)
            .option("option2")
            .videoLink("videoLink2");
    }

    public static Course getCourseRandomSampleGenerator() {
        return new Course()
            .id(longCount.incrementAndGet())
            .titleAr(UUID.randomUUID().toString())
            .titleLat(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .subTitles(UUID.randomUUID().toString())
            .requirement(UUID.randomUUID().toString())
            .duration(intCount.incrementAndGet())
            .option(UUID.randomUUID().toString())
            .videoLink(UUID.randomUUID().toString());
    }
}
