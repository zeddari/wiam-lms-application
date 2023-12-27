package com.wiam.lms.domain;

import static com.wiam.lms.domain.Country2TestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class Country2Test {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Country2.class);
        Country2 country21 = getCountry2Sample1();
        Country2 country22 = new Country2();
        assertThat(country21).isNotEqualTo(country22);

        country22.setId(country21.getId());
        assertThat(country21).isEqualTo(country22);

        country22 = getCountry2Sample2();
        assertThat(country21).isNotEqualTo(country22);
    }
}
