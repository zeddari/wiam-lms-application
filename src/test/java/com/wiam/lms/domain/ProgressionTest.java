package com.wiam.lms.domain;

import static com.wiam.lms.domain.ProgressionModeTestSamples.*;
import static com.wiam.lms.domain.ProgressionTestSamples.*;
import static com.wiam.lms.domain.SessionTestSamples.*;
import static com.wiam.lms.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProgressionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Progression.class);
        Progression progression1 = getProgressionSample1();
        Progression progression2 = new Progression();
        assertThat(progression1).isNotEqualTo(progression2);

        progression2.setId(progression1.getId());
        assertThat(progression1).isEqualTo(progression2);

        progression2 = getProgressionSample2();
        assertThat(progression1).isNotEqualTo(progression2);
    }

    @Test
    void sessionTest() throws Exception {
        Progression progression = getProgressionRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        progression.setSession(sessionBack);
        assertThat(progression.getSession()).isEqualTo(sessionBack);

        progression.session(null);
        assertThat(progression.getSession()).isNull();
    }

    @Test
    void student1Test() throws Exception {
        Progression progression = getProgressionRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        progression.setStudent1(studentBack);
        assertThat(progression.getStudent1()).isEqualTo(studentBack);

        progression.student1(null);
        assertThat(progression.getStudent1()).isNull();
    }

    @Test
    void modeTest() throws Exception {
        Progression progression = getProgressionRandomSampleGenerator();
        ProgressionMode progressionModeBack = getProgressionModeRandomSampleGenerator();

        progression.setMode(progressionModeBack);
        assertThat(progression.getMode()).isEqualTo(progressionModeBack);

        progression.mode(null);
        assertThat(progression.getMode()).isNull();
    }
}
