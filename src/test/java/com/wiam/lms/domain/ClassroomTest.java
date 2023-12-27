package com.wiam.lms.domain;

import static com.wiam.lms.domain.ClassroomTestSamples.*;
import static com.wiam.lms.domain.SessionTestSamples.*;
import static com.wiam.lms.domain.SiteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClassroomTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Classroom.class);
        Classroom classroom1 = getClassroomSample1();
        Classroom classroom2 = new Classroom();
        assertThat(classroom1).isNotEqualTo(classroom2);

        classroom2.setId(classroom1.getId());
        assertThat(classroom1).isEqualTo(classroom2);

        classroom2 = getClassroomSample2();
        assertThat(classroom1).isNotEqualTo(classroom2);
    }

    @Test
    void sessionTest() throws Exception {
        Classroom classroom = getClassroomRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        classroom.addSession(sessionBack);
        assertThat(classroom.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getClassroom()).isEqualTo(classroom);

        classroom.removeSession(sessionBack);
        assertThat(classroom.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getClassroom()).isNull();

        classroom.sessions(new HashSet<>(Set.of(sessionBack)));
        assertThat(classroom.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getClassroom()).isEqualTo(classroom);

        classroom.setSessions(new HashSet<>());
        assertThat(classroom.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getClassroom()).isNull();
    }

    @Test
    void siteTest() throws Exception {
        Classroom classroom = getClassroomRandomSampleGenerator();
        Site siteBack = getSiteRandomSampleGenerator();

        classroom.setSite(siteBack);
        assertThat(classroom.getSite()).isEqualTo(siteBack);

        classroom.site(null);
        assertThat(classroom.getSite()).isNull();
    }
}
