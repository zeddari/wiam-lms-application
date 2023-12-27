package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Payment getPaymentSample1() {
        return new Payment()
            .id(1L)
            .paymentMethod("paymentMethod1")
            .paiedBy("paiedBy1")
            .mode("mode1")
            .amount("amount1")
            .fromMonth(1)
            .toMonth(1);
    }

    public static Payment getPaymentSample2() {
        return new Payment()
            .id(2L)
            .paymentMethod("paymentMethod2")
            .paiedBy("paiedBy2")
            .mode("mode2")
            .amount("amount2")
            .fromMonth(2)
            .toMonth(2);
    }

    public static Payment getPaymentRandomSampleGenerator() {
        return new Payment()
            .id(longCount.incrementAndGet())
            .paymentMethod(UUID.randomUUID().toString())
            .paiedBy(UUID.randomUUID().toString())
            .mode(UUID.randomUUID().toString())
            .amount(UUID.randomUUID().toString())
            .fromMonth(intCount.incrementAndGet())
            .toMonth(intCount.incrementAndGet());
    }
}
