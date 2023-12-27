package com.wiam.lms.domain;

import static com.wiam.lms.domain.LanguageTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LanguageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Language.class);
        Language language1 = getLanguageSample1();
        Language language2 = new Language();
        assertThat(language1).isNotEqualTo(language2);

        language2.setId(language1.getId());
        assertThat(language1).isEqualTo(language2);

        language2 = getLanguageSample2();
        assertThat(language1).isNotEqualTo(language2);
    }

    @Test
    void userCustomTest() throws Exception {
        Language language = getLanguageRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        language.setUserCustom(userCustomBack);
        assertThat(language.getUserCustom()).isEqualTo(userCustomBack);

        language.userCustom(null);
        assertThat(language.getUserCustom()).isNull();
    }
}
