package com.wiam.lms.domain;

import static com.wiam.lms.domain.CurrencyTestSamples.*;
import static com.wiam.lms.domain.PaymentTestSamples.*;
import static com.wiam.lms.domain.SponsoringTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CurrencyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Currency.class);
        Currency currency1 = getCurrencySample1();
        Currency currency2 = new Currency();
        assertThat(currency1).isNotEqualTo(currency2);

        currency2.setId(currency1.getId());
        assertThat(currency1).isEqualTo(currency2);

        currency2 = getCurrencySample2();
        assertThat(currency1).isNotEqualTo(currency2);
    }

    @Test
    void sponsoringTest() throws Exception {
        Currency currency = getCurrencyRandomSampleGenerator();
        Sponsoring sponsoringBack = getSponsoringRandomSampleGenerator();

        currency.addSponsoring(sponsoringBack);
        assertThat(currency.getSponsorings()).containsOnly(sponsoringBack);
        assertThat(sponsoringBack.getCurrency()).isEqualTo(currency);

        currency.removeSponsoring(sponsoringBack);
        assertThat(currency.getSponsorings()).doesNotContain(sponsoringBack);
        assertThat(sponsoringBack.getCurrency()).isNull();

        currency.sponsorings(new HashSet<>(Set.of(sponsoringBack)));
        assertThat(currency.getSponsorings()).containsOnly(sponsoringBack);
        assertThat(sponsoringBack.getCurrency()).isEqualTo(currency);

        currency.setSponsorings(new HashSet<>());
        assertThat(currency.getSponsorings()).doesNotContain(sponsoringBack);
        assertThat(sponsoringBack.getCurrency()).isNull();
    }

    @Test
    void paymentTest() throws Exception {
        Currency currency = getCurrencyRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        currency.addPayment(paymentBack);
        assertThat(currency.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getCurrency()).isEqualTo(currency);

        currency.removePayment(paymentBack);
        assertThat(currency.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getCurrency()).isNull();

        currency.payments(new HashSet<>(Set.of(paymentBack)));
        assertThat(currency.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getCurrency()).isEqualTo(currency);

        currency.setPayments(new HashSet<>());
        assertThat(currency.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getCurrency()).isNull();
    }
}
