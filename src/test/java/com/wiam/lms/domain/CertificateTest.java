package com.wiam.lms.domain;

import static com.wiam.lms.domain.CertificateTestSamples.*;
import static com.wiam.lms.domain.CoteryTestSamples.*;
import static com.wiam.lms.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CertificateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Certificate.class);
        Certificate certificate1 = getCertificateSample1();
        Certificate certificate2 = new Certificate();
        assertThat(certificate1).isNotEqualTo(certificate2);

        certificate2.setId(certificate1.getId());
        assertThat(certificate1).isEqualTo(certificate2);

        certificate2 = getCertificateSample2();
        assertThat(certificate1).isNotEqualTo(certificate2);
    }

    @Test
    void studentTest() throws Exception {
        Certificate certificate = getCertificateRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        certificate.setStudent(studentBack);
        assertThat(certificate.getStudent()).isEqualTo(studentBack);

        certificate.student(null);
        assertThat(certificate.getStudent()).isNull();
    }

    @Test
    void coteryTest() throws Exception {
        Certificate certificate = getCertificateRandomSampleGenerator();
        Cotery coteryBack = getCoteryRandomSampleGenerator();

        certificate.setCotery(coteryBack);
        assertThat(certificate.getCotery()).isEqualTo(coteryBack);

        certificate.cotery(null);
        assertThat(certificate.getCotery()).isNull();
    }
}
