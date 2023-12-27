package com.wiam.lms.domain;

import static com.wiam.lms.domain.EmployeeTestSamples.*;
import static com.wiam.lms.domain.JobTitleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JobTitleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JobTitle.class);
        JobTitle jobTitle1 = getJobTitleSample1();
        JobTitle jobTitle2 = new JobTitle();
        assertThat(jobTitle1).isNotEqualTo(jobTitle2);

        jobTitle2.setId(jobTitle1.getId());
        assertThat(jobTitle1).isEqualTo(jobTitle2);

        jobTitle2 = getJobTitleSample2();
        assertThat(jobTitle1).isNotEqualTo(jobTitle2);
    }

    @Test
    void employeeTest() throws Exception {
        JobTitle jobTitle = getJobTitleRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        jobTitle.addEmployee(employeeBack);
        assertThat(jobTitle.getEmployees()).containsOnly(employeeBack);
        assertThat(employeeBack.getJob()).isEqualTo(jobTitle);

        jobTitle.removeEmployee(employeeBack);
        assertThat(jobTitle.getEmployees()).doesNotContain(employeeBack);
        assertThat(employeeBack.getJob()).isNull();

        jobTitle.employees(new HashSet<>(Set.of(employeeBack)));
        assertThat(jobTitle.getEmployees()).containsOnly(employeeBack);
        assertThat(employeeBack.getJob()).isEqualTo(jobTitle);

        jobTitle.setEmployees(new HashSet<>());
        assertThat(jobTitle.getEmployees()).doesNotContain(employeeBack);
        assertThat(employeeBack.getJob()).isNull();
    }
}
