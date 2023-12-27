package com.wiam.lms.domain;

import static com.wiam.lms.domain.CurrencyTestSamples.*;
import static com.wiam.lms.domain.ProjectTestSamples.*;
import static com.wiam.lms.domain.SponsorTestSamples.*;
import static com.wiam.lms.domain.SponsoringTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SponsoringTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sponsoring.class);
        Sponsoring sponsoring1 = getSponsoringSample1();
        Sponsoring sponsoring2 = new Sponsoring();
        assertThat(sponsoring1).isNotEqualTo(sponsoring2);

        sponsoring2.setId(sponsoring1.getId());
        assertThat(sponsoring1).isEqualTo(sponsoring2);

        sponsoring2 = getSponsoringSample2();
        assertThat(sponsoring1).isNotEqualTo(sponsoring2);
    }

    @Test
    void sponsorTest() throws Exception {
        Sponsoring sponsoring = getSponsoringRandomSampleGenerator();
        Sponsor sponsorBack = getSponsorRandomSampleGenerator();

        sponsoring.setSponsor(sponsorBack);
        assertThat(sponsoring.getSponsor()).isEqualTo(sponsorBack);

        sponsoring.sponsor(null);
        assertThat(sponsoring.getSponsor()).isNull();
    }

    @Test
    void projectTest() throws Exception {
        Sponsoring sponsoring = getSponsoringRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        sponsoring.setProject(projectBack);
        assertThat(sponsoring.getProject()).isEqualTo(projectBack);

        sponsoring.project(null);
        assertThat(sponsoring.getProject()).isNull();
    }

    @Test
    void currencyTest() throws Exception {
        Sponsoring sponsoring = getSponsoringRandomSampleGenerator();
        Currency currencyBack = getCurrencyRandomSampleGenerator();

        sponsoring.setCurrency(currencyBack);
        assertThat(sponsoring.getCurrency()).isEqualTo(currencyBack);

        sponsoring.currency(null);
        assertThat(sponsoring.getCurrency()).isNull();
    }
}
