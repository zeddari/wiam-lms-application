package com.wiam.lms.domain;

import static com.wiam.lms.domain.CoteryHistoryTestSamples.*;
import static com.wiam.lms.domain.CoteryTestSamples.*;
import static com.wiam.lms.domain.FollowUpTestSamples.*;
import static com.wiam.lms.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CoteryHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CoteryHistory.class);
        CoteryHistory coteryHistory1 = getCoteryHistorySample1();
        CoteryHistory coteryHistory2 = new CoteryHistory();
        assertThat(coteryHistory1).isNotEqualTo(coteryHistory2);

        coteryHistory2.setId(coteryHistory1.getId());
        assertThat(coteryHistory1).isEqualTo(coteryHistory2);

        coteryHistory2 = getCoteryHistorySample2();
        assertThat(coteryHistory1).isNotEqualTo(coteryHistory2);
    }

    @Test
    void followUpTest() throws Exception {
        CoteryHistory coteryHistory = getCoteryHistoryRandomSampleGenerator();
        FollowUp followUpBack = getFollowUpRandomSampleGenerator();

        coteryHistory.setFollowUp(followUpBack);
        assertThat(coteryHistory.getFollowUp()).isEqualTo(followUpBack);
        assertThat(followUpBack.getCoteryHistory()).isEqualTo(coteryHistory);

        coteryHistory.followUp(null);
        assertThat(coteryHistory.getFollowUp()).isNull();
        assertThat(followUpBack.getCoteryHistory()).isNull();
    }

    @Test
    void student2Test() throws Exception {
        CoteryHistory coteryHistory = getCoteryHistoryRandomSampleGenerator();
        Cotery coteryBack = getCoteryRandomSampleGenerator();

        coteryHistory.setStudent2(coteryBack);
        assertThat(coteryHistory.getStudent2()).isEqualTo(coteryBack);

        coteryHistory.student2(null);
        assertThat(coteryHistory.getStudent2()).isNull();
    }

    @Test
    void studentTest() throws Exception {
        CoteryHistory coteryHistory = getCoteryHistoryRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        coteryHistory.setStudent(studentBack);
        assertThat(coteryHistory.getStudent()).isEqualTo(studentBack);

        coteryHistory.student(null);
        assertThat(coteryHistory.getStudent()).isNull();
    }
}
