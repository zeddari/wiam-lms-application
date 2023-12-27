package com.wiam.lms.domain;

import static com.wiam.lms.domain.CityTestSamples.*;
import static com.wiam.lms.domain.ClassroomTestSamples.*;
import static com.wiam.lms.domain.SiteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SiteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);
        Site site1 = getSiteSample1();
        Site site2 = new Site();
        assertThat(site1).isNotEqualTo(site2);

        site2.setId(site1.getId());
        assertThat(site1).isEqualTo(site2);

        site2 = getSiteSample2();
        assertThat(site1).isNotEqualTo(site2);
    }

    @Test
    void classroomTest() throws Exception {
        Site site = getSiteRandomSampleGenerator();
        Classroom classroomBack = getClassroomRandomSampleGenerator();

        site.addClassroom(classroomBack);
        assertThat(site.getClassrooms()).containsOnly(classroomBack);
        assertThat(classroomBack.getSite()).isEqualTo(site);

        site.removeClassroom(classroomBack);
        assertThat(site.getClassrooms()).doesNotContain(classroomBack);
        assertThat(classroomBack.getSite()).isNull();

        site.classrooms(new HashSet<>(Set.of(classroomBack)));
        assertThat(site.getClassrooms()).containsOnly(classroomBack);
        assertThat(classroomBack.getSite()).isEqualTo(site);

        site.setClassrooms(new HashSet<>());
        assertThat(site.getClassrooms()).doesNotContain(classroomBack);
        assertThat(classroomBack.getSite()).isNull();
    }

    @Test
    void cityTest() throws Exception {
        Site site = getSiteRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        site.setCity(cityBack);
        assertThat(site.getCity()).isEqualTo(cityBack);

        site.city(null);
        assertThat(site.getCity()).isNull();
    }
}
