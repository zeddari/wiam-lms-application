package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Question getQuestionSample1() {
        return new Question()
            .id(1L)
            .question("question1")
            .note("note1")
            .a1("a11")
            .a2("a21")
            .a3("a31")
            .a4("a41")
            .questionTitle("questionTitle1")
            .questionDescription("questionDescription1")
            .questionPoint(1)
            .questionSubject("questionSubject1")
            .questionStatus("questionStatus1");
    }

    public static Question getQuestionSample2() {
        return new Question()
            .id(2L)
            .question("question2")
            .note("note2")
            .a1("a12")
            .a2("a22")
            .a3("a32")
            .a4("a42")
            .questionTitle("questionTitle2")
            .questionDescription("questionDescription2")
            .questionPoint(2)
            .questionSubject("questionSubject2")
            .questionStatus("questionStatus2");
    }

    public static Question getQuestionRandomSampleGenerator() {
        return new Question()
            .id(longCount.incrementAndGet())
            .question(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString())
            .a1(UUID.randomUUID().toString())
            .a2(UUID.randomUUID().toString())
            .a3(UUID.randomUUID().toString())
            .a4(UUID.randomUUID().toString())
            .questionTitle(UUID.randomUUID().toString())
            .questionDescription(UUID.randomUUID().toString())
            .questionPoint(intCount.incrementAndGet())
            .questionSubject(UUID.randomUUID().toString())
            .questionStatus(UUID.randomUUID().toString());
    }
}
