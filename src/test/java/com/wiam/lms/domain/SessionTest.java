package com.wiam.lms.domain;

import static com.wiam.lms.domain.ClassroomTestSamples.*;
import static com.wiam.lms.domain.EmployeeTestSamples.*;
import static com.wiam.lms.domain.GroupTestSamples.*;
import static com.wiam.lms.domain.PartTestSamples.*;
import static com.wiam.lms.domain.ProfessorTestSamples.*;
import static com.wiam.lms.domain.ProgressionTestSamples.*;
import static com.wiam.lms.domain.QuizCertificateTestSamples.*;
import static com.wiam.lms.domain.SessionJoinModeTestSamples.*;
import static com.wiam.lms.domain.SessionLinkTestSamples.*;
import static com.wiam.lms.domain.SessionModeTestSamples.*;
import static com.wiam.lms.domain.SessionTestSamples.*;
import static com.wiam.lms.domain.SessionTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Session.class);
        Session session1 = getSessionSample1();
        Session session2 = new Session();
        assertThat(session1).isNotEqualTo(session2);

        session2.setId(session1.getId());
        assertThat(session1).isEqualTo(session2);

        session2 = getSessionSample2();
        assertThat(session1).isNotEqualTo(session2);
    }

    @Test
    void progression1Test() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        Progression progressionBack = getProgressionRandomSampleGenerator();

        session.addProgression1(progressionBack);
        assertThat(session.getProgression1s()).containsOnly(progressionBack);
        assertThat(progressionBack.getSession()).isEqualTo(session);

        session.removeProgression1(progressionBack);
        assertThat(session.getProgression1s()).doesNotContain(progressionBack);
        assertThat(progressionBack.getSession()).isNull();

        session.progression1s(new HashSet<>(Set.of(progressionBack)));
        assertThat(session.getProgression1s()).containsOnly(progressionBack);
        assertThat(progressionBack.getSession()).isEqualTo(session);

        session.setProgression1s(new HashSet<>());
        assertThat(session.getProgression1s()).doesNotContain(progressionBack);
        assertThat(progressionBack.getSession()).isNull();
    }

    @Test
    void quizCertificateTest() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        QuizCertificate quizCertificateBack = getQuizCertificateRandomSampleGenerator();

        session.addQuizCertificate(quizCertificateBack);
        assertThat(session.getQuizCertificates()).containsOnly(quizCertificateBack);
        assertThat(quizCertificateBack.getSession()).isEqualTo(session);

        session.removeQuizCertificate(quizCertificateBack);
        assertThat(session.getQuizCertificates()).doesNotContain(quizCertificateBack);
        assertThat(quizCertificateBack.getSession()).isNull();

        session.quizCertificates(new HashSet<>(Set.of(quizCertificateBack)));
        assertThat(session.getQuizCertificates()).containsOnly(quizCertificateBack);
        assertThat(quizCertificateBack.getSession()).isEqualTo(session);

        session.setQuizCertificates(new HashSet<>());
        assertThat(session.getQuizCertificates()).doesNotContain(quizCertificateBack);
        assertThat(quizCertificateBack.getSession()).isNull();
    }

    @Test
    void professorsTest() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        Professor professorBack = getProfessorRandomSampleGenerator();

        session.addProfessors(professorBack);
        assertThat(session.getProfessors()).containsOnly(professorBack);

        session.removeProfessors(professorBack);
        assertThat(session.getProfessors()).doesNotContain(professorBack);

        session.professors(new HashSet<>(Set.of(professorBack)));
        assertThat(session.getProfessors()).containsOnly(professorBack);

        session.setProfessors(new HashSet<>());
        assertThat(session.getProfessors()).doesNotContain(professorBack);
    }

    @Test
    void employeesTest() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        session.addEmployees(employeeBack);
        assertThat(session.getEmployees()).containsOnly(employeeBack);

        session.removeEmployees(employeeBack);
        assertThat(session.getEmployees()).doesNotContain(employeeBack);

        session.employees(new HashSet<>(Set.of(employeeBack)));
        assertThat(session.getEmployees()).containsOnly(employeeBack);

        session.setEmployees(new HashSet<>());
        assertThat(session.getEmployees()).doesNotContain(employeeBack);
    }

    @Test
    void linksTest() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        SessionLink sessionLinkBack = getSessionLinkRandomSampleGenerator();

        session.addLinks(sessionLinkBack);
        assertThat(session.getLinks()).containsOnly(sessionLinkBack);

        session.removeLinks(sessionLinkBack);
        assertThat(session.getLinks()).doesNotContain(sessionLinkBack);

        session.links(new HashSet<>(Set.of(sessionLinkBack)));
        assertThat(session.getLinks()).containsOnly(sessionLinkBack);

        session.setLinks(new HashSet<>());
        assertThat(session.getLinks()).doesNotContain(sessionLinkBack);
    }

    @Test
    void classroomTest() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        Classroom classroomBack = getClassroomRandomSampleGenerator();

        session.setClassroom(classroomBack);
        assertThat(session.getClassroom()).isEqualTo(classroomBack);

        session.classroom(null);
        assertThat(session.getClassroom()).isNull();
    }

    @Test
    void typeTest() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        SessionType sessionTypeBack = getSessionTypeRandomSampleGenerator();

        session.setType(sessionTypeBack);
        assertThat(session.getType()).isEqualTo(sessionTypeBack);

        session.type(null);
        assertThat(session.getType()).isNull();
    }

    @Test
    void modeTest() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        SessionMode sessionModeBack = getSessionModeRandomSampleGenerator();

        session.setMode(sessionModeBack);
        assertThat(session.getMode()).isEqualTo(sessionModeBack);

        session.mode(null);
        assertThat(session.getMode()).isNull();
    }

    @Test
    void partTest() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        Part partBack = getPartRandomSampleGenerator();

        session.setPart(partBack);
        assertThat(session.getPart()).isEqualTo(partBack);

        session.part(null);
        assertThat(session.getPart()).isNull();
    }

    @Test
    void jmodeTest() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        SessionJoinMode sessionJoinModeBack = getSessionJoinModeRandomSampleGenerator();

        session.setJmode(sessionJoinModeBack);
        assertThat(session.getJmode()).isEqualTo(sessionJoinModeBack);

        session.jmode(null);
        assertThat(session.getJmode()).isNull();
    }

    @Test
    void groupTest() throws Exception {
        Session session = getSessionRandomSampleGenerator();
        Group groupBack = getGroupRandomSampleGenerator();

        session.setGroup(groupBack);
        assertThat(session.getGroup()).isEqualTo(groupBack);

        session.group(null);
        assertThat(session.getGroup()).isNull();
    }
}
