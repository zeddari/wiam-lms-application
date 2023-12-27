package com.wiam.lms.domain;

import static com.wiam.lms.domain.CountryTestSamples.*;
import static com.wiam.lms.domain.EmployeeTestSamples.*;
import static com.wiam.lms.domain.ExamTestSamples.*;
import static com.wiam.lms.domain.JobTestSamples.*;
import static com.wiam.lms.domain.LanguageTestSamples.*;
import static com.wiam.lms.domain.ProfessorTestSamples.*;
import static com.wiam.lms.domain.SponsorTestSamples.*;
import static com.wiam.lms.domain.StudentTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserCustomTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserCustom.class);
        UserCustom userCustom1 = getUserCustomSample1();
        UserCustom userCustom2 = new UserCustom();
        assertThat(userCustom1).isNotEqualTo(userCustom2);

        userCustom2.setId(userCustom1.getId());
        assertThat(userCustom1).isEqualTo(userCustom2);

        userCustom2 = getUserCustomSample2();
        assertThat(userCustom1).isNotEqualTo(userCustom2);
    }

    @Test
    void languagesTest() throws Exception {
        UserCustom userCustom = getUserCustomRandomSampleGenerator();
        Language languageBack = getLanguageRandomSampleGenerator();

        userCustom.addLanguages(languageBack);
        assertThat(userCustom.getLanguages()).containsOnly(languageBack);
        assertThat(languageBack.getUserCustom()).isEqualTo(userCustom);

        userCustom.removeLanguages(languageBack);
        assertThat(userCustom.getLanguages()).doesNotContain(languageBack);
        assertThat(languageBack.getUserCustom()).isNull();

        userCustom.languages(new HashSet<>(Set.of(languageBack)));
        assertThat(userCustom.getLanguages()).containsOnly(languageBack);
        assertThat(languageBack.getUserCustom()).isEqualTo(userCustom);

        userCustom.setLanguages(new HashSet<>());
        assertThat(userCustom.getLanguages()).doesNotContain(languageBack);
        assertThat(languageBack.getUserCustom()).isNull();
    }

    @Test
    void countryTest() throws Exception {
        UserCustom userCustom = getUserCustomRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        userCustom.setCountry(countryBack);
        assertThat(userCustom.getCountry()).isEqualTo(countryBack);

        userCustom.country(null);
        assertThat(userCustom.getCountry()).isNull();
    }

    @Test
    void jobTest() throws Exception {
        UserCustom userCustom = getUserCustomRandomSampleGenerator();
        Job jobBack = getJobRandomSampleGenerator();

        userCustom.setJob(jobBack);
        assertThat(userCustom.getJob()).isEqualTo(jobBack);

        userCustom.job(null);
        assertThat(userCustom.getJob()).isNull();
    }

    @Test
    void examTest() throws Exception {
        UserCustom userCustom = getUserCustomRandomSampleGenerator();
        Exam examBack = getExamRandomSampleGenerator();

        userCustom.addExam(examBack);
        assertThat(userCustom.getExams()).containsOnly(examBack);

        userCustom.removeExam(examBack);
        assertThat(userCustom.getExams()).doesNotContain(examBack);

        userCustom.exams(new HashSet<>(Set.of(examBack)));
        assertThat(userCustom.getExams()).containsOnly(examBack);

        userCustom.setExams(new HashSet<>());
        assertThat(userCustom.getExams()).doesNotContain(examBack);
    }

    @Test
    void sponsorTest() throws Exception {
        UserCustom userCustom = getUserCustomRandomSampleGenerator();
        Sponsor sponsorBack = getSponsorRandomSampleGenerator();

        userCustom.setSponsor(sponsorBack);
        assertThat(userCustom.getSponsor()).isEqualTo(sponsorBack);
        assertThat(sponsorBack.getUserCustom()).isEqualTo(userCustom);

        userCustom.sponsor(null);
        assertThat(userCustom.getSponsor()).isNull();
        assertThat(sponsorBack.getUserCustom()).isNull();
    }

    @Test
    void employeeTest() throws Exception {
        UserCustom userCustom = getUserCustomRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        userCustom.setEmployee(employeeBack);
        assertThat(userCustom.getEmployee()).isEqualTo(employeeBack);
        assertThat(employeeBack.getUserCustom()).isEqualTo(userCustom);

        userCustom.employee(null);
        assertThat(userCustom.getEmployee()).isNull();
        assertThat(employeeBack.getUserCustom()).isNull();
    }

    @Test
    void professorTest() throws Exception {
        UserCustom userCustom = getUserCustomRandomSampleGenerator();
        Professor professorBack = getProfessorRandomSampleGenerator();

        userCustom.setProfessor(professorBack);
        assertThat(userCustom.getProfessor()).isEqualTo(professorBack);
        assertThat(professorBack.getUserCustom()).isEqualTo(userCustom);

        userCustom.professor(null);
        assertThat(userCustom.getProfessor()).isNull();
        assertThat(professorBack.getUserCustom()).isNull();
    }

    @Test
    void studentTest() throws Exception {
        UserCustom userCustom = getUserCustomRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        userCustom.setStudent(studentBack);
        assertThat(userCustom.getStudent()).isEqualTo(studentBack);
        assertThat(studentBack.getUserCustom()).isEqualTo(userCustom);

        userCustom.student(null);
        assertThat(userCustom.getStudent()).isNull();
        assertThat(studentBack.getUserCustom()).isNull();
    }
}
