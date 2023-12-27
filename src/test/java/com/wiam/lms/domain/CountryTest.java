package com.wiam.lms.domain;

import static com.wiam.lms.domain.CountryTestSamples.*;
import static com.wiam.lms.domain.StudentTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CountryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Country.class);
        Country country1 = getCountrySample1();
        Country country2 = new Country();
        assertThat(country1).isNotEqualTo(country2);

        country2.setId(country1.getId());
        assertThat(country1).isEqualTo(country2);

        country2 = getCountrySample2();
        assertThat(country1).isNotEqualTo(country2);
    }

    @Test
    void studentTest() throws Exception {
        Country country = getCountryRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        country.addStudent(studentBack);
        assertThat(country.getStudents()).containsOnly(studentBack);
        assertThat(studentBack.getCountry()).isEqualTo(country);

        country.removeStudent(studentBack);
        assertThat(country.getStudents()).doesNotContain(studentBack);
        assertThat(studentBack.getCountry()).isNull();

        country.students(new HashSet<>(Set.of(studentBack)));
        assertThat(country.getStudents()).containsOnly(studentBack);
        assertThat(studentBack.getCountry()).isEqualTo(country);

        country.setStudents(new HashSet<>());
        assertThat(country.getStudents()).doesNotContain(studentBack);
        assertThat(studentBack.getCountry()).isNull();
    }

    @Test
    void userCustomTest() throws Exception {
        Country country = getCountryRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        country.addUserCustom(userCustomBack);
        assertThat(country.getUserCustoms()).containsOnly(userCustomBack);
        assertThat(userCustomBack.getCountry()).isEqualTo(country);

        country.removeUserCustom(userCustomBack);
        assertThat(country.getUserCustoms()).doesNotContain(userCustomBack);
        assertThat(userCustomBack.getCountry()).isNull();

        country.userCustoms(new HashSet<>(Set.of(userCustomBack)));
        assertThat(country.getUserCustoms()).containsOnly(userCustomBack);
        assertThat(userCustomBack.getCountry()).isEqualTo(country);

        country.setUserCustoms(new HashSet<>());
        assertThat(country.getUserCustoms()).doesNotContain(userCustomBack);
        assertThat(userCustomBack.getCountry()).isNull();
    }
}
