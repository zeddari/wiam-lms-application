package com.wiam.lms.domain;

import static com.wiam.lms.domain.QuestionTestSamples.*;
import static com.wiam.lms.domain.QuizTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QuizTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Quiz.class);
        Quiz quiz1 = getQuizSample1();
        Quiz quiz2 = new Quiz();
        assertThat(quiz1).isNotEqualTo(quiz2);

        quiz2.setId(quiz1.getId());
        assertThat(quiz1).isEqualTo(quiz2);

        quiz2 = getQuizSample2();
        assertThat(quiz1).isNotEqualTo(quiz2);
    }

    @Test
    void questionTest() throws Exception {
        Quiz quiz = getQuizRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        quiz.addQuestion(questionBack);
        assertThat(quiz.getQuestions()).containsOnly(questionBack);

        quiz.removeQuestion(questionBack);
        assertThat(quiz.getQuestions()).doesNotContain(questionBack);

        quiz.questions(new HashSet<>(Set.of(questionBack)));
        assertThat(quiz.getQuestions()).containsOnly(questionBack);

        quiz.setQuestions(new HashSet<>());
        assertThat(quiz.getQuestions()).doesNotContain(questionBack);
    }
}
