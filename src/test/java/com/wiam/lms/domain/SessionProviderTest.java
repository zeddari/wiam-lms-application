package com.wiam.lms.domain;

import static com.wiam.lms.domain.SessionLinkTestSamples.*;
import static com.wiam.lms.domain.SessionProviderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SessionProviderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SessionProvider.class);
        SessionProvider sessionProvider1 = getSessionProviderSample1();
        SessionProvider sessionProvider2 = new SessionProvider();
        assertThat(sessionProvider1).isNotEqualTo(sessionProvider2);

        sessionProvider2.setId(sessionProvider1.getId());
        assertThat(sessionProvider1).isEqualTo(sessionProvider2);

        sessionProvider2 = getSessionProviderSample2();
        assertThat(sessionProvider1).isNotEqualTo(sessionProvider2);
    }

    @Test
    void sessionLinkTest() throws Exception {
        SessionProvider sessionProvider = getSessionProviderRandomSampleGenerator();
        SessionLink sessionLinkBack = getSessionLinkRandomSampleGenerator();

        sessionProvider.addSessionLink(sessionLinkBack);
        assertThat(sessionProvider.getSessionLinks()).containsOnly(sessionLinkBack);
        assertThat(sessionLinkBack.getProvider()).isEqualTo(sessionProvider);

        sessionProvider.removeSessionLink(sessionLinkBack);
        assertThat(sessionProvider.getSessionLinks()).doesNotContain(sessionLinkBack);
        assertThat(sessionLinkBack.getProvider()).isNull();

        sessionProvider.sessionLinks(new HashSet<>(Set.of(sessionLinkBack)));
        assertThat(sessionProvider.getSessionLinks()).containsOnly(sessionLinkBack);
        assertThat(sessionLinkBack.getProvider()).isEqualTo(sessionProvider);

        sessionProvider.setSessionLinks(new HashSet<>());
        assertThat(sessionProvider.getSessionLinks()).doesNotContain(sessionLinkBack);
        assertThat(sessionLinkBack.getProvider()).isNull();
    }
}
