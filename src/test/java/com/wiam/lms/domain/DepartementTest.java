package com.wiam.lms.domain;

import static com.wiam.lms.domain.DepartementTestSamples.*;
import static com.wiam.lms.domain.DepartementTestSamples.*;
import static com.wiam.lms.domain.EmployeeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DepartementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Departement.class);
        Departement departement1 = getDepartementSample1();
        Departement departement2 = new Departement();
        assertThat(departement1).isNotEqualTo(departement2);

        departement2.setId(departement1.getId());
        assertThat(departement1).isEqualTo(departement2);

        departement2 = getDepartementSample2();
        assertThat(departement1).isNotEqualTo(departement2);
    }

    @Test
    void employeeTest() throws Exception {
        Departement departement = getDepartementRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        departement.addEmployee(employeeBack);
        assertThat(departement.getEmployees()).containsOnly(employeeBack);
        assertThat(employeeBack.getDepartement()).isEqualTo(departement);

        departement.removeEmployee(employeeBack);
        assertThat(departement.getEmployees()).doesNotContain(employeeBack);
        assertThat(employeeBack.getDepartement()).isNull();

        departement.employees(new HashSet<>(Set.of(employeeBack)));
        assertThat(departement.getEmployees()).containsOnly(employeeBack);
        assertThat(employeeBack.getDepartement()).isEqualTo(departement);

        departement.setEmployees(new HashSet<>());
        assertThat(departement.getEmployees()).doesNotContain(employeeBack);
        assertThat(employeeBack.getDepartement()).isNull();
    }

    @Test
    void departementTest() throws Exception {
        Departement departement = getDepartementRandomSampleGenerator();
        Departement departementBack = getDepartementRandomSampleGenerator();

        departement.addDepartement(departementBack);
        assertThat(departement.getDepartements()).containsOnly(departementBack);
        assertThat(departementBack.getDepartement1()).isEqualTo(departement);

        departement.removeDepartement(departementBack);
        assertThat(departement.getDepartements()).doesNotContain(departementBack);
        assertThat(departementBack.getDepartement1()).isNull();

        departement.departements(new HashSet<>(Set.of(departementBack)));
        assertThat(departement.getDepartements()).containsOnly(departementBack);
        assertThat(departementBack.getDepartement1()).isEqualTo(departement);

        departement.setDepartements(new HashSet<>());
        assertThat(departement.getDepartements()).doesNotContain(departementBack);
        assertThat(departementBack.getDepartement1()).isNull();
    }

    @Test
    void departement1Test() throws Exception {
        Departement departement = getDepartementRandomSampleGenerator();
        Departement departementBack = getDepartementRandomSampleGenerator();

        departement.setDepartement1(departementBack);
        assertThat(departement.getDepartement1()).isEqualTo(departementBack);

        departement.departement1(null);
        assertThat(departement.getDepartement1()).isNull();
    }
}
