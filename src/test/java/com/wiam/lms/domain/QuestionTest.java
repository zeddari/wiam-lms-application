package com.wiam.lms.domain;

import static com.wiam.lms.domain.AnswerTestSamples.*;
import static com.wiam.lms.domain.CourseTestSamples.*;
import static com.wiam.lms.domain.QuestionTestSamples.*;
import static com.wiam.lms.domain.QuizCertificateTestSamples.*;
import static com.wiam.lms.domain.QuizTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Question.class);
        Question question1 = getQuestionSample1();
        Question question2 = new Question();
        assertThat(question1).isNotEqualTo(question2);

        question2.setId(question1.getId());
        assertThat(question1).isEqualTo(question2);

        question2 = getQuestionSample2();
        assertThat(question1).isNotEqualTo(question2);
    }

    @Test
    void answerTest() throws Exception {
        Question question = getQuestionRandomSampleGenerator();
        Answer answerBack = getAnswerRandomSampleGenerator();

        question.addAnswer(answerBack);
        assertThat(question.getAnswers()).containsOnly(answerBack);
        assertThat(answerBack.getQuestion()).isEqualTo(question);

        question.removeAnswer(answerBack);
        assertThat(question.getAnswers()).doesNotContain(answerBack);
        assertThat(answerBack.getQuestion()).isNull();

        question.answers(new HashSet<>(Set.of(answerBack)));
        assertThat(question.getAnswers()).containsOnly(answerBack);
        assertThat(answerBack.getQuestion()).isEqualTo(question);

        question.setAnswers(new HashSet<>());
        assertThat(question.getAnswers()).doesNotContain(answerBack);
        assertThat(answerBack.getQuestion()).isNull();
    }

    @Test
    void courseTest() throws Exception {
        Question question = getQuestionRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        question.setCourse(courseBack);
        assertThat(question.getCourse()).isEqualTo(courseBack);

        question.course(null);
        assertThat(question.getCourse()).isNull();
    }

    @Test
    void quizTest() throws Exception {
        Question question = getQuestionRandomSampleGenerator();
        Quiz quizBack = getQuizRandomSampleGenerator();

        question.addQuiz(quizBack);
        assertThat(question.getQuizzes()).containsOnly(quizBack);
        assertThat(quizBack.getQuestions()).containsOnly(question);

        question.removeQuiz(quizBack);
        assertThat(question.getQuizzes()).doesNotContain(quizBack);
        assertThat(quizBack.getQuestions()).doesNotContain(question);

        question.quizzes(new HashSet<>(Set.of(quizBack)));
        assertThat(question.getQuizzes()).containsOnly(quizBack);
        assertThat(quizBack.getQuestions()).containsOnly(question);

        question.setQuizzes(new HashSet<>());
        assertThat(question.getQuizzes()).doesNotContain(quizBack);
        assertThat(quizBack.getQuestions()).doesNotContain(question);
    }

    @Test
    void quizCertificateTest() throws Exception {
        Question question = getQuestionRandomSampleGenerator();
        QuizCertificate quizCertificateBack = getQuizCertificateRandomSampleGenerator();

        question.addQuizCertificate(quizCertificateBack);
        assertThat(question.getQuizCertificates()).containsOnly(quizCertificateBack);
        assertThat(quizCertificateBack.getQuestions()).containsOnly(question);

        question.removeQuizCertificate(quizCertificateBack);
        assertThat(question.getQuizCertificates()).doesNotContain(quizCertificateBack);
        assertThat(quizCertificateBack.getQuestions()).doesNotContain(question);

        question.quizCertificates(new HashSet<>(Set.of(quizCertificateBack)));
        assertThat(question.getQuizCertificates()).containsOnly(quizCertificateBack);
        assertThat(quizCertificateBack.getQuestions()).containsOnly(question);

        question.setQuizCertificates(new HashSet<>());
        assertThat(question.getQuizCertificates()).doesNotContain(quizCertificateBack);
        assertThat(quizCertificateBack.getQuestions()).doesNotContain(question);
    }
}
