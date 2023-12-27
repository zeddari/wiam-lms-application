package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Question2TestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Question2 getQuestion2Sample1() {
        return new Question2()
            .id(1L)
            .questionTitle("questionTitle1")
            .questionDescription("questionDescription1")
            .questionPoint(1)
            .questionSubject("questionSubject1")
            .questionStatus("questionStatus1");
    }

    public static Question2 getQuestion2Sample2() {
        return new Question2()
            .id(2L)
            .questionTitle("questionTitle2")
            .questionDescription("questionDescription2")
            .questionPoint(2)
            .questionSubject("questionSubject2")
            .questionStatus("questionStatus2");
    }

    public static Question2 getQuestion2RandomSampleGenerator() {
        return new Question2()
            .id(longCount.incrementAndGet())
            .questionTitle(UUID.randomUUID().toString())
            .questionDescription(UUID.randomUUID().toString())
            .questionPoint(intCount.incrementAndGet())
            .questionSubject(UUID.randomUUID().toString())
            .questionStatus(UUID.randomUUID().toString());
    }
}
