package com.wiam.lms.domain;

import static com.wiam.lms.domain.SessionLinkTestSamples.*;
import static com.wiam.lms.domain.SessionProviderTestSamples.*;
import static com.wiam.lms.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SessionLinkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SessionLink.class);
        SessionLink sessionLink1 = getSessionLinkSample1();
        SessionLink sessionLink2 = new SessionLink();
        assertThat(sessionLink1).isNotEqualTo(sessionLink2);

        sessionLink2.setId(sessionLink1.getId());
        assertThat(sessionLink1).isEqualTo(sessionLink2);

        sessionLink2 = getSessionLinkSample2();
        assertThat(sessionLink1).isNotEqualTo(sessionLink2);
    }

    @Test
    void providerTest() throws Exception {
        SessionLink sessionLink = getSessionLinkRandomSampleGenerator();
        SessionProvider sessionProviderBack = getSessionProviderRandomSampleGenerator();

        sessionLink.setProvider(sessionProviderBack);
        assertThat(sessionLink.getProvider()).isEqualTo(sessionProviderBack);

        sessionLink.provider(null);
        assertThat(sessionLink.getProvider()).isNull();
    }

    @Test
    void sessionTest() throws Exception {
        SessionLink sessionLink = getSessionLinkRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        sessionLink.addSession(sessionBack);
        assertThat(sessionLink.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getLinks()).containsOnly(sessionLink);

        sessionLink.removeSession(sessionBack);
        assertThat(sessionLink.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getLinks()).doesNotContain(sessionLink);

        sessionLink.sessions(new HashSet<>(Set.of(sessionBack)));
        assertThat(sessionLink.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getLinks()).containsOnly(sessionLink);

        sessionLink.setSessions(new HashSet<>());
        assertThat(sessionLink.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getLinks()).doesNotContain(sessionLink);
    }
}
