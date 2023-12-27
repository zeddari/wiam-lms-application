package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DiplomaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Diploma getDiplomaSample1() {
        return new Diploma()
            .id(1L)
            .title("title1")
            .subject("subject1")
            .detail("detail1")
            .supervisor("supervisor1")
            .grade("grade1")
            .school("school1");
    }

    public static Diploma getDiplomaSample2() {
        return new Diploma()
            .id(2L)
            .title("title2")
            .subject("subject2")
            .detail("detail2")
            .supervisor("supervisor2")
            .grade("grade2")
            .school("school2");
    }

    public static Diploma getDiplomaRandomSampleGenerator() {
        return new Diploma()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .subject(UUID.randomUUID().toString())
            .detail(UUID.randomUUID().toString())
            .supervisor(UUID.randomUUID().toString())
            .grade(UUID.randomUUID().toString())
            .school(UUID.randomUUID().toString());
    }
}
