package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ExamTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Exam getExamSample1() {
        return new Exam()
            .id(1L)
            .comite("comite1")
            .studentName("studentName1")
            .examName("examName1")
            .tajweedScore(1)
            .hifdScore(1)
            .adaeScore(1)
            .decision(1);
    }

    public static Exam getExamSample2() {
        return new Exam()
            .id(2L)
            .comite("comite2")
            .studentName("studentName2")
            .examName("examName2")
            .tajweedScore(2)
            .hifdScore(2)
            .adaeScore(2)
            .decision(2);
    }

    public static Exam getExamRandomSampleGenerator() {
        return new Exam()
            .id(longCount.incrementAndGet())
            .comite(UUID.randomUUID().toString())
            .studentName(UUID.randomUUID().toString())
            .examName(UUID.randomUUID().toString())
            .tajweedScore(intCount.incrementAndGet())
            .hifdScore(intCount.incrementAndGet())
            .adaeScore(intCount.incrementAndGet())
            .decision(intCount.incrementAndGet());
    }
}
