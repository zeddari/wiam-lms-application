package com.wiam.lms.domain;

import static com.wiam.lms.domain.DepartementTestSamples.*;
import static com.wiam.lms.domain.EmployeeTestSamples.*;
import static com.wiam.lms.domain.JobTitleTestSamples.*;
import static com.wiam.lms.domain.SessionTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employee.class);
        Employee employee1 = getEmployeeSample1();
        Employee employee2 = new Employee();
        assertThat(employee1).isNotEqualTo(employee2);

        employee2.setId(employee1.getId());
        assertThat(employee1).isEqualTo(employee2);

        employee2 = getEmployeeSample2();
        assertThat(employee1).isNotEqualTo(employee2);
    }

    @Test
    void userCustomTest() throws Exception {
        Employee employee = getEmployeeRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        employee.setUserCustom(userCustomBack);
        assertThat(employee.getUserCustom()).isEqualTo(userCustomBack);

        employee.userCustom(null);
        assertThat(employee.getUserCustom()).isNull();
    }

    @Test
    void departementTest() throws Exception {
        Employee employee = getEmployeeRandomSampleGenerator();
        Departement departementBack = getDepartementRandomSampleGenerator();

        employee.setDepartement(departementBack);
        assertThat(employee.getDepartement()).isEqualTo(departementBack);

        employee.departement(null);
        assertThat(employee.getDepartement()).isNull();
    }

    @Test
    void jobTest() throws Exception {
        Employee employee = getEmployeeRandomSampleGenerator();
        JobTitle jobTitleBack = getJobTitleRandomSampleGenerator();

        employee.setJob(jobTitleBack);
        assertThat(employee.getJob()).isEqualTo(jobTitleBack);

        employee.job(null);
        assertThat(employee.getJob()).isNull();
    }

    @Test
    void sessionTest() throws Exception {
        Employee employee = getEmployeeRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        employee.addSession(sessionBack);
        assertThat(employee.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getEmployees()).containsOnly(employee);

        employee.removeSession(sessionBack);
        assertThat(employee.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getEmployees()).doesNotContain(employee);

        employee.sessions(new HashSet<>(Set.of(sessionBack)));
        assertThat(employee.getSessions()).containsOnly(sessionBack);
        assertThat(sessionBack.getEmployees()).containsOnly(employee);

        employee.setSessions(new HashSet<>());
        assertThat(employee.getSessions()).doesNotContain(sessionBack);
        assertThat(sessionBack.getEmployees()).doesNotContain(employee);
    }
}
