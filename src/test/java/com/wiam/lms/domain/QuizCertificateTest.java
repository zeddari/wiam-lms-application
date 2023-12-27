package com.wiam.lms.domain;

import static com.wiam.lms.domain.PartTestSamples.*;
import static com.wiam.lms.domain.QuestionTestSamples.*;
import static com.wiam.lms.domain.QuizCertificateTestSamples.*;
import static com.wiam.lms.domain.QuizCertificateTypeTestSamples.*;
import static com.wiam.lms.domain.SessionTestSamples.*;
import static com.wiam.lms.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QuizCertificateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuizCertificate.class);
        QuizCertificate quizCertificate1 = getQuizCertificateSample1();
        QuizCertificate quizCertificate2 = new QuizCertificate();
        assertThat(quizCertificate1).isNotEqualTo(quizCertificate2);

        quizCertificate2.setId(quizCertificate1.getId());
        assertThat(quizCertificate1).isEqualTo(quizCertificate2);

        quizCertificate2 = getQuizCertificateSample2();
        assertThat(quizCertificate1).isNotEqualTo(quizCertificate2);
    }

    @Test
    void studentsTest() throws Exception {
        QuizCertificate quizCertificate = getQuizCertificateRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        quizCertificate.addStudents(studentBack);
        assertThat(quizCertificate.getStudents()).containsOnly(studentBack);

        quizCertificate.removeStudents(studentBack);
        assertThat(quizCertificate.getStudents()).doesNotContain(studentBack);

        quizCertificate.students(new HashSet<>(Set.of(studentBack)));
        assertThat(quizCertificate.getStudents()).containsOnly(studentBack);

        quizCertificate.setStudents(new HashSet<>());
        assertThat(quizCertificate.getStudents()).doesNotContain(studentBack);
    }

    @Test
    void questionsTest() throws Exception {
        QuizCertificate quizCertificate = getQuizCertificateRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        quizCertificate.addQuestions(questionBack);
        assertThat(quizCertificate.getQuestions()).containsOnly(questionBack);

        quizCertificate.removeQuestions(questionBack);
        assertThat(quizCertificate.getQuestions()).doesNotContain(questionBack);

        quizCertificate.questions(new HashSet<>(Set.of(questionBack)));
        assertThat(quizCertificate.getQuestions()).containsOnly(questionBack);

        quizCertificate.setQuestions(new HashSet<>());
        assertThat(quizCertificate.getQuestions()).doesNotContain(questionBack);
    }

    @Test
    void partTest() throws Exception {
        QuizCertificate quizCertificate = getQuizCertificateRandomSampleGenerator();
        Part partBack = getPartRandomSampleGenerator();

        quizCertificate.setPart(partBack);
        assertThat(quizCertificate.getPart()).isEqualTo(partBack);

        quizCertificate.part(null);
        assertThat(quizCertificate.getPart()).isNull();
    }

    @Test
    void sessionTest() throws Exception {
        QuizCertificate quizCertificate = getQuizCertificateRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        quizCertificate.setSession(sessionBack);
        assertThat(quizCertificate.getSession()).isEqualTo(sessionBack);

        quizCertificate.session(null);
        assertThat(quizCertificate.getSession()).isNull();
    }

    @Test
    void typeTest() throws Exception {
        QuizCertificate quizCertificate = getQuizCertificateRandomSampleGenerator();
        QuizCertificateType quizCertificateTypeBack = getQuizCertificateTypeRandomSampleGenerator();

        quizCertificate.setType(quizCertificateTypeBack);
        assertThat(quizCertificate.getType()).isEqualTo(quizCertificateTypeBack);

        quizCertificate.type(null);
        assertThat(quizCertificate.getType()).isNull();
    }
}
