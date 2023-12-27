package com.wiam.lms.domain;

import static com.wiam.lms.domain.CertificateTestSamples.*;
import static com.wiam.lms.domain.CoteryHistoryTestSamples.*;
import static com.wiam.lms.domain.CoteryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CoteryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cotery.class);
        Cotery cotery1 = getCoterySample1();
        Cotery cotery2 = new Cotery();
        assertThat(cotery1).isNotEqualTo(cotery2);

        cotery2.setId(cotery1.getId());
        assertThat(cotery1).isEqualTo(cotery2);

        cotery2 = getCoterySample2();
        assertThat(cotery1).isNotEqualTo(cotery2);
    }

    @Test
    void coteryHistoryTest() throws Exception {
        Cotery cotery = getCoteryRandomSampleGenerator();
        CoteryHistory coteryHistoryBack = getCoteryHistoryRandomSampleGenerator();

        cotery.addCoteryHistory(coteryHistoryBack);
        assertThat(cotery.getCoteryHistories()).containsOnly(coteryHistoryBack);
        assertThat(coteryHistoryBack.getStudent2()).isEqualTo(cotery);

        cotery.removeCoteryHistory(coteryHistoryBack);
        assertThat(cotery.getCoteryHistories()).doesNotContain(coteryHistoryBack);
        assertThat(coteryHistoryBack.getStudent2()).isNull();

        cotery.coteryHistories(new HashSet<>(Set.of(coteryHistoryBack)));
        assertThat(cotery.getCoteryHistories()).containsOnly(coteryHistoryBack);
        assertThat(coteryHistoryBack.getStudent2()).isEqualTo(cotery);

        cotery.setCoteryHistories(new HashSet<>());
        assertThat(cotery.getCoteryHistories()).doesNotContain(coteryHistoryBack);
        assertThat(coteryHistoryBack.getStudent2()).isNull();
    }

    @Test
    void certificateTest() throws Exception {
        Cotery cotery = getCoteryRandomSampleGenerator();
        Certificate certificateBack = getCertificateRandomSampleGenerator();

        cotery.addCertificate(certificateBack);
        assertThat(cotery.getCertificates()).containsOnly(certificateBack);
        assertThat(certificateBack.getCotery()).isEqualTo(cotery);

        cotery.removeCertificate(certificateBack);
        assertThat(cotery.getCertificates()).doesNotContain(certificateBack);
        assertThat(certificateBack.getCotery()).isNull();

        cotery.certificates(new HashSet<>(Set.of(certificateBack)));
        assertThat(cotery.getCertificates()).containsOnly(certificateBack);
        assertThat(certificateBack.getCotery()).isEqualTo(cotery);

        cotery.setCertificates(new HashSet<>());
        assertThat(cotery.getCertificates()).doesNotContain(certificateBack);
        assertThat(certificateBack.getCotery()).isNull();
    }
}
