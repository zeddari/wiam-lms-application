package com.wiam.lms.domain;

import static com.wiam.lms.domain.CityTestSamples.*;
import static com.wiam.lms.domain.SiteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(City.class);
        City city1 = getCitySample1();
        City city2 = new City();
        assertThat(city1).isNotEqualTo(city2);

        city2.setId(city1.getId());
        assertThat(city1).isEqualTo(city2);

        city2 = getCitySample2();
        assertThat(city1).isNotEqualTo(city2);
    }

    @Test
    void siteTest() throws Exception {
        City city = getCityRandomSampleGenerator();
        Site siteBack = getSiteRandomSampleGenerator();

        city.addSite(siteBack);
        assertThat(city.getSites()).containsOnly(siteBack);
        assertThat(siteBack.getCity()).isEqualTo(city);

        city.removeSite(siteBack);
        assertThat(city.getSites()).doesNotContain(siteBack);
        assertThat(siteBack.getCity()).isNull();

        city.sites(new HashSet<>(Set.of(siteBack)));
        assertThat(city.getSites()).containsOnly(siteBack);
        assertThat(siteBack.getCity()).isEqualTo(city);

        city.setSites(new HashSet<>());
        assertThat(city.getSites()).doesNotContain(siteBack);
        assertThat(siteBack.getCity()).isNull();
    }
}
