package com.wiam.lms.domain;

import static com.wiam.lms.domain.JobTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JobTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Job.class);
        Job job1 = getJobSample1();
        Job job2 = new Job();
        assertThat(job1).isNotEqualTo(job2);

        job2.setId(job1.getId());
        assertThat(job1).isEqualTo(job2);

        job2 = getJobSample2();
        assertThat(job1).isNotEqualTo(job2);
    }

    @Test
    void userCustomTest() throws Exception {
        Job job = getJobRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        job.addUserCustom(userCustomBack);
        assertThat(job.getUserCustoms()).containsOnly(userCustomBack);
        assertThat(userCustomBack.getJob()).isEqualTo(job);

        job.removeUserCustom(userCustomBack);
        assertThat(job.getUserCustoms()).doesNotContain(userCustomBack);
        assertThat(userCustomBack.getJob()).isNull();

        job.userCustoms(new HashSet<>(Set.of(userCustomBack)));
        assertThat(job.getUserCustoms()).containsOnly(userCustomBack);
        assertThat(userCustomBack.getJob()).isEqualTo(job);

        job.setUserCustoms(new HashSet<>());
        assertThat(job.getUserCustoms()).doesNotContain(userCustomBack);
        assertThat(userCustomBack.getJob()).isNull();
    }
}
