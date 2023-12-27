package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CoteryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Cotery getCoterySample1() {
        return new Cotery().id(1L).coteryName("coteryName1").studentFullName("studentFullName1");
    }

    public static Cotery getCoterySample2() {
        return new Cotery().id(2L).coteryName("coteryName2").studentFullName("studentFullName2");
    }

    public static Cotery getCoteryRandomSampleGenerator() {
        return new Cotery()
            .id(longCount.incrementAndGet())
            .coteryName(UUID.randomUUID().toString())
            .studentFullName(UUID.randomUUID().toString());
    }
}
