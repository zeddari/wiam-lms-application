package com.wiam.lms.domain;

import static com.wiam.lms.domain.SessionTestSamples.*;
import static com.wiam.lms.domain.SessionTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SessionTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SessionType.class);
        SessionType sessionType1 = getSessionTypeSample1();
        SessionType sessionType2 = new SessionType();
        assertThat(sessionType1).isNotEqualTo(sessionType2);

        sessionType2.setId(sessionType1.getId());
        assertThat(sessionType1).isEqualTo(sessionType2);

        sessionType2 = getSessionTypeSample2();
        assertThat(sessionType1).isNotEqualTo(sessionType2);
    }

    @Test
    void sessionTest() throws Exception {
        SessionType sessionType = getSessionTypeRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        sessionType.addSession(sessionBack);
        assertThat(sessionType.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getType()).isEqualTo(sessionType);

        sessionType.removeSession(sessionBack);
        assertThat(sessionType.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getType()).isNull();

        sessionType.sessions(new HashSet<>(Set.of(sessionBack)));
        assertThat(sessionType.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getType()).isEqualTo(sessionType);

        sessionType.setSessions(new HashSet<>());
        assertThat(sessionType.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getType()).isNull();
    }
}
