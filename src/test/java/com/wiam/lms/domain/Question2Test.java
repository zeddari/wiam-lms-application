package com.wiam.lms.domain;

import static com.wiam.lms.domain.Question2TestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class Question2Test {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Question2.class);
        Question2 question21 = getQuestion2Sample1();
        Question2 question22 = new Question2();
        assertThat(question21).isNotEqualTo(question22);

        question22.setId(question21.getId());
        assertThat(question21).isEqualTo(question22);

        question22 = getQuestion2Sample2();
        assertThat(question21).isNotEqualTo(question22);
    }
}
