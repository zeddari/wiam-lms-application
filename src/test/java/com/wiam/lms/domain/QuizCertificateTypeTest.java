package com.wiam.lms.domain;

import static com.wiam.lms.domain.QuizCertificateTestSamples.*;
import static com.wiam.lms.domain.QuizCertificateTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QuizCertificateTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuizCertificateType.class);
        QuizCertificateType quizCertificateType1 = getQuizCertificateTypeSample1();
        QuizCertificateType quizCertificateType2 = new QuizCertificateType();
        assertThat(quizCertificateType1).isNotEqualTo(quizCertificateType2);

        quizCertificateType2.setId(quizCertificateType1.getId());
        assertThat(quizCertificateType1).isEqualTo(quizCertificateType2);

        quizCertificateType2 = getQuizCertificateTypeSample2();
        assertThat(quizCertificateType1).isNotEqualTo(quizCertificateType2);
    }

    @Test
    void quizCertificateTest() throws Exception {
        QuizCertificateType quizCertificateType = getQuizCertificateTypeRandomSampleGenerator();
        QuizCertificate quizCertificateBack = getQuizCertificateRandomSampleGenerator();

        quizCertificateType.addQuizCertificate(quizCertificateBack);
        assertThat(quizCertificateType.getQuizCertificates()).containsOnly(quizCertificateBack);
        assertThat(quizCertificateBack.getType()).isEqualTo(quizCertificateType);

        quizCertificateType.removeQuizCertificate(quizCertificateBack);
        assertThat(quizCertificateType.getQuizCertificates()).doesNotContain(quizCertificateBack);
        assertThat(quizCertificateBack.getType()).isNull();

        quizCertificateType.quizCertificates(new HashSet<>(Set.of(quizCertificateBack)));
        assertThat(quizCertificateType.getQuizCertificates()).containsOnly(quizCertificateBack);
        assertThat(quizCertificateBack.getType()).isEqualTo(quizCertificateType);

        quizCertificateType.setQuizCertificates(new HashSet<>());
        assertThat(quizCertificateType.getQuizCertificates()).doesNotContain(quizCertificateBack);
        assertThat(quizCertificateBack.getType()).isNull();
    }
}
