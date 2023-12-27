package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfessorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Professor getProfessorSample1() {
        return new Professor()
            .id(1L)
            .phoneNumber("phoneNumber1")
            .mobileNumber("mobileNumber1")
            .about("about1")
            .code("code1")
            .lastDegree("lastDegree1");
    }

    public static Professor getProfessorSample2() {
        return new Professor()
            .id(2L)
            .phoneNumber("phoneNumber2")
            .mobileNumber("mobileNumber2")
            .about("about2")
            .code("code2")
            .lastDegree("lastDegree2");
    }

    public static Professor getProfessorRandomSampleGenerator() {
        return new Professor()
            .id(longCount.incrementAndGet())
            .phoneNumber(UUID.randomUUID().toString())
            .mobileNumber(UUID.randomUUID().toString())
            .about(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .lastDegree(UUID.randomUUID().toString());
    }
}
