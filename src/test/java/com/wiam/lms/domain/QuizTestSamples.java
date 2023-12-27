package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class QuizTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Quiz getQuizSample1() {
        return new Quiz().id(1L).quizTitle("quizTitle1").quizDescription("quizDescription1");
    }

    public static Quiz getQuizSample2() {
        return new Quiz().id(2L).quizTitle("quizTitle2").quizDescription("quizDescription2");
    }

    public static Quiz getQuizRandomSampleGenerator() {
        return new Quiz()
            .id(longCount.incrementAndGet())
            .quizTitle(UUID.randomUUID().toString())
            .quizDescription(UUID.randomUUID().toString());
    }
}
