package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CurrencyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Currency getCurrencySample1() {
        return new Currency().id(1L).nameAr("nameAr1").nameLat("nameLat1").code("code1");
    }

    public static Currency getCurrencySample2() {
        return new Currency().id(2L).nameAr("nameAr2").nameLat("nameLat2").code("code2");
    }

    public static Currency getCurrencyRandomSampleGenerator() {
        return new Currency()
            .id(longCount.incrementAndGet())
            .nameAr(UUID.randomUUID().toString())
            .nameLat(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString());
    }
}
