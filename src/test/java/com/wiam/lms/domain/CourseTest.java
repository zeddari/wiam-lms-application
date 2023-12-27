package com.wiam.lms.domain;

import static com.wiam.lms.domain.CourseTestSamples.*;
import static com.wiam.lms.domain.EnrolementTestSamples.*;
import static com.wiam.lms.domain.LevelTestSamples.*;
import static com.wiam.lms.domain.PartTestSamples.*;
import static com.wiam.lms.domain.ProfessorTestSamples.*;
import static com.wiam.lms.domain.QuestionTestSamples.*;
import static com.wiam.lms.domain.TopicTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.wiam.lms.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CourseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Course.class);
        Course course1 = getCourseSample1();
        Course course2 = new Course();
        assertThat(course1).isNotEqualTo(course2);

        course2.setId(course1.getId());
        assertThat(course1).isEqualTo(course2);

        course2 = getCourseSample2();
        assertThat(course1).isNotEqualTo(course2);
    }

    @Test
    void partTest() throws Exception {
        Course course = getCourseRandomSampleGenerator();
        Part partBack = getPartRandomSampleGenerator();

        course.addPart(partBack);
        assertThat(course.getParts()).containsOnly(partBack);
        assertThat(partBack.getCourse()).isEqualTo(course);

        course.removePart(partBack);
        assertThat(course.getParts()).doesNotContain(partBack);
        assertThat(partBack.getCourse()).isNull();

        course.parts(new HashSet<>(Set.of(partBack)));
        assertThat(course.getParts()).containsOnly(partBack);
        assertThat(partBack.getCourse()).isEqualTo(course);

        course.setParts(new HashSet<>());
        assertThat(course.getParts()).doesNotContain(partBack);
        assertThat(partBack.getCourse()).isNull();
    }

    @Test
    void enrolementTest() throws Exception {
        Course course = getCourseRandomSampleGenerator();
        Enrolement enrolementBack = getEnrolementRandomSampleGenerator();

        course.addEnrolement(enrolementBack);
        assertThat(course.getEnrolements()).containsOnly(enrolementBack);
        assertThat(enrolementBack.getCourse()).isEqualTo(course);

        course.removeEnrolement(enrolementBack);
        assertThat(course.getEnrolements()).doesNotContain(enrolementBack);
        assertThat(enrolementBack.getCourse()).isNull();

        course.enrolements(new HashSet<>(Set.of(enrolementBack)));
        assertThat(course.getEnrolements()).containsOnly(enrolementBack);
        assertThat(enrolementBack.getCourse()).isEqualTo(course);

        course.setEnrolements(new HashSet<>());
        assertThat(course.getEnrolements()).doesNotContain(enrolementBack);
        assertThat(enrolementBack.getCourse()).isNull();
    }

    @Test
    void questionTest() throws Exception {
        Course course = getCourseRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        course.addQuestion(questionBack);
        assertThat(course.getQuestions()).containsOnly(questionBack);
        assertThat(questionBack.getCourse()).isEqualTo(course);

        course.removeQuestion(questionBack);
        assertThat(course.getQuestions()).doesNotContain(questionBack);
        assertThat(questionBack.getCourse()).isNull();

        course.questions(new HashSet<>(Set.of(questionBack)));
        assertThat(course.getQuestions()).containsOnly(questionBack);
        assertThat(questionBack.getCourse()).isEqualTo(course);

        course.setQuestions(new HashSet<>());
        assertThat(course.getQuestions()).doesNotContain(questionBack);
        assertThat(questionBack.getCourse()).isNull();
    }

    @Test
    void topic1Test() throws Exception {
        Course course = getCourseRandomSampleGenerator();
        Topic topicBack = getTopicRandomSampleGenerator();

        course.setTopic1(topicBack);
        assertThat(course.getTopic1()).isEqualTo(topicBack);

        course.topic1(null);
        assertThat(course.getTopic1()).isNull();
    }

    @Test
    void levelTest() throws Exception {
        Course course = getCourseRandomSampleGenerator();
        Level levelBack = getLevelRandomSampleGenerator();

        course.setLevel(levelBack);
        assertThat(course.getLevel()).isEqualTo(levelBack);

        course.level(null);
        assertThat(course.getLevel()).isNull();
    }

    @Test
    void professor1Test() throws Exception {
        Course course = getCourseRandomSampleGenerator();
        Professor professorBack = getProfessorRandomSampleGenerator();

        course.setProfessor1(professorBack);
        assertThat(course.getProfessor1()).isEqualTo(professorBack);

        course.professor1(null);
        assertThat(course.getProfessor1()).isNull();
    }
}
