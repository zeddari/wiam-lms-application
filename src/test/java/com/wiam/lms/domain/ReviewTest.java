package com.wiam.lms.domain;

import static com.wiam.lms.domain.PartTestSamples.*;
import static com.wiam.lms.domain.ReviewTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Review.class);
        Review review1 = getReviewSample1();
        Review review2 = new Review();
        assertThat(review1).isNotEqualTo(review2);

        review2.setId(review1.getId());
        assertThat(review1).isEqualTo(review2);

        review2 = getReviewSample2();
        assertThat(review1).isNotEqualTo(review2);
    }

    @Test
    void userCustomTest() throws Exception {
        Review review = getReviewRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        review.setUserCustom(userCustomBack);
        assertThat(review.getUserCustom()).isEqualTo(userCustomBack);

        review.userCustom(null);
        assertThat(review.getUserCustom()).isNull();
    }

    @Test
    void courseTest() throws Exception {
        Review review = getReviewRandomSampleGenerator();
        Part partBack = getPartRandomSampleGenerator();

        review.setCourse(partBack);
        assertThat(review.getCourse()).isEqualTo(partBack);

        review.course(null);
        assertThat(review.getCourse()).isNull();
    }
}
