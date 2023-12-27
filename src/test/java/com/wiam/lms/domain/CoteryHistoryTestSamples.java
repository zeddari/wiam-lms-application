package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CoteryHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CoteryHistory getCoteryHistorySample1() {
        return new CoteryHistory().id(1L).coteryName("coteryName1").studentFullName("studentFullName1");
    }

    public static CoteryHistory getCoteryHistorySample2() {
        return new CoteryHistory().id(2L).coteryName("coteryName2").studentFullName("studentFullName2");
    }

    public static CoteryHistory getCoteryHistoryRandomSampleGenerator() {
        return new CoteryHistory()
            .id(longCount.incrementAndGet())
            .coteryName(UUID.randomUUID().toString())
            .studentFullName(UUID.randomUUID().toString());
    }
}
