package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SponsorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Sponsor getSponsorSample1() {
        return new Sponsor()
            .id(1L)
            .phoneNumber("phoneNumber1")
            .mobileNumber("mobileNumber1")
            .about("about1")
            .code("code1")
            .lastDegree("lastDegree1");
    }

    public static Sponsor getSponsorSample2() {
        return new Sponsor()
            .id(2L)
            .phoneNumber("phoneNumber2")
            .mobileNumber("mobileNumber2")
            .about("about2")
            .code("code2")
            .lastDegree("lastDegree2");
    }

    public static Sponsor getSponsorRandomSampleGenerator() {
        return new Sponsor()
            .id(longCount.incrementAndGet())
            .phoneNumber(UUID.randomUUID().toString())
            .mobileNumber(UUID.randomUUID().toString())
            .about(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .lastDegree(UUID.randomUUID().toString());
    }
}
