package com.wiam.lms.domain;

import static com.wiam.lms.domain.CourseTestSamples.*;
import static com.wiam.lms.domain.ProfessorTestSamples.*;
import static com.wiam.lms.domain.SessionTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProfessorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Professor.class);
        Professor professor1 = getProfessorSample1();
        Professor professor2 = new Professor();
        assertThat(professor1).isNotEqualTo(professor2);

        professor2.setId(professor1.getId());
        assertThat(professor1).isEqualTo(professor2);

        professor2 = getProfessorSample2();
        assertThat(professor1).isNotEqualTo(professor2);
    }

    @Test
    void userCustomTest() throws Exception {
        Professor professor = getProfessorRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        professor.setUserCustom(userCustomBack);
        assertThat(professor.getUserCustom()).isEqualTo(userCustomBack);

        professor.userCustom(null);
        assertThat(professor.getUserCustom()).isNull();
    }

    @Test
    void courseTest() throws Exception {
        Professor professor = getProfessorRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        professor.addCourse(courseBack);
        assertThat(professor.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getProfessor1()).isEqualTo(professor);

        professor.removeCourse(courseBack);
        assertThat(professor.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getProfessor1()).isNull();

        professor.courses(new HashSet<>(Set.of(courseBack)));
        assertThat(professor.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getProfessor1()).isEqualTo(professor);

        professor.setCourses(new HashSet<>());
        assertThat(professor.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getProfessor1()).isNull();
    }

    @Test
    void sessionTest() throws Exception {
        Professor professor = getProfessorRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        professor.addSession(sessionBack);
        assertThat(professor.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getProfessors()).containsOnly(professor);

        professor.removeSession(sessionBack);
        assertThat(professor.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getProfessors()).doesNotContain(professor);

        professor.sessions(new HashSet<>(Set.of(sessionBack)));
        assertThat(professor.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getProfessors()).containsOnly(professor);

        professor.setSessions(new HashSet<>());
        assertThat(professor.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getProfessors()).doesNotContain(professor);
    }
}
