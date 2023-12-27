package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EmployeeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Employee getEmployeeSample1() {
        return new Employee()
            .id(1L)
            .phoneNumber("phoneNumber1")
            .mobileNumber("mobileNumber1")
            .about("about1")
            .code("code1")
            .lastDegree("lastDegree1");
    }

    public static Employee getEmployeeSample2() {
        return new Employee()
            .id(2L)
            .phoneNumber("phoneNumber2")
            .mobileNumber("mobileNumber2")
            .about("about2")
            .code("code2")
            .lastDegree("lastDegree2");
    }

    public static Employee getEmployeeRandomSampleGenerator() {
        return new Employee()
            .id(longCount.incrementAndGet())
            .phoneNumber(UUID.randomUUID().toString())
            .mobileNumber(UUID.randomUUID().toString())
            .about(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .lastDegree(UUID.randomUUID().toString());
    }
}
