package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Country2TestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Country2 getCountry2Sample1() {
        return new Country2().id(1L).countryName("countryName1");
    }

    public static Country2 getCountry2Sample2() {
        return new Country2().id(2L).countryName("countryName2");
    }

    public static Country2 getCountry2RandomSampleGenerator() {
        return new Country2().id(longCount.incrementAndGet()).countryName(UUID.randomUUID().toString());
    }
}
