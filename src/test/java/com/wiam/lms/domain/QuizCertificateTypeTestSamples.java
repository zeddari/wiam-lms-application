package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class QuizCertificateTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static QuizCertificateType getQuizCertificateTypeSample1() {
        return new QuizCertificateType().id(1L).titleAr("titleAr1").titleLat("titleLat1");
    }

    public static QuizCertificateType getQuizCertificateTypeSample2() {
        return new QuizCertificateType().id(2L).titleAr("titleAr2").titleLat("titleLat2");
    }

    public static QuizCertificateType getQuizCertificateTypeRandomSampleGenerator() {
        return new QuizCertificateType()
            .id(longCount.incrementAndGet())
            .titleAr(UUID.randomUUID().toString())
            .titleLat(UUID.randomUUID().toString());
    }
}
