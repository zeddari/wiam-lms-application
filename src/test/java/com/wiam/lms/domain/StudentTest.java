package com.wiam.lms.domain;

import static com.wiam.lms.domain.AnswerTestSamples.*;
import static com.wiam.lms.domain.CertificateTestSamples.*;
import static com.wiam.lms.domain.CoteryHistoryTestSamples.*;
import static com.wiam.lms.domain.CountryTestSamples.*;
import static com.wiam.lms.domain.EnrolementTestSamples.*;
import static com.wiam.lms.domain.GroupTestSamples.*;
import static com.wiam.lms.domain.ProgressionTestSamples.*;
import static com.wiam.lms.domain.QuizCertificateTestSamples.*;
import static com.wiam.lms.domain.SponsorTestSamples.*;
import static com.wiam.lms.domain.StudentTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StudentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Student.class);
        Student student1 = getStudentSample1();
        Student student2 = new Student();
        assertThat(student1).isNotEqualTo(student2);

        student2.setId(student1.getId());
        assertThat(student1).isEqualTo(student2);

        student2 = getStudentSample2();
        assertThat(student1).isNotEqualTo(student2);
    }

    @Test
    void userCustomTest() throws Exception {
        Student student = getStudentRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        student.setUserCustom(userCustomBack);
        assertThat(student.getUserCustom()).isEqualTo(userCustomBack);

        student.userCustom(null);
        assertThat(student.getUserCustom()).isNull();
    }

    @Test
    void coteryHistoryTest() throws Exception {
        Student student = getStudentRandomSampleGenerator();
        CoteryHistory coteryHistoryBack = getCoteryHistoryRandomSampleGenerator();

        student.addCoteryHistory(coteryHistoryBack);
        assertThat(student.getCoteryHistories()).containsOnly(coteryHistoryBack);
        assertThat(coteryHistoryBack.getStudent()).isEqualTo(student);

        student.removeCoteryHistory(coteryHistoryBack);
        assertThat(student.getCoteryHistories()).doesNotContain(coteryHistoryBack);
        assertThat(coteryHistoryBack.getStudent()).isNull();

        student.coteryHistories(new HashSet<>(Set.of(coteryHistoryBack)));
        assertThat(student.getCoteryHistories()).containsOnly(coteryHistoryBack);
        assertThat(coteryHistoryBack.getStudent()).isEqualTo(student);

        student.setCoteryHistories(new HashSet<>());
        assertThat(student.getCoteryHistories()).doesNotContain(coteryHistoryBack);
        assertThat(coteryHistoryBack.getStudent()).isNull();
    }

    @Test
    void certificateTest() throws Exception {
        Student student = getStudentRandomSampleGenerator();
        Certificate certificateBack = getCertificateRandomSampleGenerator();

        student.addCertificate(certificateBack);
        assertThat(student.getCertificates()).containsOnly(certificateBack);
        assertThat(certificateBack.getStudent()).isEqualTo(student);

        student.removeCertificate(certificateBack);
        assertThat(student.getCertificates()).doesNotContain(certificateBack);
        assertThat(certificateBack.getStudent()).isNull();

        student.certificates(new HashSet<>(Set.of(certificateBack)));
        assertThat(student.getCertificates()).containsOnly(certificateBack);
        assertThat(certificateBack.getStudent()).isEqualTo(student);

        student.setCertificates(new HashSet<>());
        assertThat(student.getCertificates()).doesNotContain(certificateBack);
        assertThat(certificateBack.getStudent()).isNull();
    }

    @Test
    void enrolementTest() throws Exception {
        Student student = getStudentRandomSampleGenerator();
        Enrolement enrolementBack = getEnrolementRandomSampleGenerator();

        student.addEnrolement(enrolementBack);
        assertThat(student.getEnrolements()).containsOnly(enrolementBack);
        assertThat(enrolementBack.getStudent()).isEqualTo(student);

        student.removeEnrolement(enrolementBack);
        assertThat(student.getEnrolements()).doesNotContain(enrolementBack);
        assertThat(enrolementBack.getStudent()).isNull();

        student.enrolements(new HashSet<>(Set.of(enrolementBack)));
        assertThat(student.getEnrolements()).containsOnly(enrolementBack);
        assertThat(enrolementBack.getStudent()).isEqualTo(student);

        student.setEnrolements(new HashSet<>());
        assertThat(student.getEnrolements()).doesNotContain(enrolementBack);
        assertThat(enrolementBack.getStudent()).isNull();
    }

    @Test
    void answerTest() throws Exception {
        Student student = getStudentRandomSampleGenerator();
        Answer answerBack = getAnswerRandomSampleGenerator();

        student.addAnswer(answerBack);
        assertThat(student.getAnswers()).containsOnly(answerBack);
        assertThat(answerBack.getStudent()).isEqualTo(student);

        student.removeAnswer(answerBack);
        assertThat(student.getAnswers()).doesNotContain(answerBack);
        assertThat(answerBack.getStudent()).isNull();

        student.answers(new HashSet<>(Set.of(answerBack)));
        assertThat(student.getAnswers()).containsOnly(answerBack);
        assertThat(answerBack.getStudent()).isEqualTo(student);

        student.setAnswers(new HashSet<>());
        assertThat(student.getAnswers()).doesNotContain(answerBack);
        assertThat(answerBack.getStudent()).isNull();
    }

    @Test
    void progressionTest() throws Exception {
        Student student = getStudentRandomSampleGenerator();
        Progression progressionBack = getProgressionRandomSampleGenerator();

        student.addProgression(progressionBack);
        assertThat(student.getProgressions()).containsOnly(progressionBack);
        assertThat(progressionBack.getStudent1()).isEqualTo(student);

        student.removeProgression(progressionBack);
        assertThat(student.getProgressions()).doesNotContain(progressionBack);
        assertThat(progressionBack.getStudent1()).isNull();

        student.progressions(new HashSet<>(Set.of(progressionBack)));
        assertThat(student.getProgressions()).containsOnly(progressionBack);
        assertThat(progressionBack.getStudent1()).isEqualTo(student);

        student.setProgressions(new HashSet<>());
        assertThat(student.getProgressions()).doesNotContain(progressionBack);
        assertThat(progressionBack.getStudent1()).isNull();
    }

    @Test
    void group2Test() throws Exception {
        Student student = getStudentRandomSampleGenerator();
        Group groupBack = getGroupRandomSampleGenerator();

        student.setGroup2(groupBack);
        assertThat(student.getGroup2()).isEqualTo(groupBack);

        student.group2(null);
        assertThat(student.getGroup2()).isNull();
    }

    @Test
    void countryTest() throws Exception {
        Student student = getStudentRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        student.setCountry(countryBack);
        assertThat(student.getCountry()).isEqualTo(countryBack);

        student.country(null);
        assertThat(student.getCountry()).isNull();
    }

    @Test
    void sponsorTest() throws Exception {
        Student student = getStudentRandomSampleGenerator();
        Sponsor sponsorBack = getSponsorRandomSampleGenerator();

        student.addSponsor(sponsorBack);
        assertThat(student.getSponsors()).containsOnly(sponsorBack);
        assertThat(sponsorBack.getStudents()).containsOnly(student);

        student.removeSponsor(sponsorBack);
        assertThat(student.getSponsors()).doesNotContain(sponsorBack);
        assertThat(sponsorBack.getStudents()).doesNotContain(student);

        student.sponsors(new HashSet<>(Set.of(sponsorBack)));
        assertThat(student.getSponsors()).containsOnly(sponsorBack);
        assertThat(sponsorBack.getStudents()).containsOnly(student);

        student.setSponsors(new HashSet<>());
        assertThat(student.getSponsors()).doesNotContain(sponsorBack);
        assertThat(sponsorBack.getStudents()).doesNotContain(student);
    }

    @Test
    void quizCertificateTest() throws Exception {
        Student student = getStudentRandomSampleGenerator();
        QuizCertificate quizCertificateBack = getQuizCertificateRandomSampleGenerator();

        student.addQuizCertificate(quizCertificateBack);
        assertThat(student.getQuizCertificates()).containsOnly(quizCertificateBack);
        assertThat(quizCertificateBack.getStudents()).containsOnly(student);

        student.removeQuizCertificate(quizCertificateBack);
        assertThat(student.getQuizCertificates()).doesNotContain(quizCertificateBack);
        assertThat(quizCertificateBack.getStudents()).doesNotContain(student);

        student.quizCertificates(new HashSet<>(Set.of(quizCertificateBack)));
        assertThat(student.getQuizCertificates()).containsOnly(quizCertificateBack);
        assertThat(quizCertificateBack.getStudents()).containsOnly(student);

        student.setQuizCertificates(new HashSet<>());
        assertThat(student.getQuizCertificates()).doesNotContain(quizCertificateBack);
        assertThat(quizCertificateBack.getStudents()).doesNotContain(student);
    }
}
