package com.wiam.lms.domain;

import static com.wiam.lms.domain.CurrencyTestSamples.*;
import static com.wiam.lms.domain.EnrolementTestSamples.*;
import static com.wiam.lms.domain.PaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payment.class);
        Payment payment1 = getPaymentSample1();
        Payment payment2 = new Payment();
        assertThat(payment1).isNotEqualTo(payment2);

        payment2.setId(payment1.getId());
        assertThat(payment1).isEqualTo(payment2);

        payment2 = getPaymentSample2();
        assertThat(payment1).isNotEqualTo(payment2);
    }

    @Test
    void currencyTest() throws Exception {
        Payment payment = getPaymentRandomSampleGenerator();
        Currency currencyBack = getCurrencyRandomSampleGenerator();

        payment.setCurrency(currencyBack);
        assertThat(payment.getCurrency()).isEqualTo(currencyBack);

        payment.currency(null);
        assertThat(payment.getCurrency()).isNull();
    }

    @Test
    void enrolmentTest() throws Exception {
        Payment payment = getPaymentRandomSampleGenerator();
        Enrolement enrolementBack = getEnrolementRandomSampleGenerator();

        payment.setEnrolment(enrolementBack);
        assertThat(payment.getEnrolment()).isEqualTo(enrolementBack);

        payment.enrolment(null);
        assertThat(payment.getEnrolment()).isNull();
    }
}
