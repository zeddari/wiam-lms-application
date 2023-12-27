package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class QuizCertificateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static QuizCertificate getQuizCertificateSample1() {
        return new QuizCertificate().id(1L).title("title1").description("description1");
    }

    public static QuizCertificate getQuizCertificateSample2() {
        return new QuizCertificate().id(2L).title("title2").description("description2");
    }

    public static QuizCertificate getQuizCertificateRandomSampleGenerator() {
        return new QuizCertificate()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
