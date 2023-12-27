package com.wiam.lms.domain;

import static com.wiam.lms.domain.SessionJoinModeTestSamples.*;
import static com.wiam.lms.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SessionJoinModeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SessionJoinMode.class);
        SessionJoinMode sessionJoinMode1 = getSessionJoinModeSample1();
        SessionJoinMode sessionJoinMode2 = new SessionJoinMode();
        assertThat(sessionJoinMode1).isNotEqualTo(sessionJoinMode2);

        sessionJoinMode2.setId(sessionJoinMode1.getId());
        assertThat(sessionJoinMode1).isEqualTo(sessionJoinMode2);

        sessionJoinMode2 = getSessionJoinModeSample2();
        assertThat(sessionJoinMode1).isNotEqualTo(sessionJoinMode2);
    }

    @Test
    void sessionTest() throws Exception {
        SessionJoinMode sessionJoinMode = getSessionJoinModeRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        sessionJoinMode.addSession(sessionBack);
        assertThat(sessionJoinMode.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getJmode()).isEqualTo(sessionJoinMode);

        sessionJoinMode.removeSession(sessionBack);
        assertThat(sessionJoinMode.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getJmode()).isNull();

        sessionJoinMode.sessions(new HashSet<>(Set.of(sessionBack)));
        assertThat(sessionJoinMode.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getJmode()).isEqualTo(sessionJoinMode);

        sessionJoinMode.setSessions(new HashSet<>());
        assertThat(sessionJoinMode.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getJmode()).isNull();
    }
}
