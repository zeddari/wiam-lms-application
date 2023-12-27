package com.wiam.lms.domain;

import static com.wiam.lms.domain.CoteryHistoryTestSamples.*;
import static com.wiam.lms.domain.FollowUpTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FollowUpTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FollowUp.class);
        FollowUp followUp1 = getFollowUpSample1();
        FollowUp followUp2 = new FollowUp();
        assertThat(followUp1).isNotEqualTo(followUp2);

        followUp2.setId(followUp1.getId());
        assertThat(followUp1).isEqualTo(followUp2);

        followUp2 = getFollowUpSample2();
        assertThat(followUp1).isNotEqualTo(followUp2);
    }

    @Test
    void coteryHistoryTest() throws Exception {
        FollowUp followUp = getFollowUpRandomSampleGenerator();
        CoteryHistory coteryHistoryBack = getCoteryHistoryRandomSampleGenerator();

        followUp.setCoteryHistory(coteryHistoryBack);
        assertThat(followUp.getCoteryHistory()).isEqualTo(coteryHistoryBack);

        followUp.coteryHistory(null);
        assertThat(followUp.getCoteryHistory()).isNull();
    }
}
