package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StudentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Student getStudentSample1() {
        return new Student()
            .id(1L)
            .phoneNumber("phoneNumber1")
            .mobileNumber("mobileNumber1")
            .about("about1")
            .code("code1")
            .lastDegree("lastDegree1");
    }

    public static Student getStudentSample2() {
        return new Student()
            .id(2L)
            .phoneNumber("phoneNumber2")
            .mobileNumber("mobileNumber2")
            .about("about2")
            .code("code2")
            .lastDegree("lastDegree2");
    }

    public static Student getStudentRandomSampleGenerator() {
        return new Student()
            .id(longCount.incrementAndGet())
            .phoneNumber(UUID.randomUUID().toString())
            .mobileNumber(UUID.randomUUID().toString())
            .about(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .lastDegree(UUID.randomUUID().toString());
    }
}
