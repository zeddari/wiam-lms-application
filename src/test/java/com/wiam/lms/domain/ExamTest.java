package com.wiam.lms.domain;

import static com.wiam.lms.domain.ExamTestSamples.*;
import static com.wiam.lms.domain.UserCustomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ExamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Exam.class);
        Exam exam1 = getExamSample1();
        Exam exam2 = new Exam();
        assertThat(exam1).isNotEqualTo(exam2);

        exam2.setId(exam1.getId());
        assertThat(exam1).isEqualTo(exam2);

        exam2 = getExamSample2();
        assertThat(exam1).isNotEqualTo(exam2);
    }

    @Test
    void userCustomTest() throws Exception {
        Exam exam = getExamRandomSampleGenerator();
        UserCustom userCustomBack = getUserCustomRandomSampleGenerator();

        exam.addUserCustom(userCustomBack);
        assertThat(exam.getUserCustoms()).containsOnly(userCustomBack);
        assertThat(userCustomBack.getExams()).containsOnly(exam);

        exam.removeUserCustom(userCustomBack);
        assertThat(exam.getUserCustoms()).doesNotContain(userCustomBack);
        assertThat(userCustomBack.getExams()).doesNotContain(exam);

        exam.userCustoms(new HashSet<>(Set.of(userCustomBack)));
        assertThat(exam.getUserCustoms()).containsOnly(userCustomBack);
        assertThat(userCustomBack.getExams()).containsOnly(exam);

        exam.setUserCustoms(new HashSet<>());
        assertThat(exam.getUserCustoms()).doesNotContain(userCustomBack);
        assertThat(userCustomBack.getExams()).doesNotContain(exam);
    }
}
