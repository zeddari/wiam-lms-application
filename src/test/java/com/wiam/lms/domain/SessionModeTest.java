package com.wiam.lms.domain;

import static com.wiam.lms.domain.SessionModeTestSamples.*;
import static com.wiam.lms.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SessionModeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SessionMode.class);
        SessionMode sessionMode1 = getSessionModeSample1();
        SessionMode sessionMode2 = new SessionMode();
        assertThat(sessionMode1).isNotEqualTo(sessionMode2);

        sessionMode2.setId(sessionMode1.getId());
        assertThat(sessionMode1).isEqualTo(sessionMode2);

        sessionMode2 = getSessionModeSample2();
        assertThat(sessionMode1).isNotEqualTo(sessionMode2);
    }

    @Test
    void sessionTest() throws Exception {
        SessionMode sessionMode = getSessionModeRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        sessionMode.addSession(sessionBack);
        assertThat(sessionMode.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getMode()).isEqualTo(sessionMode);

        sessionMode.removeSession(sessionBack);
        assertThat(sessionMode.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getMode()).isNull();

        sessionMode.sessions(new HashSet<>(Set.of(sessionBack)));
        assertThat(sessionMode.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getMode()).isEqualTo(sessionMode);

        sessionMode.setSessions(new HashSet<>());
        assertThat(sessionMode.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getMode()).isNull();
    }
}
